package com.friskysoft.framework;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

public class Element {

    private By wrappedBy;

    private WebElement wrappedElement;

    private Element parentFrame = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(Element.class);

    enum LocatorType {
        ID, XPATH, CSS, CLASS_NAME, TAG, NAME, LINK_TEXT, PARTIAL_LINK_TEXT
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
            throw new IllegalArgumentException("Locator cannot be null or empty");
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
            return null;
        }
    }

    public WebDriver getDriver() {
        // returns the thread static webdriver for this thread
        return Browser.getWebDriver();
    }

    public WebElement getWebElement() {
        if (wrappedElement == null) {
            return getDriver().findElement(getBy());
        } else {
            return wrappedElement;
        }
    }

    public Select getSelectElement() {
        return new Select(getWebElement());
    }

    public List<WebElement> getWebElements() {
        return getDriver().findElements(getBy());
    }

    public Element click() {
        LOGGER.info("Element click: " + this);
        run(() -> getWebElement().click());
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
        run(() -> new WebDriverWait(getDriver(), timeOutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(wrappedBy)));
        return this;
    }

    public Element waitToBePresent() {
        return waitToBePresent(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBePresent(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be present: " + this);
        run(() -> new WebDriverWait(getDriver(), timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(wrappedBy)));
        return this;
    }

    public Element waitToBeClickable() {
        return waitToBeClickable(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeClickable(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be clickable: " + this);
        new WebDriverWait(getDriver(), timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(wrappedBy));
        return this;
    }

    public Element waitToBeInvisible() {
        return waitToBeInvisible(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeInvisible(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to disappear: " + this);
        new WebDriverWait(getDriver(), timeOutInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(wrappedBy));
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

    public Element submit() {
        LOGGER.info("Element submit: " + this);
        run(() -> getWebElement().submit());
        return this;
    }

    public Element clear() {
        LOGGER.info("Element text clear: " + this);
        run(() -> getWebElement().clear());
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
            Wait<WebDriver> wait = new WebDriverWait(getDriver(), 2);
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
        return ((JavascriptExecutor) getDriver()).executeScript(script, args);
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
        executeScript(getJQuerySelector() + ".trigger('mouseenter')");
        return this;
    }

    public Element hover() {
        return triggerHover();
    }

    public Element dragTo(Element destination) {
        LOGGER.info("Dragging element: " + this + " to: " + destination);
        Actions actions = new Actions(getDriver());
        actions.dragAndDrop(this.getWebElement(), destination.getWebElement()).perform();
        return this;
    }

    public Element scrollIntoView() {
        LOGGER.info("Scrolling element into view: " + this);
        try {
            executeScript("arguments[0].scrollIntoView(true);", getWebElement());
        } catch (Exception ex) {
            Actions actions = new Actions(getDriver());
            actions.moveToElement(getWebElement()).perform();
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
            getDriver().switchTo().defaultContent();
        }
        LOGGER.info("Switching to frame: " + this);
        getDriver().switchTo().frame(getWebElement());
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
