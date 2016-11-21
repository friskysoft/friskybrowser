package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.test.pages.GooglePage;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.openqa.selenium.remote.BrowserType;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class SampleTest {

    @Test(enabled = false)
    public void setupBrowser() throws Exception {
        ChromeDriverManager.getInstance().setup();
        Browser browser = Browser.newInstance(BrowserType.CHROME)
                .setPageLoadTimeout(30, TimeUnit.SECONDS)
                .setImplicitWait(5, TimeUnit.SECONDS);

        browser.open("https://www.google.com/?complete=0");
        browser.takeScreenshot();

        GooglePage.searchBox.waitToBePresent().sendKeys("Selenium");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        String actualText = GooglePage.searchResults.getFirst().getText();
        assertTrue(actualText.contains("Selenium"));

        String actualLink = GooglePage.searchResultLinks.getFirst().getLink();
        assertTrue(actualLink.contains("seleniumhq.org"));

        browser.takeScreenshot();
        browser.destroy();
    }

}
