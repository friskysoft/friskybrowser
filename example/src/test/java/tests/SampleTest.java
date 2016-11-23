package tests;

import com.friskysoft.framework.Browser;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.BrowserType;
import pages.GooglePage;

import java.util.concurrent.TimeUnit;

public class SampleTest {

    public static Browser browser;

    @Before
    public void setup() {

        // Autoconfigure chromedriver executable
        ChromeDriverManager.getInstance().setup();

        // Instantiate a new webdriver
        browser = Browser.newInstance(BrowserType.CHROME)
                .setPageLoadTimeout(30, TimeUnit.SECONDS)
                .setImplicitWait(5, TimeUnit.SECONDS);
    }

    @Test
    public void searchTest() {

        browser.open("https://www.google.com/?complete=0");
        browser.takeScreenshot();

        GooglePage.searchBox.waitToBePresent().sendKeys("Selenium");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        String actualText = GooglePage.searchResults.getFirst().getText();
        Assert.assertTrue(actualText.contains("Selenium"));

        String actualLink = GooglePage.searchResultLinks.getFirst().getLink();
        Assert.assertTrue(actualLink.contains("seleniumhq.org"));

    }

    @After
    public void teardown() {
        browser.takeScreenshot();

        // Shutdown webdriver
        browser.destroy();
    }

}
