package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.test.utils.ImageUploader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.BrowserType;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.util.concurrent.TimeUnit;

public class _BaseTests {

    Browser browser;

    protected Log getLogger() {
        return LogFactory.getLog(this.getClass());
    }

    @BeforeClass
    public void setupBrowser() throws Exception {
        browser = Browser.newInstance(BrowserType.PHANTOMJS)
                .setPageLoadTimeout(30, TimeUnit.SECONDS)
                .setImplicitWait(5, TimeUnit.SECONDS);
    }

    @AfterMethod
    public void failureScreenshot(ITestResult result) {
        if (result.getStatus() != ITestResult.SUCCESS) {
            try {
                String screenshot = browser.takeScreenshot();
                String screenshotUrl = ImageUploader.upload(screenshot);
                getLogger().error("Test failed! Screenshot uploaded at: " + screenshotUrl);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @AfterClass
    public void teardownBrowser() {
        browser.destroy();
    }

}
