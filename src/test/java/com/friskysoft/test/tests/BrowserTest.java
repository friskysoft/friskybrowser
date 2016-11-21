package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.framework.Element;
import com.friskysoft.test.pages.CarsPage;
import com.friskysoft.test.pages.GooglePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.BrowserType;
import org.testng.annotations.*;

import java.util.concurrent.TimeUnit;
import static org.testng.Assert.*;

public class BrowserTest {

    private Browser browser;

    private static final Log LOGGER = LogFactory.getLog(BrowserTest.class);

    @BeforeClass
    public void setupBrowser() throws Exception {
        browser = Browser.newInstance(BrowserType.PHANTOMJS)
                .setPageLoadTimeout(30, TimeUnit.SECONDS)
                .setImplicitWait(5, TimeUnit.SECONDS);
    }

    @Test
    public void googleSearch() {
        browser.open("https://www.google.com/?complete=0");
        browser.takeScreenshot();

        GooglePage.searchBox.waitToBePresent().sendKeys("git");
        GooglePage.searchButton.waitToBeClickable().click();
        GooglePage.searchResults.waitToBePresent(10);

        int count = GooglePage.searchResults.count();
        assert(count > 0);

        for (Element element : GooglePage.searchResults.getAll()) {
            String text = element.getText();
            LOGGER.info(text);
            assertTrue(text.toLowerCase().contains("git"), "Actual text: <" + text + ">");
        }

    }

    @Test
    public void carsSearch() {

        browser.open("https://www.cars.com");
        browser.takeScreenshot();

        CarsPage.homePage.waitToBeVisible();
        CarsPage.makeDropdown.click().selectByText("Toyota");
        CarsPage.zipInput.type("10001");
        CarsPage.searchSubmit.click();
        CarsPage.searchResultTitle.waitToBeVisible();
        String text = CarsPage.searchResultTitle.getText();
        LOGGER.info(text);
        assertTrue(text.toLowerCase().contains("toyota"), "Actual text: <" + text + ">");

    }

    @AfterMethod
    public void screenshot() {
        browser.takeScreenshot();
    }

    @AfterClass
    public void teardownBrowser() {
        browser.destroy();
    }

}
