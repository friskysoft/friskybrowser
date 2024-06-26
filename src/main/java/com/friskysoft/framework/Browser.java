package com.friskysoft.framework;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.friskysoft.framework.utils.Utilities;
import io.github.bonigarcia.wdm.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Browser implements WebDriver {

    private static final ThreadLocal<WebDriver> wrappedThreadLocalDriver = new ThreadLocal<>();
    private static final Set<WebDriver> allWebdriverInstances = new HashSet<>();
    private static Browser singletonBrowser;
    private static String defaultScreenshotDir;
    private static String defaultVideoDir;

    static {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH':'mm':'ss");
        defaultScreenshotDir = "./screenshots/" + format.format(new Date());
        defaultVideoDir = "./video/" + format.format(new Date());
    }

    public static final int DEFAULT_IMPLICIT_WAIT = 10;
    public static final int DEFAULT_EXPLICIT_WAIT = 10;
    public static final int DEFAULT_PAGELOAD_WAIT = 60;

    public static final String CHROMEDRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";
    public static final String GECKODRIVER_SYSTEM_PROPERTY = "webdriver.gecko.driver";

    private static final Logger LOGGER = LoggerFactory.getLogger(Browser.class);

    public static WebDriver driver() {
        WebDriver driver = wrappedThreadLocalDriver.get();
        if (driver == null) {
            LOGGER.trace("ThreadLocal driver is null, did you forget to call setWebDriver(driver)?");
        }
        return driver;
    }

    public static Browser setWebDriver(WebDriver driver) {
        wrappedThreadLocalDriver.set(driver);
        if (driver != null) {
            allWebdriverInstances.add(driver);
        }
        return getInstance();
    }

    /**
     * Use newInstance() methods instead
     */
    private Browser() {
    }

    public static Browser getInstance() {
        if (singletonBrowser == null) {
            singletonBrowser = new Browser();
        }
        return singletonBrowser;
    }

    public static Set<WebDriver> getAllWebdriverInstances() {
        return allWebdriverInstances;
    }

    public static Browser newLocalDriver(String browserName) {
        return newLocalDriver(browserName, false);
    }

    @SuppressWarnings("deprecation")
    public static Browser newLocalDriver(String browserName, boolean hideChromeAutomationFeatures) {
        WebDriver driver;
        BrowserType browserType = BrowserType.from(browserName);
        MutableCapabilities options = getDefaultBrowserCapabilities(browserType);
        if (hideChromeAutomationFeatures && options instanceof ChromeOptions) {
            ((ChromeOptions) options).setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
            ((ChromeOptions) options).addArguments("--disable-blink-features=AutomationControlled");
        }
        switch (browserType) {
            case SAFARI:
                WebDriverManager.safaridriver().setup();
                driver = new SafariDriver((SafariOptions) options);
                break;
            case IE:
                WebDriverManager.iedriver().setup();
                driver = new InternetExplorerDriver((InternetExplorerOptions) options);
                break;
            case EDGE:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver((EdgeOptions) options);
                break;
            case FIREFOX:
            case FIREFOX_HEADLESS:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver((FirefoxOptions) options);
                break;
            case CHROME:
            case CHROME_HEADLESS:
            default:
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver((ChromeOptions) options);
                break;
        }
        driver.manage().timeouts().implicitlyWait(DEFAULT_IMPLICIT_WAIT, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(DEFAULT_PAGELOAD_WAIT, TimeUnit.SECONDS);
        return setWebDriver(driver);
    }

    public static Browser newRemoteDriver(String remoteHubUrl, String browserType) {
        URL url;
        try {
            url = new URL(remoteHubUrl);
            return newRemoteDriver(url, browserType);
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid remote hub url: " + remoteHubUrl);
        }
    }

    public static Browser newRemoteDriver(URL remoteHubUrl, String browserType) {
        return newRemoteDriver(remoteHubUrl, getDefaultBrowserCapabilities(browserType));
    }

    public static Browser newRemoteDriver(URL remoteHubUrl, Capabilities capabilities) {
        WebDriver remoteDriver = new RemoteWebDriver(remoteHubUrl, capabilities);
        return setWebDriver(remoteDriver);
    }

    public static MutableCapabilities getDefaultBrowserCapabilities(String browserType) {
        return getDefaultBrowserCapabilities(BrowserType.from(browserType));
    }

    public static MutableCapabilities getDefaultBrowserCapabilities(BrowserType browserType) {
        switch (browserType) {
            case FIREFOX:
                return new FirefoxOptions();
            case FIREFOX_HEADLESS:
                FirefoxOptions ffOptions = new FirefoxOptions();
                ffOptions.addArguments("--headless");
                ffOptions.setLogLevel(FirefoxDriverLogLevel.FATAL);
                return ffOptions;
            case SAFARI:
                return new SafariOptions();
            case IE:
                return new InternetExplorerOptions();
            case EDGE:
                return new EdgeOptions();
            case CHROME_HEADLESS:
                ChromeOptions chromeOptions = getDefaultChromeOptions();
                chromeOptions.addArguments("--headless", "--disable-gpu");
                return chromeOptions;
            case UNKNOWN:
            case CHROME:
            default:
                return getDefaultChromeOptions();
        }
    }

    public static ChromeOptions getDefaultChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.setPageLoadTimeout(Duration.ofSeconds(DEFAULT_PAGELOAD_WAIT));
        System.setProperty("webdriver.chrome.args", "--disable-logging");
        System.setProperty("webdriver.chrome.silentOutput", "true");
        return options;
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

    public static PlatformType getPlatformType() {
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

    public static ArchType getArchType() {
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

    public static void closeAllWebdriverInstances() {
        allWebdriverInstances.forEach(Browser::close);
    }

    public static void quitAllWebdriverInstances() {
        allWebdriverInstances.forEach(Browser::quit);
    }

    public static void close(WebDriver driver) {
        if (driver != null) {
            try {
                driver.close();
            } catch (Exception ex) {
                LOGGER.warn("close() method threw an exception: " + ex.getMessage());
            }
        } else {
            LOGGER.trace("close() method was invoked on a null webdriver object");
        }
    }

    public static void quit(WebDriver driver) {
        if (driver() != null) {
            try {
                driver().quit();
            } catch (Exception ex) {
                LOGGER.warn("quit() method threw an exception: " + ex.getMessage());
            }
        } else {
            LOGGER.trace("quit() method was invoked on a null webdriver object");
        }
    }

    @Override
    public void close() {
        close(driver());
    }

    @Override
    public void quit() {
        quit(driver());
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

    public Browser maximize() {
        moveWindow(0, 0);
        try {
            driver().manage().window().maximize();
        } catch (Exception ex1) {
            try {
                int w = Integer.parseInt(executeScript("return screen.width").toString());
                int h = Integer.parseInt(executeScript("return screen.height").toString());
                resize(w, h);
            } catch (Exception ex2) {
                LOGGER.warn(String.format("Browser maximize failed with errors <%s> and <%s> ", ex1.getMessage(), ex2.getMessage()));
            }
        }
        return this;
    }

    public Browser moveToCenter() {
        try {
            int screenWidth = Integer.parseInt(executeScript("return screen.width").toString());
            int screenHeight = Integer.parseInt(executeScript("return screen.height").toString());
            int windowWidth = driver().manage().window().getSize().getWidth();
            int windowHeight = driver().manage().window().getSize().getHeight();
            moveWindow(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);
        } catch (Exception ex1) {
            LOGGER.warn(String.format("Browser moveToCenter failed with error <%s>", ex1.getMessage()));
        }
        return this;
    }

    public Browser resize(int width, int height) {
        try {
            driver().manage().window().setPosition(new Point(0, 0));
            driver().manage().window().setSize(new Dimension(width, height));
        } catch (Exception ex) {
            LOGGER.warn(String.format("Browser resize failed with error <%s>", ex.getMessage()));
        }
        return this;
    }

    public Browser moveWindow(int x, int y) {
        try {
            driver().manage().window().setPosition(new Point(x, y));
        } catch (Exception ex) {
            LOGGER.warn(String.format("Browser moveWindow failed with error <%s>", ex.getMessage()));
        }
        return this;
    }

    public Browser minimize() {
        try {
            driver().manage().window().setPosition(new Point(0, 10000));
        } catch (Exception ex) {
            LOGGER.warn(String.format("Browser resize failed with error <%s>", ex.getMessage()));
        }
        return this;
    }

    public Actions getActions() {
        return new Actions(driver());
    }

    public JavascriptExecutor getJavascriptExecutor() {
        return (JavascriptExecutor) (driver());
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

    public static void destroy() {
        try {
            driver().close();
        } catch (Exception ignore) {
        }
        try {
            driver().quit();
        } catch (Exception ignore) {
        }
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

    public static String getDefaultScreenshotDir() {
        return defaultScreenshotDir;
    }

    public static String getDefaultVideoDir() {
        return defaultVideoDir;
    }

    public static String getDefaultVideoFileName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[1];
        for (int i = 1; i < stackTraceElements.length; i++) {
            caller = stackTraceElements[i];
            if (!StringUtils.startsWith(caller.getClassName(), Browser.class.getPackage().getName())) {
                break;
            }
        }
        String methodName = caller.getMethodName();
        String className = caller.getClassName();
        String[] classNameSplit = className.split("\\.");
        className = classNameSplit[classNameSplit.length - 1];
        DateFormat timeFormatter = new SimpleDateFormat("'T'HHmmss'+'SSS");
        return String.format("%s_%s_%s", className, methodName, timeFormatter.format(new Date()));
    }

    public static String getDefaultScreenshotFileName() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement caller = stackTraceElements[1];
        for (int i = 1; i < stackTraceElements.length; i++) {
            caller = stackTraceElements[i];
            if (!StringUtils.startsWith(caller.getClassName(), Browser.class.getPackage().getName())) {
                break;
            }
        }
        String methodName = caller.getMethodName();
        String className = caller.getClassName();
        String[] classNameSplit = className.split("\\.");
        className = classNameSplit[classNameSplit.length - 1];
        String title;
        try {
            title = driver().getTitle().replaceAll("[^A-Za-z0-9]", "");
        } catch (Exception ex) {
            title = "unknown-gage-title";
        }
        DateFormat timeFormatter = new SimpleDateFormat("'T'HHmmss'+'SSS");
        return String.format("%s_%s_%s_%s", className, methodName, title, timeFormatter.format(new Date()));
    }

    public String takeScreenshot() {
        return takeScreenshot(false);
    }

    public String takeScreenshot(boolean isFullPage) {
        return takeScreenshot(defaultScreenshotDir + "/" + getDefaultScreenshotFileName(), isFullPage);
    }

    public String takeScreenshot(String filepath) {
        return takeScreenshot(filepath, false);
    }

    public String takeScreenshot(String filepath, boolean isFullPage) {
        try {
            File file = new File(filepath);
            Shutterbug.shootPage(driver(), isFullPage ? Capture.FULL_SCROLL : Capture.VIEWPORT, true)
                    .withName(file.getName())
                    .save(file.getParentFile().getAbsolutePath());
            return file.getAbsolutePath() + ".png";
        } catch (Exception ex) {
            ex.printStackTrace();
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
        new WebDriverWait(driver(), Duration.ofSeconds(timeOutInSeconds))
                .until(ExpectedConditions.presenceOfElementLocated(by));
        return this;
    }

    public Browser waitForElementToBeClickable(By by) {
        return waitForElementToBeClickable(by, DEFAULT_EXPLICIT_WAIT);
    }

    public Browser waitForElementToBeClickable(By by, int timeOutInSeconds) {
        new WebDriverWait(driver(), Duration.ofSeconds(timeOutInSeconds))
                .until(ExpectedConditions.elementToBeClickable(by));
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
