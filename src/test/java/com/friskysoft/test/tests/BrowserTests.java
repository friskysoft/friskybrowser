package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.test.framework.BaseTests;
import com.friskysoft.test.utils.TestConstants;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class BrowserTests extends BaseTests {

    @BeforeMethod
    public void openUrl() {
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
    public void moveWindow() {
        browser.moveWindow(100, 500);
    }

    @Test
    public void moveToCenter() {
        browser
                .minimize()
                .resize(800, 600)
                .moveToCenter();
    }

    @Test
    public void minimize() {
        browser.minimize();
    }

    @Test
    public void screenshot() {
        browser.takeScreenshot();
    }

    @Test
    public void fullpageScreenshot() {
        browser.open(baseUrl + largePagePath);
        browser.takeScreenshot(true);
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

        //FIXME: Assertions.assertThatThrownBy(() -> loginPage.username.sendKeys("foo")).isInstanceOf(UnhandledAlertException.class);
        //FIXME: Assertions.assertThat(browser.getAlertText()).isEqualTo("Handle me!");

        browser.dismissAlertIfExists();
        loginPage.username.sendKeys("foo");
        browser.dismissAlertIfExists();
        loginPage.username.sendKeys("foo");

        browser.executeScript("alert('Handle me!')");
        //FIXME: Assertions.assertThatThrownBy(() -> loginPage.username.sendKeys("foo")).isInstanceOf(UnhandledAlertException.class);

        browser.dismissAlertIfExists();
        loginPage.username.sendKeys("foo");

        browser.switchToTopWindow();
        loginPage.username.sendKeys("foo");

        browser.acceptAlertIfExists();
    }

    @Test
    public void staticMethods() {
        Browser.getDefaultVideoFileName();
        Browser.getArchType();
    }

    @Test
    public void webdriverMethods() {
        browser.setScriptTimeout(10, TimeUnit.SECONDS);
        browser.refresh();
        browser.fullscreen();
        browser.get("https://google.com");
        browser.get("https://www.google.com/search?q=java");
        Assertions.assertThat(browser.getTitle()).containsIgnoringCase("java");
        browser.back();
        Assertions.assertThat(browser.getTitle()).doesNotContainIgnoringCase("java");
        browser.forward();
        Assertions.assertThat(browser.getTitle()).containsIgnoringCase("java");
        browser.getPageSource();
        browser.navigate().refresh();
        browser.takeScreenshot("build/tmp/test.png");
    }

    @Test
    public void webelementMethods() {
        By html = By.tagName("html");
        browser.get("https://google.com");
        browser.findElement(html);
        browser.findElements(html);
        browser.waitForElementToBeClickable(html);
        browser.waitForElementToBePresent(html);
    }

    @Test
    public void remoteDriver() {
        Assertions.assertThatThrownBy(() -> Browser.newRemoteDriver("http://localhost:4444", "chrome"))
                .isInstanceOf(SessionNotCreatedException.class)
                .hasMessageContaining("Could not start a new session");
        Assertions.assertThatThrownBy(() -> Browser.newRemoteDriver("localhost", "chrome"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid remote hub url");
    }
}
