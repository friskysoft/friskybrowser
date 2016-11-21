package com.friskysoft.framework;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Browser implements WebDriver {

    private static ThreadLocal<WebDriver> wrappedThreadLocalDriver = new ThreadLocal<>();
    private static Browser singletonBrowser;
    private static String defaultScreenshotDir = "./screenshots";

    public static final int DEFAULT_IMPLICIT_WAIT = 5;
    public static final int DEFAULT_EXPLICIT_WAIT = 10;

    public static final String CHROMEDRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";
    public static final String GECKODRIVER_SYSTEM_PROPERTY = "webdriver.gecko.driver";

    private static final Log LOGGER = LogFactory.getLog(Browser.class);

    public static WebDriver getWebDriver() {
        return wrappedThreadLocalDriver.get();
    }

    public static void setWebDriver(WebDriver driver) {
        wrappedThreadLocalDriver.set(driver);
    }

    /**
     * Use newInstance() methods instead
     */
    private Browser() {}

    public static Browser newInstance() {
        if (singletonBrowser == null) {
            singletonBrowser = new Browser();
        }
        return singletonBrowser;
    }

    @SuppressWarnings("deprecation")
    public static Browser newInstance(String browserType) {
        WebDriver driver;
        autoFindWebDriverExecutable(browserType);
        DesiredCapabilities capabilities = getDefaultBrowserCapabilities(browserType);
        switch (browserType) {
            case BrowserType.CHROME:
                driver = new ChromeDriver(capabilities);
                break;
            case BrowserType.FIREFOX:
                driver = new FirefoxDriver(capabilities);
                break;
            case BrowserType.SAFARI:
                driver = new SafariDriver(capabilities);
                break;
            case BrowserType.OPERA:
            case BrowserType.OPERA_BLINK:
                driver = new OperaDriver(capabilities);
                break;
            case BrowserType.IE:
            case BrowserType.IEXPLORE:
                driver = new InternetExplorerDriver(capabilities);
                break;
            case BrowserType.EDGE:
                driver = new EdgeDriver(capabilities);
                break;
            case BrowserType.PHANTOMJS:
            default:
                driver = new PhantomJSDriver(capabilities);
                break;
        }
        setWebDriver(driver);
        return newInstance().resize(1500, 1000);
    }

    public static Browser newInstance(WebDriver driver) {
        setWebDriver(driver);
        return newInstance();
    }

    public static Browser newInstance(URL remoteHubUrl, String browserType) {
        WebDriver remoteDriver = new RemoteWebDriver(remoteHubUrl, getDefaultBrowserCapabilities(browserType));
        setWebDriver(remoteDriver);
        return newInstance();
    }

    private static URL getResource(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }

    private static void autoFindWebDriverExecutable(String browserType) {
        URL webDriverLocation;
        switch (browserType) {
            case BrowserType.CHROME:
                webDriverLocation = getResource("webdrivers/chromedriver_" + getPlatformType().name().toLowerCase() + getArchType().getValue());
                if (webDriverLocation != null) {
                    LOGGER.info("chromedriver found at: " + webDriverLocation);
                    System.setProperty(CHROMEDRIVER_SYSTEM_PROPERTY, webDriverLocation.getPath() + "/chromedriver");
                    break;
                }

                if (getPlatformType().equals(PlatformType.MAC)) {
                    webDriverLocation = getResource("webdrivers/chromedriver_macosx");
                    if (webDriverLocation != null) {
                        LOGGER.info("chromedriver found at: " + webDriverLocation);
                        System.setProperty(CHROMEDRIVER_SYSTEM_PROPERTY, webDriverLocation.getPath() + "/chromedriver");
                        break;
                    }
                }

                webDriverLocation = getResource("chromedriver");
                if (webDriverLocation != null) {
                    LOGGER.info("chromedriver found at: " + webDriverLocation);
                    System.setProperty(CHROMEDRIVER_SYSTEM_PROPERTY, webDriverLocation.getPath());
                    break;
                }
                break;

            case BrowserType.FIREFOX:
                webDriverLocation = getResource("webdrivers/geckodriver_" + getPlatformType().name().toLowerCase() + getArchType().getValue());
                if (webDriverLocation != null) {
                    LOGGER.info("geckodriver found at: " + webDriverLocation);
                    System.setProperty(GECKODRIVER_SYSTEM_PROPERTY, webDriverLocation.getPath() + "/geckodriver");
                    break;
                }

                if (getPlatformType().equals(PlatformType.MAC)) {
                    webDriverLocation = getResource("webdrivers/geckodriver_macos");
                    if (webDriverLocation != null) {
                        LOGGER.info("geckodriver found at: " + webDriverLocation);
                        System.setProperty(GECKODRIVER_SYSTEM_PROPERTY, webDriverLocation.getPath() + "/geckodriver");
                        break;
                    }
                }

                webDriverLocation = getResource("geckodriver");
                if (webDriverLocation != null) {
                    LOGGER.info("geckodriver found at: " + webDriverLocation);
                    System.setProperty(GECKODRIVER_SYSTEM_PROPERTY, webDriverLocation.getPath());
                    break;
                }
                break;
        }
    }

    @SuppressWarnings("deprecation")
    public static DesiredCapabilities getDefaultBrowserCapabilities(String browserType) {
        switch (browserType) {
            case BrowserType.CHROME:
                return DesiredCapabilities.chrome();
            case BrowserType.FIREFOX:
                return DesiredCapabilities.firefox();
            case BrowserType.SAFARI:
                return DesiredCapabilities.safari();
            case BrowserType.OPERA:
            case BrowserType.OPERA_BLINK:
                return DesiredCapabilities.operaBlink();
            case BrowserType.IE:
            case BrowserType.IEXPLORE:
                return DesiredCapabilities.internetExplorer();
            default:
                return DesiredCapabilities.phantomjs();
        }
    }

    public enum ArchType {
        X86(32), X64(64);

        ArchType(int value) {
            this.value = value;
        }

        int value;

        public int getValue() {
            return value;
        }
    }

    public enum PlatformType {
        WINDOWS, MAC, LINUX
    }

    private static PlatformType getPlatformType() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return PlatformType.MAC;
        } else if (os.contains("linux")) {
            return PlatformType.LINUX;
        } else if (os.contains("win")) {
            return PlatformType.WINDOWS;
        } else {
            return PlatformType.LINUX;
        }
    }

    private static ArchType getArchType() {
        boolean is64bit;
        if (System.getProperty("os.name").contains("Windows")) {
            is64bit = (System.getenv("ProgramFiles(x86)") != null);
        } else {
            is64bit = (System.getProperty("os.arch").contains("64"));
        }
        return is64bit ? ArchType.X64 : ArchType.X86;
    }

    @Override
    public void get(String url) {
        LOGGER.info("Opening page at url: " + url);
        getWebDriver().get(url);
    }

    @Override
    public String getCurrentUrl() {
        return getWebDriver().getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return getWebDriver().getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return getWebDriver().findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return getWebDriver().findElement(by);
    }

    @Override
    public String getPageSource() {
        return getWebDriver().getPageSource();
    }

    @Override
    public void close() {
        getWebDriver().close();
    }

    @Override
    public void quit() {
        getWebDriver().quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return getWebDriver().getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return getWebDriver().getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return getWebDriver().switchTo();
    }

    @Override
    public Navigation navigate() {
        return getWebDriver().navigate();
    }

    @Override
    public Options manage() {
        return getWebDriver().manage();
    }

    public Browser resize(int width, int height) {
        getWebDriver().manage().window().setPosition(new Point(0,0));
        getWebDriver().manage().window().setSize(new Dimension(width, height));
        return this;
    }

    public void destroy() {
        try {
            getWebDriver().close();
        } catch (Exception ignore) {}
        try {
            getWebDriver().quit();
        } catch (Exception ignore) {}
        setWebDriver(null);
    }

    public Browser open(String url) {
        this.get(url);
        return this;
    }

    public Browser sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            // ignore
        }
        return this;
    }

    public Browser wait(int time, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(time));
        } catch (InterruptedException ex) {
            // ignore
        }
        return this;
    }

    public Browser setDefaultScreenshotDir(String defaultScreenshotDir) {
        Browser.defaultScreenshotDir = defaultScreenshotDir;
        return this;
    }

    public Browser takeScreenshot() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[2].getMethodName();
        String className = stackTraceElements[2].getClassName();
        String[] classNameSplit = className.split("\\.");
        className = classNameSplit[classNameSplit.length-1];

        DateFormat format = new SimpleDateFormat("YYYYMMdd_HHmmss");
        String title = getTitle().replaceAll("[^A-Za-z0-9]", "_");
        return takeScreenshot(String.format(defaultScreenshotDir + "/screenshot_%s_%s_%s_%s.png",
                format.format(new Date()), className, methodName, title));
    }

    public Browser takeScreenshot(String filepath) {
        try {
            File scrFile = ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(filepath));
        } catch (IOException ioex) {
            ioex.printStackTrace();
            // ignore
        }
        return this;
    }

    public Browser setPageLoadTimeout(int time, TimeUnit unit) {
        getWebDriver().manage().timeouts().pageLoadTimeout(time, unit);
        return this;
    }

    public Browser setScriptTimeout(int time, TimeUnit unit) {
        getWebDriver().manage().timeouts().setScriptTimeout(time, unit);
        return this;
    }

    public Browser setImplicitWait(int time, TimeUnit unit) {
        getWebDriver().manage().timeouts().implicitlyWait(time, unit);
        return this;
    }

    public Browser waitForElementToBePresent(By by) {
        return waitForElementToBePresent(by, DEFAULT_EXPLICIT_WAIT);
    }

    public Browser waitForElementToBePresent(By by, int timeOutInSeconds) {
        new WebDriverWait(getWebDriver(), timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(by));
        return this;
    }

    public Browser waitForElementToBeClickable(By by) {
        return waitForElementToBeClickable(by, DEFAULT_EXPLICIT_WAIT);
    }

    public Browser waitForElementToBeClickable(By by, int timeOutInSeconds) {
        new WebDriverWait(getWebDriver(), timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(by));
        return this;
    }

}