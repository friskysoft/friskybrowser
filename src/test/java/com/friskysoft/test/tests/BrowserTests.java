package com.friskysoft.test.tests;

import com.friskysoft.test.pages.HomePage;
import com.friskysoft.test.pages.LoginPage;
import com.friskysoft.test.utils.TestConstants;
import org.testng.Assert;
import org.testng.annotations.Test;

public class BrowserTests extends _BaseTests {

    @Test
    public void loginUsingCss() {
        browser.open(LoginPage.url);
        LoginPage.username_css.type(TestConstants.TEST_USERNAME);
        LoginPage.password_css.type(TestConstants.TEST_PASSWORD);
        LoginPage.submit_css.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);
    }

    @Test
    public void loginUsingXpath() {
        browser.open(LoginPage.url);
        LoginPage.username_xpath.type(TestConstants.TEST_USERNAME);
        LoginPage.password_xpath.type(TestConstants.TEST_PASSWORD);
        LoginPage.submit_xpath.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);
    }

    @Test
    public void loginUsingId() {
        browser.open(LoginPage.url);
        LoginPage.username_id.type(TestConstants.TEST_USERNAME);
        LoginPage.password_id.type(TestConstants.TEST_PASSWORD);
        LoginPage.submit_css.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);
    }
}
