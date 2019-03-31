package com.friskysoft.framework;

import io.github.bonigarcia.wdm.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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

    public static final int DEFAULT_IMPLICIT_WAIT = 10;
    public static final int DEFAULT_EXPLICIT_WAIT = 10;
    public static final int DEFAULT_PAGELOAD_WAIT = 60;

    public static final String CHROMEDRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";
    public static final String GECKODRIVER_SYSTEM_PROPERTY = "webdriver.gecko.driver";

    private static final Logger LOGGER = LoggerFactory.getLogger(Browser.class);

    public static WebDriver driver() {
        WebDriver driver = wrappedThreadLocalDriver.get();
        if (driver == null) {
            LOGGER.warn("ThreadLocal driver is null, did you forget to call setWebDriver(driver)?");
        }
        return driver;
    }

    public static Browser setWebDriver(WebDriver driver) {
        wrappedThreadLocalDriver.set(driver);
        return getInstance();
    }

    /**
     * Use newInstance() methods instead
     */
    private Browser() {}

    public static Browser getInstance() {
        if (singletonBrowser == null) {
            singletonBrowser = new Browser();
        }
        return singletonBrowser;
    }

    @SuppressWarnings("deprecation")
    public static Browser newLocalDriver(String browserType) {
        WebDriver driver;
        Capabilities capabilities = getDefaultBrowserCapabilities(browserType);
        switch (browserType) {
            case BrowserType.CHROME:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(capabilities);
                break;
            case BrowserType.FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver(capabilities);
                break;
            case BrowserType.SAFARI:
                driver = new SafariDriver(capabilities);
                break;
            case BrowserType.OPERA:
            case BrowserType.OPERA_BLINK:
                WebDriverManager.operadriver().setup();
                driver = new OperaDriver(capabilities);
                break;
            case BrowserType.IE:
            case BrowserType.IEXPLORE:
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver(capabilities);
                break;
            case BrowserType.EDGE:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver(capabilities);
                break;
            case BrowserType.HTMLUNIT:
            case BrowserType.PHANTOMJS:
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless", "--disable-gpu");
                driver = new ChromeDriver(chromeOptions);
                break;
        }
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(DEFAULT_PAGELOAD_WAIT, TimeUnit.SECONDS);
        return setWebDriver(driver).fullscreen();
    }

    public static Browser newRemoteDriver(String remoteHubUrl, String browserType) {
        URL url; 
        try {
            url = new URL(remoteHubUrl);
            return newRemoteDriver(url, browserType);
        } catch (MalformedURLException ex) {
            throw new AssertionError("Invalid remote hub url: " + remoteHubUrl);
        }
    }

    public static Browser newRemoteDriver(URL remoteHubUrl, String browserType) {
        WebDriver remoteDriver = new RemoteWebDriver(remoteHubUrl, getDefaultBrowserCapabilities(browserType));
        return setWebDriver(remoteDriver);
    }

    private static Capabilities getDefaultBrowserCapabilities(String browserType) {
        switch (browserType) {
            case BrowserType.FIREFOX:
                return new FirefoxOptions();
            case BrowserType.SAFARI:
                return new SafariOptions();
            case BrowserType.OPERA:
            case BrowserType.OPERA_BLINK:
                return new OperaOptions();
            case BrowserType.IE:
            case BrowserType.IEXPLORE:
                return new InternetExplorerOptions();
            case BrowserType.EDGE:
                return new EdgeOptions();
            case BrowserType.ANDROID:
                return DesiredCapabilities.android();
            case BrowserType.IPHONE:
                return DesiredCapabilities.iphone();
            case BrowserType.IPAD:
                return DesiredCapabilities.ipad();
            case BrowserType.HTMLUNIT:
            case BrowserType.PHANTOMJS:
            case BrowserType.CHROME:
            default:
                return new ChromeOptions();
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

    public void refresh() {
        LOGGER.info("Refreshing page with url: " + getCurrentUrl());
        driver().navigate().refresh();
    }

    public void back() {
        LOGGER.info("Navigating back");
        driver().navigate().back();
    }

    public void forward() {
        LOGGER.info("Navigating forward");
        driver().navigate().forward();
    }

    @Override
    public void get(String url) {
        LOGGER.info("Opening page at url: " + url);
        driver().get(url);
    }

    @Override
    public String getCurrentUrl() {
        return driver().getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return driver().getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return driver().findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return driver().findElement(by);
    }

    @Override
    public String getPageSource() {
        return driver().getPageSource();
    }

    @Override
    public void close() {
        if (driver() != null) {
            try {
                driver().close();
            } catch (Exception ex) {
                LOGGER.warn("close() method threw an exception: " + ex.getMessage());
            }
        } else {
            LOGGER.warn("close() method was invoked on a null webdriver object");
        }
    }

    @Override
    public void quit() {
        if (driver() != null) {
            try {
                driver().quit();
            } catch (Exception ex) {
                LOGGER.warn("quit() method threw an exception: " + ex.getMessage());
            }
        } else {
            LOGGER.warn("quit() method was invoked on a null webdriver object");
        }
    }

    @Override
    public Set<String> getWindowHandles() {
        return driver().getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return driver().getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return driver().switchTo();
    }

    @Override
    public Navigation navigate() {
        return driver().navigate();
    }

    @Override
    public Options manage() {
        return driver().manage();
    }

    public Browser fullscreen() {
        try {
            driver().manage().window().fullscreen();
        } catch (Exception ex1) {
            try {
                int w = Integer.parseInt(executeScript("return screen.width").toString());
                int h = Integer.parseInt(executeScript("return screen.height").toString());
                resize(w, h);
            } catch (Exception ex2) {
                LOGGER.warn(String.format("Fullscreen failed with errors <%s> and <%s> ", ex1.getMessage(), ex2.getMessage()));
            }
        }
        return this;
    }

    public Browser resize(int width, int height) {
        try {
            driver().manage().window().setPosition(new Point(0, 0));
            driver().manage().window().setSize(new Dimension(width, height));
        } catch (Exception ex) {
            LOGGER.warn(String.format("Resize failed with error <%s>", ex.getMessage()));
        }
        return this;
    }

    public Actions getActions() {
        return new Actions(driver());
    }

    public JavascriptExecutor getJavascriptExecutor() {
        return (JavascriptExecutor)(driver());
    }

    public Object executeScript(String script, Object... args) {
        return getJavascriptExecutor().executeScript(script, args);
    }

    public Object executeAsyncScript(String script, Object... args) {
        return getJavascriptExecutor().executeAsyncScript(script, args);
    }

    public Object injectJQuery() {
        return injectJQuery("3.3.1");
    }

    public Object injectJQuery(String version) {
        return executeAsyncScript(String.format(Utilities.JQUERY_LOADER_SCRIPT, version));
    }

    public void destroy() {
        try {
            driver().close();
        } catch (Exception ignore) {}
        try {
            driver().quit();
        } catch (Exception ignore) {}
        setWebDriver(null);
    }

    public Browser open(String url) {
        this.get(url);
        return this;
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    public static void sleep(int time, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(time));
        } catch (InterruptedException ex) {
            // ignore
        }
    }

    public Browser setDefaultScreenshotDir(String defaultScreenshotDir) {
        Browser.defaultScreenshotDir = defaultScreenshotDir;
        return this;
    }

    public String takeScreenshot() {
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

    public String takeScreenshot(String filepath) {
        try {
            File scrFile = ((TakesScreenshot) driver()).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(filepath));
            return new File(filepath).getAbsolutePath();
        } catch (IOException ioex) {
            ioex.printStackTrace();
            return null;
        }
    }

    public Browser setPageLoadTimeout(int time, TimeUnit unit) {
        driver().manage().timeouts().pageLoadTimeout(time, unit);
        return this;
    }

    public Browser setScriptTimeout(int time, TimeUnit unit) {
        driver().manage().timeouts().setScriptTimeout(time, unit);
        return this;
    }

    public Browser setImplicitWait(int time, TimeUnit unit) {
        driver().manage().timeouts().implicitlyWait(time, unit);
        return this;
    }

    public Browser waitForElementToBePresent(By by) {
        return waitForElementToBePresent(by, DEFAULT_EXPLICIT_WAIT);
    }

    public Browser waitForElementToBePresent(By by, int timeOutInSeconds) {
        new WebDriverWait(driver(), timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(by));
        return this;
    }

    public Browser waitForElementToBeClickable(By by) {
        return waitForElementToBeClickable(by, DEFAULT_EXPLICIT_WAIT);
    }

    public Browser waitForElementToBeClickable(By by, int timeOutInSeconds) {
        new WebDriverWait(driver(), timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(by));
        return this;
    }

    public Browser switchToDefaultContent() {
        driver().switchTo().defaultContent();
        return this;
    }

    public Browser switchToFrame(Element iframeElement) {
        driver().switchTo().frame(iframeElement.getWebElement());
        return this;
    }

    public Browser switchToParent() {
        driver().switchTo().parentFrame();
        return this;
    }

    public Browser dismissAlertIfExists() {
        try {
            dismissAlert();
        } catch (Exception ex) {
            //ignore
        }
        return this;
    }

    public Browser dismissAlert() {
        alert().dismiss();
        return this;
    }

    public Browser acceptAlert() {
        alert().accept();
        return this;
    }

    public Browser acceptAlertIfExists() {
        try {
            alert().accept();
        } catch (Exception ex) {
            //ignore
        }
        return this;
    }

    public String getAlertText() {
        return alert().getText();
    }

    public Alert alert() {
        return driver().switchTo().alert();
    }

    public Browser switchToTopWindow() {
        for (String name : driver().getWindowHandles()) {
            driver().switchTo().window(name);
        }
        return this;
    }
}