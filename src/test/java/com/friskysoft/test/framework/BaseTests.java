package com.friskysoft.test.framework;

import com.friskysoft.framework.Browser;
import com.friskysoft.test.pages.FramesPage;
import com.friskysoft.test.pages.HomePage;
import com.friskysoft.test.pages.LoginPage;
import com.friskysoft.test.utils.ImageUploader;
import com.friskysoft.test.utils.TestConstants;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.BrowserType;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.log4testng.Logger;

import java.net.URL;
import java.util.concurrent.TimeUnit;

public class BaseTests {

    protected Browser browser;
    protected String browserType = BrowserType.PHANTOMJS;
    protected String baseUrl = null;
    protected String loginPath = "/login.html";
    protected String homePath = "/home.html";
    protected String framesPath = "/frames.html";

    protected HomePage homePage = new HomePage();
    protected LoginPage loginPage = new LoginPage();
    protected FramesPage framesPage = new FramesPage();

    protected Logger getLogger() {
        return Logger.getLogger(this.getClass());
    }

    @BeforeClass
    public void setupBaseUrl() {
        URL resource = this.getClass().getResource("/test-web");
        baseUrl = "file://" + resource.getPath();
    }

    @BeforeClass
    public void setupBrowser() {
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
