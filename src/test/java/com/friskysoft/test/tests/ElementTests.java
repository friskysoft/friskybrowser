package com.friskysoft.test.tests;

import com.friskysoft.test.framework.BaseTests;
import com.friskysoft.test.utils.TestConstants;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ElementTests extends BaseTests {

    @BeforeMethod
    public void openUrl() {
        browser.open(baseUrl + loginPath);
    }

    @Test
    public void loginUsingCss() {
        loginPage.usernameCss.type(TestConstants.TEST_USERNAME);
        loginPage.passwordCss.type(TestConstants.TEST_PASSWORD);
        loginPage.submitCss.click();
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + homePath);
    }

    @Test
    public void loginUsingXpath() {
        loginPage.usernameXpath.type(TestConstants.TEST_USERNAME);
        loginPage.passwordXpath.type(TestConstants.TEST_PASSWORD);
        loginPage.submitXpath.click();
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + homePath);
    }

    @Test
    public void loginUsingId() {
        loginPage.usernameId.type(TestConstants.TEST_USERNAME);
        loginPage.passwordId.type(TestConstants.TEST_PASSWORD);
        loginPage.submitCss.click();
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + homePath);
    }

    @Test
    public void loginUsingName() {
        loginPage.usernameName.type(TestConstants.TEST_USERNAME);
        loginPage.passwordName.type(TestConstants.TEST_PASSWORD);
        loginPage.submitCss.clickOption("Login");
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + homePath);
    }

    @Test
    public void loginUsingContainsTextOrAttribute() {
        loginPage.usernameUsingAttribute.type(TestConstants.TEST_USERNAME);
        loginPage.passwordUsingAttribute.type(TestConstants.TEST_PASSWORD);
        loginPage.submitBtnContainingText.waitToBeClickable().click();
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + homePath);
    }

    @Test
    public void clickOption() {
        Assertions.assertThatThrownBy(() -> loginPage.submit.clickOption("Search"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Matching option was not found for text: Search");
    }

    @Test
    public void inputBoxTest() {
        String value;
        loginPage.username.waitToBePresent().clear();
        value = loginPage.username.waitToBeVisible().getValue();
        Assert.assertEquals(value, "");

        loginPage.username.backspace().type(TestConstants.TEST_USERNAME);
        value = loginPage.username.getValue();
        Assert.assertEquals(value, TestConstants.TEST_USERNAME);

        loginPage.username.type("12xx").backspace(2).type("34");
        value = loginPage.username.getValue();
        Assert.assertEquals(value, TestConstants.TEST_USERNAME + "1234");

        loginPage.username.clear();
        value = loginPage.username.getValue();
        Assert.assertEquals(value, "");

        loginPage.username.sendKeys("ab", 3).backspace(3).sendKeys("xy", 2);
        value = loginPage.username.getValue();
        Assert.assertEquals(value, "abaxyxy");
    }

    @Test
    public void textAsserts() {
        loginPage.login(TestConstants.TEST_USERNAME, "");
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + loginPath);
        loginPage.flashMessage.waitToBeVisible()
                .assertTextIsEqualTo("Password cannot be empty")
                .assertTextIsEqualTo("passWORD caNNot Be Empty", false)
                .assertTextContainsString("cannot")
                .assertTextContainsString("CanNOT BE", false);

        Assertions.assertThatThrownBy(() ->
                loginPage.flashMessage.waitToBeVisible().assertTextIsEqualTo("password cannot be empty")
        ).isInstanceOf(AssertionError.class);

        Assertions.assertThatThrownBy(() ->
                loginPage.flashMessage.waitToBeVisible().assertTextIsEqualTo("cannot be empty", false)
        ).isInstanceOf(AssertionError.class);

        Assertions.assertThatThrownBy(() ->
                loginPage.flashMessage.waitToBeVisible().assertTextContainsString("can be")
        ).isInstanceOf(AssertionError.class);

        Assertions.assertThatThrownBy(() ->
                loginPage.flashMessage.waitToBeVisible().assertTextContainsString("CANNOT")
        ).isInstanceOf(AssertionError.class);

        Assertions.assertThatThrownBy(() ->
                loginPage.flashMessage.waitToBeVisible().assertTextContainsString("CAN BE", false)
        ).isInstanceOf(AssertionError.class);
    }

    @Test
    public void elementScreenshot() {
        Assert.assertTrue(loginPage.form.isDisplayed());
        loginPage.form.takeScreenshot();
    }

    @Test
    public void hiddenElement() {
        Assert.assertFalse(loginPage.flashMessage.isDisplayed());
        loginPage.login("abc", "123");
        Assert.assertTrue(loginPage.flashMessage.isDisplayed());
    }

    @Test
    public void invalidElement() {
        Assert.assertFalse(loginPage.notPresentElement.isDisplayed());
        try {
            loginPage.notPresentElement.waitToBePresent(2);
            Assert.fail("TimeoutException expected");
        } catch (Exception ex) {
            Assert.assertEquals(ex.getClass(), org.openqa.selenium.TimeoutException.class);
        }
        try {
            loginPage.notPresentElement.waitToBeVisible(2);
            Assert.fail("TimeoutException expected");
        } catch (Exception ex) {
            Assert.assertEquals(ex.getClass(), org.openqa.selenium.TimeoutException.class);
        }
        try {
            loginPage.notPresentElement.waitToBeClickable(2);
            Assert.fail("TimeoutException expected");
        } catch (Exception ex) {
            Assert.assertEquals(ex.getClass(), org.openqa.selenium.TimeoutException.class);
        }
    }

    @DataProvider
    public Object[][] dropdownData() {
        return new Object[][] {
                {0, "Camera", "cmr"},
                {1, "Laptop", "lpt"},
                {2, "Tablet", "tab"},
                {3, "Phone", "phn"},
        };
    }

    @Test(dataProvider = "dropdownData")
    public void selectFromDropdownByIndex(int index, String text, String value) {
        loginPage.login();
        homePage.categoryDropdown.selectByIndex(index);
        Assert.assertEquals(homePage.categoryDropdown.getValue(), value);
        Assert.assertEquals(homePage.categoryDropdown.getSelectedOption().getText(), text);
    }

    @Test(dataProvider = "dropdownData")
    public void selectFromDropdownByText(int index, String text, String value) {
        loginPage.login();
        homePage.categoryDropdown.selectByText(text);
        Assert.assertEquals(homePage.categoryDropdown.getValue(), value);
        Assert.assertEquals(homePage.categoryDropdown.getSelectedOption().getText(), text);
    }

    @Test(dataProvider = "dropdownData")
    public void selectFromDropdownByValue(int index, String text, String value) {
        loginPage.login();
        homePage.categoryDropdown.selectByValue(value);
        Assert.assertEquals(homePage.categoryDropdown.getValue(), value);
        Assert.assertEquals(homePage.categoryDropdown.getSelectedOption().getText(), text);
    }

    @Test
    public void waitForElementToBeVisible() {
        loginPage.login();
        homePage.searchBox.sendKeys("foo");
        homePage.searchButton.click();
        Assertions.assertThatThrownBy(() -> {
            homePage.searchResult.waitToBeVisible(1);
        }).isInstanceOf(TimeoutException.class).hasMessageContaining("waiting for visibility of element located by " + homePage.searchResult.getBy());

        homePage.searchResult.waitToBeVisible(10);
    }

    @Test
    public void waitForElementToBeInvisible() {
        loginPage.login();
        homePage.searchBox.sendKeys("foo");
        homePage.searchButton.click();
        homePage.spinner.waitToBeVisible(5).waitToBeInvisible(10);

        homePage.searchButton.click();
        Assertions.assertThatThrownBy(() -> {
            homePage.spinner.waitToBeVisible(5).waitToBeInvisible(1);
        }).isInstanceOf(TimeoutException.class).hasMessageContaining("waiting for element to no longer be visible: " + homePage.spinner.getBy());

        homePage.searchResult.waitToBeVisible();
    }

    @Test
    public void waitForElementToBeClickable() {
        browser.open(baseUrl + overlapPath);
        Assertions.assertThatThrownBy(() -> overlapPage.searchButton.click())
                .isInstanceOf(WebDriverException.class)
                .hasMessageContaining("is not clickable at point");

        browser.open(baseUrl + overlapPath);
        overlapPage.searchButton.waitToBeClickable(6).click();

        browser.open(baseUrl + overlapPath);
        Assertions.assertThatThrownBy(() -> overlapPage.searchButton.waitToBeClickable(3).click())
                .isInstanceOf(WebDriverException.class)
                .hasMessageContaining("is not clickable at point");
    }

    @Test
    public void elementHover() {
        browser.open(baseUrl + hoverPath);

        Assertions.assertThatThrownBy(() -> hoverPage.popup.waitToBeVisible(2))
                .isInstanceOf(WebDriverException.class)
                .hasMessageContaining("waiting for visibility of element");

        hoverPage.parent.hover();
        hoverPage.popup.waitToBeVisible(2);
    }

    @Test
    public void elementActions() {
        browser.injectJQuery();
        loginPage.submit.rightClick();
        loginPage.submit.hover();
        loginPage.submit.triggerClick();
    }
}
