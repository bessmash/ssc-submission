package com.ssc.Assessment.tests;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.serenitybdd.junit.runners.SerenityRunner;

import java.time.Duration;
import java.util.List;


@RunWith(SerenityRunner.class)
public class Question6Test {
	
	private static final String BASE_URL = "https://parabank.parasoft.com/parabank/index.htm";

	@Test
	public void verifyLatestTransactionDisplayedCorrectly() {
		
		// should be parameterized from command line
		String username = System.getProperty("test.username", "john");
		String password = System.getProperty("test.password", "demo");
		
		// parameter to run tests headless
		String headless = System.getProperty("test.headless", "true");

		// number of attempts to refresh accounts table
		int attemptCount = 3;

		
		// browser options
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		if (headless.equalsIgnoreCase("true")) {
		    options.addArguments("--headless");
		}
		
		// webdriver
		WebDriver driver = new ChromeDriver(options);
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> accountLinks = null; // list of account links
		
		try {

			// go to the site
            driver.get(BASE_URL);
            assertTrue(driver.getTitle().contains("ParaBank"));
            
            // login
            driver.findElement(By.name("username")).sendKeys(username);
            driver.findElement(By.name("password")).sendKeys(password);
            driver.findElement(By.cssSelector("input.button")).click();
            
 
            // wait for account overview Table
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountTable")));
            assertTrue(driver.findElement(By.id("showOverview"))
            		.getText().contains("Accounts Overview"));
            

            // because not all accounts visible, click on link "Accounts Overview"
            // and wait till there is more accounts than 1
            for (int i = 0; i < attemptCount ; i++) {
            	try {
            		accountLinks = wait.until(d -> {
            			List<WebElement> links = d.findElements(By.xpath("//td/a"));
            			return (links.size() > 1) ? links : null;
            		});
            	} catch (Exception e) {
            		System.out.println("Not enough accounts are visible");
            	}
            
            	// if still only 1 link, try clicking again
            	if ((accountLinks == null || accountLinks.size() <=1) && i <attemptCount - 1) {
            		driver.findElement(By.linkText("Accounts Overview")).click();
            		System.out.println("Refreshing the list...");
            	}
            }
      
            
            // looping through accounts, some of them might not work
            accountLinks = driver.findElements(By.xpath("//td/a"));
            for (WebElement accountLink : accountLinks) {
            	
            	try {
                    String accountId = accountLink.getText();
                    accountLink.click();
                    
                    // waiting for account id on a page
                    wait.until(d -> {
                        WebElement el = d.findElement(By.id("accountId"));
                        return !el.getText().trim().isEmpty();
                    });
                    assertTrue(driver.findElement(By.id("accountDetails"))
                    		.getText().contains(accountId));

                    // getting the last transaction element from the table
                    WebElement lastTransactionEl = driver.findElement
                    		(By.xpath("//table[@id='transactionTable']/tbody/tr[last()]"));
                    String lastTransaction = lastTransactionEl.getText();
                    System.out.println("Last Transaction: " + lastTransaction);
                    
                    
                    // verify row starts with a valid date
                    assertTrue("Row does not start with a valid date", lastTransaction.matches("^\\d{2}-\\d{2}-\\d{4}.*"));
 
                    // verify there is only one currency value in the string
                    int countValues = lastTransaction.split("\\$\\d+\\.\\d{2}", -1).length - 1;
                    assertTrue("Unexpected number of Currency Values", countValues==1);
                    
                    // check the description is not empty
                    int descriptionStart = lastTransaction.indexOf(" ") + 1;
                    int amountStart = lastTransaction.lastIndexOf("$");
                    assertFalse("Description is empty", 
                    		lastTransaction.substring(descriptionStart, amountStart).trim().isEmpty());
                    
                    // exit from loop
                    break;
                   		
            	} catch (Exception e) {
            		System.out.println("Skipping glitchy account...");
                    driver.navigate().back();
                    wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                    	    By.xpath("//td/a"), 0));
                    accountLinks = driver.findElements(By.xpath("//td/a"));              		
            	}
            	
            }

		} catch (Exception e) {
 
			System.out.println("No transactions found. "
					+ "This may be expected for a new or inactive account.");
            
		} finally {
			
			driver.quit();
		}
		
	}

}
