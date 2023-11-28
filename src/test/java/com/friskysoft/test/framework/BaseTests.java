package com.friskysoft.test.framework;

import com.friskysoft.framework.Browser;
import com.friskysoft.framework.utils.GifRecorder;
import com.friskysoft.test.pages.*;
import com.friskysoft.test.utils.image.ImageUploader;
import com.friskysoft.test.utils.image.ImgbbImageUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

public class BaseTests {

    protected Browser browser;
    protected String baseUrl = null;
    protected String loginPath = "/login.html";
    protected String homePath = "/home.html";
    protected String framesPath = "/frames.html";
    protected String overlapPath = "/overlap.html";
    protected String hoverPath = "/hover.html";
    protected String largePagePath = "/largePage.html";

    protected HomePage homePage = new HomePage();
    protected LoginPage loginPage = new LoginPage();
    protected FramesPage framesPage = new FramesPage();
    protected OverlapPage overlapPage = new OverlapPage();
    protected HoverPage hoverPage = new HoverPage();

    protected Logger getLogger() {
        return LoggerFactory.getLogger(this.getClass());
    }

    protected static final ImageUploader imageUploader = new ImgbbImageUploader();

    static {
        java.util.logging.Logger root = java.util.logging.Logger.getLogger("");
        root.setLevel(java.util.logging.Level.WARNING);
        for (Handler handler : root.getHandlers()) {
            handler.setLevel(java.util.logging.Level.WARNING);
        }
    }

    private String browserType() {
        final int choice = (int) (Thread.currentThread().getId() % 2);
        return new String[]{"chrome headless", "firefox headless"}[choice];
    }

    @BeforeClass
    public void beforeClass() {
        final URL resource = this.getClass().getResource("/test-web");
        baseUrl = "file://" + resource.getPath();
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        final String browserType = browserType();
        getLogger().info("Browser: {}", browserType);
        getLogger().info("Running {}.{}", method.getDeclaringClass().getName(), method.getName());
        browser = Browser.newLocalDriver(browserType)
                .setPageLoadTimeout(30, TimeUnit.SECONDS)
                .setImplicitWait(5, TimeUnit.SECONDS)
                .maximize();
        GifRecorder.start();
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        GifRecorder.stopAndSave(result.getMethod().getRealClass().getSimpleName() + "_" + result.getMethod().getMethodName());
        if (result.getStatus() != ITestResult.SUCCESS) {
            try {
                String screenshot = browser.takeScreenshot();
                String screenshotUrl = imageUploader.upload(screenshot);
                getLogger().error("Test failed! Screenshot uploaded at: " + screenshotUrl);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        browser.destroy();
    }

    @AfterSuite
    public void afterSuite() {
        Browser.quitAllWebdriverInstances();
    }
}
