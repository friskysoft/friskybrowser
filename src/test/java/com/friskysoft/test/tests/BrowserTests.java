package com.friskysoft.test.tests;

import com.friskysoft.test.framework.BaseTests;
import com.friskysoft.test.utils.TestConstants;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.UnhandledAlertException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class BrowserTests extends BaseTests {

    @BeforeMethod
    public void openUrl() {
        setupBrowser();
        browser.open(baseUrl + loginPath);
    }

    @Test
    public void loginTests() {
        String actualMessage;

        loginPage.login(TestConstants.TEST_USERNAME, "");
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + loginPath);
        actualMessage = loginPage.flashMessage.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Password cannot be empty");

        loginPage.login("", TestConstants.TEST_PASSWORD);
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + loginPath);
        actualMessage = loginPage.flashMessage.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Username cannot be empty");

        loginPage.login("", "");
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + loginPath);
        actualMessage = loginPage.flashMessage.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Username and Password cannot be empty");

        loginPage.login("abc", "123");
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + loginPath);
        actualMessage = loginPage.flashMessage.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Bad Credentials");

        loginPage.username.clear().type(TestConstants.TEST_USERNAME);
        loginPage.password.clear().type(TestConstants.TEST_PASSWORD);
        loginPage.submit.click();
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + homePath);

    }

    @Test
    public void jqueryTriggers() {
        browser.injectJQuery();
        loginPage.submit.triggerHover().triggerClick();
        Assert.assertEquals(browser.getCurrentUrl(), baseUrl + loginPath);
        String actualMessage = loginPage.flashMessage.waitToBeVisible().getText();
        Assert.assertEquals(actualMessage, "Username and Password cannot be empty");
    }

    @Test
    public void driverQuit() {
        browser.quit();
        browser.quit();
        browser.quit();
    }

    @Test
    public void driverClose() {
        browser.close();
        browser.close();
        browser.close();
    }

    @Test
    public void alertHandling() {

        browser.executeScript("alert('Handle me!')");

        Assertions.assertThatThrownBy(() -> loginPage.username.sendKeys("foo")).isInstanceOf(UnhandledAlertException.class);
        Assertions.assertThat(browser.getAlertText()).isEqualTo("Handle me!");

        browser.dismissAlert();
        loginPage.username.sendKeys("foo");
        browser.dismissAlertIfExists();
        loginPage.username.sendKeys("foo");

        browser.executeScript("alert('Handle me!')");
        Assertions.assertThatThrownBy(() -> loginPage.username.sendKeys("foo")).isInstanceOf(UnhandledAlertException.class);

        browser.dismissAlertIfExists();
        loginPage.username.sendKeys("foo");

        browser.switchToTopWindow();
        loginPage.username.sendKeys("foo");
    }
}
