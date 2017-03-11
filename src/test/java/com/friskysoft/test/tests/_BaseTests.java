package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.test.utils.ImageUploader;
import com.friskysoft.test.utils.TestConstants;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.BrowserType;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class _BaseTests {

    Browser browser;
    String browserType = BrowserType.PHANTOMJS;
    String baseUrl = null;
    String loginPath = "/login.html";
    String homePath = "/home.html";

    protected Log getLogger() {
        return LogFactory.getLog(this.getClass());
    }

    @BeforeClass
    public void setupBaseUrl() throws Exception {
        URL resource = this.getClass().getResource("/test-web");
        baseUrl = "file://" + resource.getPath();
    }

    @BeforeClass
    public void setupBrowser() throws Exception {
        if (browserType.equals(BrowserType.PHANTOMJS) && StringUtils.isBlank(System.getProperty(TestConstants.PHANTOMJS_SYSTEM_PROPERTY))) {
            PhantomJsDriverManager.getInstance().setup();
        }
        if (browserType.equals(BrowserType.CHROME) && StringUtils.isBlank(System.getProperty(TestConstants.CHROMEDRIVER_SYSTEM_PROPERTY))) {
            ChromeDriverManager.getInstance().setup();
        }
        browser = Browser.newInstance(browserType)
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
