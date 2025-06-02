const { Builder, By, until } = require('selenium-webdriver');
const assert = require('assert');

async function runTest() {
  	const base_url = "https://parabank.parasoft.com/parabank/index.htm";
  	
  	// should be parameterized from command line
  	const username = process.env.TEST_USERNAME || 'john';
  	const password = process.env.TEST_PASSWORD || 'demo';
  	
  	// parameter to run tests headless
	const headless = process.env.TEST_HEADLESS !== 'false'; // default is true  	
  	
  	// number of attempts to refresh accounts table
	let attemptCount = 3;


  	// browser options
  	const chrome = require('selenium-webdriver/chrome');
  	let options = new chrome.Options();
	options.addArguments("--window-size=1920,1080");
	if (headless)
  		options.addArguments("--headless");

  	// driver
  	const driver = await new Builder()
    	.forBrowser('chrome')
    	.setChromeOptions(options)
    	.build();

  	try {
    	
    	// go to the site
    	await driver.get(base_url);
    	const title = await driver.getTitle();
    	assert(title.includes("ParaBank"), "Page title is incorrect");

    	// login
    	await driver.findElement(By.name('username')).sendKeys(username);
    	await driver.findElement(By.name('password')).sendKeys(password);
    	await driver.findElement(By.css('input.button')).click();

    	// wait for account overview table
    	await driver.wait(until.elementLocated(By.id('accountTable')), 10000);
		const overview = await driver.findElement(By.id('showOverview')).getText();
		assert(overview.includes("Accounts Overview"), 
			"Expected 'Accounts Overview' to appear, but got: " + overview);
    	
    	// getting accounts links
    	let accountLinks = await driver.findElements(By.xpath("//td/a"));

    	// click "Accounts Overview" again if there's only one account
    	let attempts = 0;
    	while (accountLinks.length <= 1 && attempts < attemptCount) {
    		console.log("Refreshing the account list...");
      		await driver.findElement(By.linkText('Accounts Overview')).click();
      		await driver.sleep(5000);
      		accountLinks = await driver.findElements(By.xpath("//td/a"));
      		attempts++;
    	}

    	// Loop through accounts
    	for (let i = 0; i < accountLinks.length; i++) {
      	try {
        	const accountId = await accountLinks[i].getText();
        	await accountLinks[i].click();

			// waiting for account id on a page
      		await driver.sleep(5000);
        	let accountDetails = await driver.wait(
          		until.elementLocated(By.id('accountId')), 5000);
        	const textDetails = await driver.findElement(By.id('accountDetails')).getText();
			assert(textDetails.includes(accountId), 
				"Expected " + accountId + " to appear, but got: " + textDetails);
 
        	// getting the last transaction element from the table
        	const transactionRow = await driver.findElement(
          		By.xpath("//table[@id='transactionTable']/tbody/tr[last()]")
        	);
        	const lastTransaction = await transactionRow.getText();
        	console.log('Last transaction found:', lastTransaction);

        	// verify row starts with a valid date
        	assert(/^\d{2}-\d{2}-\d{4}.*/.test(lastTransaction), 
        		"Row does not start with a valid date");

        	// verify there is only one currency value in the string
        	const countValues = (lastTransaction.match(/\$\d+\.\d{2}/g) || []).length;
			assert.strictEqual(countValues, 1, "Unexpected number of currency values");
        	
        	// check the description is not empty
        	const descriptionStart = lastTransaction.indexOf(" ")+ 1;
        	const amountStart = lastTransaction.lastIndexOf('$');
        	const description = lastTransaction.substring(descriptionStart, amountStart).trim();
			assert(description.length > 0, "Description is empty");

        	// exit from loop
        	break;

      	} catch (error) {
        	console.log('Skipping glitchy account');
        	await driver.navigate().back();
        	await driver.wait(until.elementLocated(By.xpath("//td/a")), 5000);
        	accountLinks = await driver.findElements(By.xpath("//td/a")); // re-fetch
      	}
    }

  } catch (error) {
    	console.log("No transactions found. "
					+ "This may be expected for a new or inactive account.");
  } finally {
    await driver.quit();
  }
}

runTest();
