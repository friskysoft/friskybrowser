package com.friskysoft.framework;

import com.assertthat.selenium_shutterbug.core.CaptureElement;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.friskysoft.framework.Browser.driver;

public class Element {

    private By wrappedBy;

    private WebElement wrappedElement;

    private Element parentFrame = null;

    private int waitToBeClickable = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(Element.class);

    enum LocatorType {
        ID, XPATH, CSS, CLASS_NAME, TAG, NAME, LINK_TEXT, PARTIAL_LINK_TEXT
    }

    private static void logWarningWithDeclaringClass(String text) {
        try {
            String declaringClassInfo = Utilities.getDeclaringClassInfo(Element.class);
            if (declaringClassInfo != null && !declaringClassInfo.trim().isEmpty()) {
                LOGGER.warn(declaringClassInfo + " - " + text);
            } else {
                LOGGER.warn(text);
            }
        } catch (Exception ex) {
            //ignore
        }
    }

    private static void logInit(String initUsing) {
        try {
            String declaringClassInfo = Utilities.getDeclaringClassInfo(Element.class);
            String elementInfo = "Setting up element using: " + initUsing;
            if (declaringClassInfo != null && !declaringClassInfo.trim().isEmpty()) {
                LOGGER.debug(declaringClassInfo + " - " + elementInfo);
            } else {
                LOGGER.debug(elementInfo);
            }
        } catch (Exception ex) {
            LOGGER.warn("Error while trying to log Element.<init>", ex);
        }
    }

    public static Element findUsing(String locator) {
        return new Element(locator);
    }

    public static Element findUsingId(String id) {
        return new Element(By.id(id));
    }

    public static Element findUsingCss(String css) {
        return new Element(By.cssSelector(css));
    }

    public static Element findUsingXpath(String xpath) {
        return new Element(By.xpath(xpath));
    }

    public static Element findUsingName(String name) {
        return new Element(By.name(name));
    }

    public static Element findUsingLinkText(String linkText) {
        return new Element(By.linkText(linkText));
    }

    public Element(By by) {
        setBy(by);
    }

    public Element(WebElement element) {
        logInit(element.toString());
        wrappedElement = element;
    }

    public Element(String locator) {
        LocatorType locatorType;
        if (locator == null || locator.isEmpty()) {
            logWarningWithDeclaringClass("null or empty String was used as element locator.");
            setBy(By.cssSelector(""));
            return;
        }
        locator = locator.trim();
        if (locator.startsWith(".") || locator.startsWith("#")) {
            locatorType = LocatorType.CSS;
        } else if (locator.startsWith("/")) {
            locatorType = LocatorType.XPATH;
        } else if (locator.matches("^\\(*/+.*")) {
            locatorType = LocatorType.XPATH;
        } else if (locator.toLowerCase().startsWith("id=")) {
            locatorType = LocatorType.ID;
            locator = locator.replaceFirst("id=", "");
        } else if (locator.toLowerCase().startsWith("css=")) {
            locatorType = LocatorType.CSS;
            locator = locator.replaceFirst("css=", "");
        } else if (locator.toLowerCase().startsWith("xpath=")) {
            locatorType = LocatorType.XPATH;
            locator = locator.replaceFirst("xpath=", "");
        } else if (locator.toLowerCase().startsWith("name=")) {
            locatorType = LocatorType.NAME;
            locator = locator.replaceFirst("name=", "");
        } else if (locator.toLowerCase().startsWith("class=")) {
            locatorType = LocatorType.CLASS_NAME;
            locator = locator.replaceFirst("class=", "");
        } else if (locator.toLowerCase().startsWith("classname=")) {
            locatorType = LocatorType.CLASS_NAME;
            locator = locator.replaceFirst("classname=", "");
        } else if (locator.toLowerCase().startsWith("linktext=")) {
            locatorType = LocatorType.LINK_TEXT;
            locator = locator.replaceFirst("linktext=", "");
        } else if (locator.toLowerCase().startsWith("partiallinktext=")) {
            locatorType = LocatorType.PARTIAL_LINK_TEXT;
            locator = locator.replaceFirst("partiallinktext=", "");
        } else if (locator.toLowerCase().startsWith("tag=")) {
            locatorType = LocatorType.TAG;
            locator = locator.replaceFirst("tag=", "");
        } else if (locator.contains("text()")) {
            locatorType = LocatorType.XPATH;
        } else {
            locatorType = LocatorType.CSS;
        }

        switch (locatorType) {
            case XPATH:
                setBy(By.xpath(locator));
                break;
            case ID:
                setBy(By.id(locator));
                break;
            case NAME:
                setBy(By.name(locator));
                break;
            case TAG:
                setBy(By.tagName(locator));
                break;
            case CLASS_NAME:
                setBy(By.className(locator));
                break;
            case LINK_TEXT:
                setBy(By.linkText(locator));
                break;
            case PARTIAL_LINK_TEXT:
                setBy(By.partialLinkText(locator));
                break;
            case CSS:
            default:
                setBy(By.cssSelector(locator));
        }
    }

    public By getBy() {
        return wrappedBy;
    }

    public Element setBy(By wrappedBy) {
        logInit(wrappedBy.toString());
        this.wrappedBy = wrappedBy;
        return this;
    }

    @Override
    public String toString() {
        if (wrappedBy != null) {
            return wrappedBy.toString();
        } else if (wrappedElement != null) {
            String locatorString = wrappedElement.toString();
            String[] locators = locatorString.replace("[", "").replace("]", "").split("->");
            if (locators.length > 1) {
                return locators[1].trim();
            } else {
                return locatorString;
            }
        } else {
            return "null";
        }
    }

    public WebElement getWebElement() {
        if (wrappedElement == null) {
            return driver().findElement(getBy());
        } else {
            return wrappedElement;
        }
    }

    public Select getSelectElement() {
        return new Select(getWebElement());
    }

    public List<WebElement> getWebElements() {
        return driver().findElements(getBy());
    }

    public Element pause(long milliseconds) {
        Browser.sleep(milliseconds);
        return this;
    }

    public Element sleep(long milliseconds) {
        Browser.sleep(milliseconds);
        return this;
    }

    public Element click() {
        long start = System.currentTimeMillis();
        LOGGER.info("Element click: " + this);
        WebDriverException exception;
        do {
            try {
                run(() -> getWebElement().click());
                exception = null;
                break;
            } catch (WebDriverException ex) {
                if (waitToBeClickable > 0) {
                    LOGGER.warn("Waiting for element to be clickable. Reason: " + ex.getMessage().split("\n")[0]);
                    exception = ex;
                    sleep(500);
                } else {
                    throw ex;
                }
            }
        } while (System.currentTimeMillis() - start <= waitToBeClickable * 1000);
        waitToBeClickable = 0;
        if (exception != null) {
            throw exception;
        }
        return this;
    }

    public Element rightClick() {
        LOGGER.info("Element right-click: " + this);
        run(() -> Browser.getInstance().getActions().contextClick(this.getWebElement()).build().perform());
        return this;
    }

    public Element waitToBeVisible() {
        return waitToBeVisible(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeVisible(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be visible: " + this);
        run(() -> new WebDriverWait(driver(), timeOutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(wrappedBy)));
        return this;
    }

    public Element waitToBePresent() {
        return waitToBePresent(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBePresent(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be present: " + this);
        run(() -> new WebDriverWait(driver(), timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(wrappedBy)));
        return this;
    }

    public Element waitToBeClickable() {
        return waitToBeClickable(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeClickable(int timeOutInSeconds) {
        waitToBeClickable = timeOutInSeconds;
        LOGGER.info("Waiting for element to be clickable: " + this);
        run(() -> new WebDriverWait(driver(), timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(wrappedBy)));
        return this;
    }

    public Element waitToBeInvisible() {
        return waitToBeInvisible(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeInvisible(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to disappear: " + this);
        run(() -> new WebDriverWait(driver(), timeOutInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(wrappedBy)));
        return this;
    }

    public Element type(CharSequence... chars) {
        return sendKeys(chars);
    }

    public Element sendKeys(CharSequence... chars) {
        LOGGER.info("Element sendKeys: " + this);
        run(() -> getWebElement().sendKeys(chars));
        return this;
    }

    public Element sendKeys(CharSequence key, int repeat) {
        LOGGER.info("Element sendKeys: " + this + " repeating '" + key + "' for " + repeat + " times");
        run(() -> {
            for (int i = 0; i < repeat; i++) {
                getWebElement().sendKeys(key);
            }
        });
        return this;
    }

    public Element backspace() {
        return sendKeys(Keys.BACK_SPACE);
    }

    public Element backspace(int repeat) {
        return sendKeys(Keys.BACK_SPACE, repeat);
    }

    public Element submit() {
        LOGGER.info("Element submit: " + this);
        run(() -> getWebElement().submit());
        return this;
    }

    public Element clear() {
        LOGGER.info("Element text clear: " + this);
        run(() -> getWebElement().clear());
        String value = this.getValue();
        //if value is still not empty, use backspaces to clear the text
        if (!value.isEmpty()) {
            LOGGER.warn("Text could not be cleared using selenium clear(), so using backspaces instead. Element: " + this);
            this.backspace(value.length());
        }
        return this;
    }

    public Element selectByIndex(int index) {
        LOGGER.info("Select option by index <" + index + "> from: " + this);
        getSelectElement().selectByIndex(index);
        return this;
    }

    public Element selectByText(String text) {
        LOGGER.info("Select option by text <" + text + "> from: " + this);
        getSelectElement().selectByVisibleText(text);
        return this;
    }

    public Element selectByValue(String value) {
        LOGGER.info("Select option by value <" + value + "> from: " + this);
        getSelectElement().selectByValue(value);
        return this;
    }

    public String getTagName() {
        return getWebElement().getTagName();
    }

    public String getAttribute(String name) {
        return getWebElement().getAttribute(name);
    }

    public String getSrc() {
        return getAttribute("src");
    }

    public String getLink() {
        return getAttribute("href");
    }

    public boolean isSelected() {
        return getWebElement().isSelected();
    }

    public boolean isEnabled() {
        return getWebElement().isEnabled();
    }

    public int count() {
        LOGGER.info("Element count: " + this);
        return getWebElements().size();
    }

    public String getText() {
        LOGGER.info("Element getText: " + this);
        return call(() -> getWebElement().getText()).toString();
    }

    public String getValue() {
        LOGGER.info("Element getValue: " + this);
        return call(() -> getWebElement().getAttribute("value")).toString();
    }

    public Element getSelectedOption() {
        LOGGER.info("Element getSelectedOption: " + this);
        return new Element(getSelectElement().getFirstSelectedOption());
    }

    public boolean isDisplayed() {
        try {
            Wait<WebDriver> wait = new WebDriverWait(driver(), 2);
            wait.until(ExpectedConditions.visibilityOf(getWebElement()));
            return true;
        } catch (Throwable tr) {
            return false;
        }
    }

    public boolean isPresent() {
        return isDisplayed();
    }

    public Point getLocation() {
        return getWebElement().getLocation();
    }

    public Dimension getSize() {
        return getWebElement().getSize();
    }

    public Rectangle getRect() {
        return getWebElement().getRect();
    }

    public String getCssValue(String propertyName) {
        return getWebElement().getCssValue(propertyName);
    }

    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return getWebElement().getScreenshotAs(target);
    }

    public Element getFirst() {
        return getAllIfNotEmpty().get(0);
    }

    public Element getLast() {
        List<Element> elements = getAllIfNotEmpty();
        return elements.get(elements.size() - 1);
    }

    public Element getNth(int index) {
        return getAllIfNotEmpty().get(index);
    }

    private List<Element> getAllIfNotEmpty() {
        List<Element> elements = getAll();
        if (elements == null || elements.isEmpty()) {
            throw new NotFoundException("No elements found for selector: " + wrappedBy);
        }
        return elements;
    }

    public List<Element> getAll() {
        List<WebElement> webElements = getWebElements();
        List<Element> elements = new LinkedList<>();
        webElements.forEach(webElement -> elements.add(new Element(webElement)));
        return elements;
    }

    private Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor) driver()).executeScript(script, args);
    }

    private String getJQuerySelector() {
        String selector = wrappedBy.toString().split(": ")[1];
        selector = selector.replace("\"", "'");
        if (wrappedBy.toString().startsWith("By.selector: ") || wrappedBy.toString().startsWith("By.cssSelector: ")) {
            return "$(\"" + selector + "\")";
        } else if (wrappedBy.toString().startsWith("By.xpath: ")) {
            return "$x(\"" + selector + "\")";
        } else {
            throw new InvalidSelectorException("Identifier not supported");
        }
    }

    public Element triggerClick() {
        LOGGER.info("Element javascript click: " + this);
        executeScript(getJQuerySelector() + ".trigger('click')");
        return this;
    }

    public Element triggerHover() {
        LOGGER.info("Element javascript hover: " + this);
        this.jsMouseEvent("mouseover");
        this.jsMouseEvent("mouseenter");
        return this;
    }

    private void jsMouseEvent(String event) {
        String code = "var fireOnThis = arguments[0];"
                + "var evObj = document.createEvent('MouseEvents');"
                + "evObj.initEvent( '" + event + "', true, true );"
                + "fireOnThis.dispatchEvent(evObj);";
        this.executeScript(code, getWebElement());
    }

    public Element hover() {
        return triggerHover();
    }

    public Element dragTo(Element destination) {
        LOGGER.info("Dragging element: " + this + " to: " + destination);
        Actions actions = new Actions(driver());
        actions.dragAndDrop(this.getWebElement(), destination.getWebElement()).perform();
        return this;
    }

    public Element scrollIntoView() {
        LOGGER.info("Scrolling element into view: " + this);
        try {
            executeScript("arguments[0].scrollIntoView(true);", getWebElement());
        } catch (Exception ex) {
            Actions actions = new Actions(driver());
            actions.moveToElement(getWebElement()).perform();
        }
        return this;
    }

    public Element takeScreenshot() {
        return takeScreenshot(false);
    }

    public Element takeScreenshot(boolean scrolling) {
        String filename = Browser.getDefaultScreenshotFileName() + "_" + this.toString().replaceAll("[^A-Za-z0-9]", "");
        return takeScreenshot(Browser.getDefaultScreenshotDir() + "/" + filename, scrolling);
    }

    public Element takeScreenshot(String filepath) {
        return takeScreenshot(filepath, false);
    }

    public Element takeScreenshot(String filepath, boolean scrolling) {
        try {
            File file = new File(filepath);
            Shutterbug.shootElement(driver(), getWebElement(), scrolling ? CaptureElement.FULL_SCROLL : CaptureElement.VIEWPORT)
                    .withName(file.getName())
                    .save(file.getParentFile().getAbsolutePath());
        } catch (Exception ex) {
            LOGGER.error("Unable to take screenshot of the element: " + this + ". Error:" + ex.getMessage());
        }
        return this;
    }

    public Element getParentFrame() {
        return parentFrame;
    }

    public Element setParentFrame(Element parentFrame) {
        this.parentFrame = parentFrame;
        return this;
    }

    public Element switchTo() {
        if (parentFrame != null) {
            parentFrame.switchTo();
        } else {
            driver().switchTo().defaultContent();
        }
        LOGGER.info("Switching to frame: " + this);
        driver().switchTo().frame(getWebElement());
        return this;
    }

    public Element assertTextIsEqualTo(String expected) {
        return assertTextIsEqualTo(expected, true);
    }

    public Element assertTextIsEqualTo(String expected, boolean matchCase) {
        if (matchCase) {
            Assertions.assertThat(this.getText()).as("Text from " + this).isEqualTo(expected);
        } else {
            Assertions.assertThat(this.getText()).as("Text from " + this).isEqualToIgnoringCase(expected);
        }
        return this;
    }

    public Element assertTextContainsString(String expectedSubString) {
        return assertTextContainsString(expectedSubString, true);
    }

    public Element assertTextContainsString(String expectedSubString, boolean matchCase) {
        if (matchCase) {
            Assertions.assertThat(this.getText()).as("Text from " + this).contains(expectedSubString);
        } else {
            Assertions.assertThat(this.getText()).as("Text from " + this).containsIgnoringCase(expectedSubString);
        }
        return this;
    }

    private void run(Runnable runnable) {
        int retryLeft = 3;
        RuntimeException ex = null;
        while (retryLeft > 0) {
            try {
                retryLeft--;
                runnable.run();
                return;
            } catch (StaleElementReferenceException staleEx) {
                ex = staleEx;
                LOGGER.warn("Stale element found, retry left: " + retryLeft + ". Original error: " + staleEx.getMessage());
                Browser.sleep(100);
            }
        }
        throw ex;
    }

    private Object call(Callable callable) {
        int retryLeft = 3;
        RuntimeException runEx = null;
        while (retryLeft > 0) {
            try {
                retryLeft--;
                return callable.call();
            } catch (StaleElementReferenceException staleEx) {
                runEx = staleEx;
                LOGGER.warn("Stale element found, retry left: " + retryLeft + ". Original error: " + staleEx.getMessage());
                Browser.sleep(100);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        throw runEx;
    }
}
