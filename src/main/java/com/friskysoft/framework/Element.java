package com.friskysoft.framework;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.LinkedList;
import java.util.List;

public class Element {

    private By wrappedBy;
    private WebElement wrappedElement;

    private static final Log LOGGER = LogFactory.getLog(Element.class);

    enum LocatorType {
        ID, XPATH, CSS, CLASSNAME, TAG, LINKTEXT, PARTIAL_LINKTEXT
    }

    public Element(By by) {
        setBy(by);
    }

    public Element(WebElement element) {
        wrappedElement = element;
    }

    public Element(String locator) {
        LocatorType locatorType;
        if (locator ==  null || locator.isEmpty()) {
            throw new IllegalArgumentException("Locator cannot be null or empty");
        }
        if (locator.startsWith(".") || locator.startsWith("#")) {
            locatorType = LocatorType.CSS;
        } else if (locator.startsWith("/")) {
            locatorType = LocatorType.XPATH;
        } else if (locator.contains("#")) {
            locatorType = LocatorType.CSS;
        } else if (locator.contains("/")) {
            locatorType = LocatorType.XPATH;
        } else {
            locatorType = LocatorType.CSS;
        }

        if (locatorType == LocatorType.XPATH) {
            setBy(By.xpath(locator));
        } else {
            setBy(By.cssSelector(locator));
        }
    }

    public By getBy() {
        return wrappedBy;
    }

    public Element setBy(By wrappedBy) {
        this.wrappedBy = wrappedBy;
        return this;
    }

    @Override
    public String toString() {
        if (wrappedBy != null) {
            return wrappedBy.toString();
        } else if (wrappedElement != null) {
            String locatorString = wrappedElement.toString();
            String[] locators = locatorString.replace("[","").replace("]","").split("->");
            if (locators.length > 1) {
                return locators[1].trim();
            } else {
                return locatorString;
            }
        } else {
            return null;
        }
    }

    public WebElement getWebElement() {
        if (wrappedElement == null) {
            return Browser.getWebDriver().findElement(getBy());
        } else {
            return wrappedElement;
        }
    }

    public Select getSelectElement() {
        return new Select(getWebElement());
    }

    public List<WebElement> getWebElements() {
        return Browser.getWebDriver().findElements(getBy());
    }

    public Element click() {
        LOGGER.info("Element click: " + this);
        getWebElement().click();
        return this;
    }

    public Element waitToBeVisible() {
        return waitToBeVisible(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeVisible(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be visible: " + this);
        new WebDriverWait(Browser.getWebDriver(), timeOutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(wrappedBy));
        return this;
    }

    public Element waitToBePresent() {
        return waitToBePresent(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBePresent(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be present: " + this);
        new WebDriverWait(Browser.getWebDriver(), timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(wrappedBy));
        return this;
    }

    public Element waitToBeClickable() {
        return waitToBeClickable(Browser.DEFAULT_EXPLICIT_WAIT);
    }

    public Element waitToBeClickable(int timeOutInSeconds) {
        LOGGER.info("Waiting for element to be clickable: " + this);
        new WebDriverWait(Browser.getWebDriver(), timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(wrappedBy));
        return this;
    }

    public Element type(CharSequence... chars) {
        return sendKeys(chars);
    }

    public Element sendKeys(CharSequence... chars) {
        LOGGER.info("Element sendKeys: " + this);
        getWebElement().sendKeys(chars);
        return this;
    }

    public Element submit() {
        LOGGER.info("Element submit: " + this);
        getWebElement().submit();
        return this;
    }

    public Element clear() {
        LOGGER.info("Element text clear: " + this);
        getWebElement().clear();
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
        return getWebElement().getText();
    }

    public boolean isDisplayed() {
        return getWebElement().isDisplayed();
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
        List<Element> elements = getAll();
        if (elements == null || elements.isEmpty()) {
            throw new NotFoundException("No elements found for selector: " + wrappedBy);
        }
        return elements.get(0);
    }

    public List<Element> getAll() {
        List<WebElement> webElements = getWebElements();
        List<Element> elements = new LinkedList<>();
        webElements.forEach(webElement -> elements.add(new Element(webElement)));
        return elements;
    }
}
