package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.test.pages.GooglePage;
import org.openqa.selenium.remote.BrowserType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class SampleTest {

    private Browser browser;

    @BeforeClass
    public void setupBrowser() throws Exception {
        browser = Browser.newInstance(BrowserType.PHANTOMJS)
                .setPageLoadTimeout(30, TimeUnit.SECONDS)
                .setImplicitWait(5, TimeUnit.SECONDS);
    }

    @BeforeMethod
    public void resetPage() {
        browser.open("https://www.google.com/?complete=0");
        browser.takeScreenshot();
    }

    @Test
    public void sampleTest1() {
        GooglePage.searchBox.waitToBePresent().sendKeys("Selenium");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        String actualText = GooglePage.searchResults.getFirst().getText();
        assertTrue(actualText.contains("Selenium"));

        String actualLink = GooglePage.searchResultLinks.getFirst().getLink();
        assertTrue(actualLink.contains("seleniumhq.org"));
    }

    @Test
    public void sampleTest2() {
        GooglePage.searchBox.waitToBePresent().sendKeys("GitHub");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        String actualText = GooglePage.searchResults.getFirst().getText();
        assertTrue(actualText.contains("GitHub"));

        String actualLink = GooglePage.searchResultLinks.getFirst().getLink();
        assertTrue(actualLink.contains("github.com"));
    }

    @AfterClass
    public void teardownBrowser() {
        browser.takeScreenshot();
        browser.destroy();
    }

}
