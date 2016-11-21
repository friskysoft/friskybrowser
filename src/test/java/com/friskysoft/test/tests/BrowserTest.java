package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.framework.Element;
import com.friskysoft.test.pages.GooglePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.BrowserType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class BrowserTest {

    Browser browser;

    private static final Log LOGGER = LogFactory.getLog(BrowserTest.class);

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
    public void searchTest() {

        GooglePage.searchBox.waitToBePresent().sendKeys("git");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        int count = GooglePage.searchResults.count();
        assert(count > 0);

        for (Element element : GooglePage.searchResults.getAll()) {
            String text = element.getText();
            assert(text.toLowerCase().contains("git"));
        }

    }

    @Test
    public void searchTest2() {

        GooglePage.searchBox.waitToBePresent().sendKeys("selenium");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        int count = GooglePage.searchResults.count();
        assert(count > 0);

        for (Element element : GooglePage.searchResults.getAll()) {
            String text = element.getText();
            assert(text.toLowerCase().contains("selenium"));
        }
    }

    @AfterClass
    public void teardownBrowser() {
        browser.takeScreenshot();
        browser.destroy();
    }

}
