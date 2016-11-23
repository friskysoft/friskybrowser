# Frisky-Browser

[![Build Status](https://travis-ci.org/friskysoft/friskybrowser.svg?branch=master)](https://travis-ci.org/friskysoft/friskybrowser)

**Frisky-Browser** is a java wrapper library created on top of **Selenium Webdriver**. You can use **Frisky-Browser** features with your existing webdriver tests, or you can set up a new one within a matter of minutes. **Frisky-Browser** uses a thread static webdriver under the hood, so you can run parallel tests without any conflicts (with the assumption that each test is using a single thread).

### Requirements
- Java 8 or above.
- Selenium 3 or above (downloaded automatically when you use maven or gradle or a similar dependency management tool).

### Features
#### Simple and flexible element constructors
The library is intelligent enough to understand the type of selector you are using. You can also use the traditional Webdriver **By** selectors, or **Selenium RC** type selectors.
```java
public class LoginPage {

    public static Element username = new Element("input.username");
    public static Element password = new Element("//input[name='password']");
    public static Element login = new Element("id=login_button");
    public static Element message = new Element(By.id("error_message"));

}
```

#### Chain multiple actions for an element
Reduce the line of codes by chaining actions on the same element.
```java
LoginPage.username.waitToBeVisible().clear().sendKeys("rafaat");
LoginPage.password.waitToBeVisible().clear().sendKeys("pa$$word").submit();
```
```java
LoginPage.login.waitToBeClickable().click();
```
```java
LoginPage.message.waitToBeVisible(5).getText();
```

#### Use existing webdriver
If you are already using webdriver, it is very easy to hook it up with Frisky-Browser.
```java
WebDriver driver = new ChromeDriver();
Browser browser = Browser.newInstance(driver);
browser.open("https://www.friskysoft.com");
```
```java
DesiredCapabilities capabilities = DesiredCapabilities.chrome();
WebDriver driver = new RemoteWebDriver(capabilities);
Browser browser = Browser.newInstance(driver);
browser.open("https://www.friskysoft.com");
```

#### Easy use of remote webdriver hub
Just specify the Hub URL and Browser type (and OS type as optional)
```java
Browser browser = Browser.newRemoteInstance("http://localhost:4444/wd/hub", BrowserType.CHROME);
browser.open("https://www.friskysoft.com");
```

### Sample test
```java
@Test
public void loginTest() {

    Browser browser = Browser.newInstance(BrowserType.CHROME)
                             .setPageLoadTimeout(30, TimeUnit.SECONDS)
                             .setImplicitWait(5, TimeUnit.SECONDS);

    browser.open("https://www.friskysoft.com");
    browser.takeScreenshot();

    LoginPage.username.waitToBeVisible();
    LoginPage.username.clear().sendKeys("rafaat");
    LoginPage.password.clear().sendKeys("pa$$word");
    LoginPage.login.click();

    String actualMessage = LoginPage.message.waitToBeVisible(5).getText();
    Assert.assertEquals(actualText, "Wrong Credentials");

    browser.takeScreenshot();
    browser.destroy();
}
```

Visit this link for a full example project: https://github.com/friskysoft/friskybrowser-example
