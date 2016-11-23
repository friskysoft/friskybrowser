package com.friskysoft.test.tests;

import com.friskysoft.test.pages.HomePage;
import com.friskysoft.test.pages.LoginPage;
import com.friskysoft.test.utils.TestConstants;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ElementTests extends _BaseTests {

    @BeforeMethod
    public void openUrl() {
        browser.open(LoginPage.url);
    }

    @Test
    public void loginUsingCss() {
        LoginPage.username_css.type(TestConstants.TEST_USERNAME);
        LoginPage.password_css.type(TestConstants.TEST_PASSWORD);
        LoginPage.submit_css.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);
    }

    @Test
    public void loginUsingXpath() {
        LoginPage.username_xpath.type(TestConstants.TEST_USERNAME);
        LoginPage.password_xpath.type(TestConstants.TEST_PASSWORD);
        LoginPage.submit_xpath.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);
    }

    @Test
    public void loginUsingId() {
        LoginPage.username_id.type(TestConstants.TEST_USERNAME);
        LoginPage.password_id.type(TestConstants.TEST_PASSWORD);
        LoginPage.submit_css.click();
        Assert.assertEquals(browser.getCurrentUrl(), HomePage.url);
    }

    @Test
    public void inputBoxTest() {
        String value;
        LoginPage.username.clear();
        value = LoginPage.username.getValue();
        Assert.assertEquals(value, "");

        LoginPage.username.type(TestConstants.TEST_USERNAME);
        value = LoginPage.username.getValue();
        Assert.assertEquals(value, TestConstants.TEST_USERNAME);

        LoginPage.username.type("1234");
        value = LoginPage.username.getValue();
        Assert.assertEquals(value, TestConstants.TEST_USERNAME + "1234");

        LoginPage.username.clear();
        value = LoginPage.username.getValue();
        Assert.assertEquals(value, "");
    }
}
