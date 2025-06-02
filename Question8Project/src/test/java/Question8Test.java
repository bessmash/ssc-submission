
import org.junit.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import io.appium.java_client.windows.WindowsDriver;

public class Question8Test {

    @Test
    public void AdditionTest() throws MalformedURLException
    {
        
    	// capabilities
    	DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("app", "Microsoft.WindowsCalculator_8wekyb3d8bbwe!App"); // calc app
        capabilities.setCapability("platformName", "Windows"); // platform
        
        // driver opens session
        WindowsDriver<WebElement> driver = new WindowsDriver<>(URI.create("http://127.0.0.1:4723").toURL(), capabilities);
        System.out.println("Windows Driver Session ID: " + driver.getSessionId());
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        
        
        // 25 + 75 = 100
        driver.findElementByName("Two").click();
        driver.findElementByName("Five").click();       // 25
        	driver.findElementByName("Plus").click();   // +
        driver.findElementByName("Seven").click(); 
        driver.findElementByName("Five").click();       // 75
        	driver.findElementByName("Equals").click(); // =
        
        // 100 == 100
        String total = driver.findElementByAccessibilityId("CalculatorResults")
        		.getText().replace("Display is", "").trim();
        assertEquals("100", total);
        
        driver.quit();
    }

}