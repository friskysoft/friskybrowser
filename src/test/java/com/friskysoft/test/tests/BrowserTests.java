package com.friskysoft.test.tests;

import com.friskysoft.test.pages.HomePage;
import com.friskysoft.test.pages.LoginPage;
import com.friskysoft.test.utils.TestConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BrowserTests extends _BaseTests {

    @BeforeMethod
    public void openUrl() {
        browser.open(LoginPage.url);
    }

    @Test
    public void loginTests() {
        String actualMessage;

        LoginPage.username.clear().type(TestConstants.TEST_USERNAME);
        LoginPage.password.clear();
        LoginPage.submit.click();
        Assert.assertNotEquals(browser.getCurrentUrl(), HomePage.url);
        actualMessage = LoginPage.flash_message.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Password cannot be empty");

        LoginPage.username.clear();
        LoginPage.password.clear().type(TestConstants.TEST_PASSWORD);
        LoginPage.submit.click();
        Assert.assertNotEquals(browser.getCurrentUrl(), HomePage.url);
        actualMessage = LoginPage.flash_message.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Username cannot be empty");

        LoginPage.username.clear();
        LoginPage.password.clear();
        LoginPage.submit.click();
        Assert.assertNotEquals(browser.getCurrentUrl(), HomePage.url);
        actualMessage = LoginPage.flash_message.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Username and Password cannot be empty");

        LoginPage.username.clear().type("abc");
        LoginPage.password.clear().type("123");
        LoginPage.submit.click();
        Assert.assertNotEquals(browser.getCurrentUrl(), HomePage.url);
        actualMessage = LoginPage.flash_message.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Bad Credentials");

        LoginPage.username.clear().type(TestConstants.TEST_USERNAME);
        LoginPage.password.clear().type(TestConstants.TEST_PASSWORD);
        LoginPage.submit.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);

    }

    @Test
    public void jqueryTriggers() {
        LoginPage.submit.triggerHover().triggerClick();
        Assert.assertNotEquals(browser.getCurrentUrl(), HomePage.url);
        String actualMessage = LoginPage.flash_message.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Username and Password cannot be empty");
    }
}
