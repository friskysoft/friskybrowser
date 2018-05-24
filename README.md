# Frisky-Browser

[![Build Status](https://travis-ci.org/friskysoft/friskybrowser.svg?branch=master)](https://travis-ci.org/friskysoft/friskybrowser/builds)

**Frisky-Browser** is a java wrapper library created on top of **Selenium Webdriver**. You can use **Frisky-Browser** features with your existing webdriver tests, or you can set up a new one within a matter of minutes. **Frisky-Browser** uses a thread static webdriver under the hood, so you can run parallel tests without any conflicts (with the assumption that each test is using a single thread).

### Requirements
- Java 8 or above.
- Selenium 3 or above (downloaded automatically when you use maven or gradle or a similar dependency management tool).

### Features
#### Simple and flexible element constructors
The library is intelligent enough to understand the type of selector you are using (no more `findElement()` or `@FindBy`). You can also use the traditional Webdriver **By** selectors, or **Selenium RC** type selectors.
```java
public class LoginPage {

    public Element username = new Element("input.username");
    public Element password = new Element("//input[name='password']");
    public Element login = new Element("id=login_button");
    public Element message = new Element(By.id("error_message"));

}
```
```java
LoginPage loginPage = new LoginPage();
```
#### Simpler and chainable actions for an element
Reduce the line of codes by chaining actions on the same element.
```java
loginPage.username.waitToBeVisible().clear().sendKeys("rafaat");
loginPage.password.waitToBeVisible().clear().sendKeys("pa$$word").submit();
loginPage.login.waitToBeClickable().click();
```
```java
String errorMessage = loginPage.message.waitToBeVisible(5).getText();
```
```java
homePage.menu.waitToBeVisible().hover();
homePage.lastTodoItem.scrollIntoView().dragTo(homePage.topRow);
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

#### Execute sync or async javascripts
You can run any sync or async javascripts on the page you are testing. Frisky-Browser also injects jquery on page load, so you can use the jquery functions as well.
```java
browser.executeScript(script);
browser.executeAsyncScript(script);
```

### Sample test
```java
@Test
public void loginTest() {

    Browser browser = Browser.newInstance(BrowserType.CHROME)
                             .setPageLoadTimeout(30, TimeUnit.SECONDS)
                             .setImplicitWait(5, TimeUnit.SECONDS);

    LoginPage loginPage = new LoginPage();

    browser.open("https://www.friskysoft.com");
    browser.takeScreenshot();

    loginPage.username.waitToBeVisible(10);
    loginPage.username.clear().sendKeys("rafaat");
    loginPage.password.clear().sendKeys("pa$$word");
    loginPage.login.click();

    String actualMessage = loginPage.message.waitToBeVisible(5).getText();
    assertEquals(actualText, "Wrong Credentials");

    browser.takeScreenshot();
    browser.destroy();
}
```

Visit this link for a full example project: https://github.com/friskysoft/friskybrowser-example

Please feel free to report issues at [rafaat123@gmail.com](mailto:rafaat123@gmail.com). Contributions are always welcome!
