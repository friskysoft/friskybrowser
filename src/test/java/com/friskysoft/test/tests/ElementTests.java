package com.friskysoft.test.tests;

import com.friskysoft.test.framework.BaseTests;
import com.friskysoft.test.utils.TestConstants;
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
    public void inputBoxTest() {
        String value;
        loginPage.username.clear();
        value = loginPage.username.getValue();
        Assert.assertEquals(value, "");

        loginPage.username.type(TestConstants.TEST_USERNAME);
        value = loginPage.username.getValue();
        Assert.assertEquals(value, TestConstants.TEST_USERNAME);

        loginPage.username.type("1234");
        value = loginPage.username.getValue();
        Assert.assertEquals(value, TestConstants.TEST_USERNAME + "1234");

        loginPage.username.clear();
        value = loginPage.username.getValue();
        Assert.assertEquals(value, "");
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
}
