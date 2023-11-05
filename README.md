# Frisky-Browser

[![Build Status](https://github.com/friskysoft/friskybrowser/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/friskysoft/friskybrowser/actions?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/com.friskysoft/friskybrowser.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.friskysoft%22%20a%3A%22friskybrowser%22)

📦 **Frisky-Browser** is a java wrapper library created on top of **Selenium Webdriver**. You can use **Frisky-Browser** features with your existing webdriver tests, or you can set up a new one within a matter of minutes. **Frisky-Browser** uses a thread static webdriver under the hood, so you can run parallel tests without any conflicts (with the assumption that each test is using a single thread).

**To add to your project's dependency**

- Gradle:

        implementation 'com.friskysoft:friskybrowser:3.0.0'

- Maven:

        <dependency>
            <groupId>com.friskysoft</groupId>
            <artifactId>friskybrowser</artifactId>
            <version>2.2.0</version>
        </dependency>

### Requirements
- Java 11 or above.
- Selenium 4 or above (downloaded automatically when you use maven or gradle or a similar dependency management tool).

### Features
#### Simple and flexible element constructors
The library is intelligent enough to understand the type of selector you are using (no more `findElement()` or `@FindBy`). You can also use the traditional Webdriver **By** selectors, or **Selenium RC** type selectors.
```java
public class LoginPage {

    public Element username = new Element("input.username");
                              // OR Element.findUsing("input.username");
    public Element password = new Element("//input[name='password']");
                              // OR Element.findUsing("//input[name='password']");
                              // OR Element.findUsingXpath("//input[name='password']");
                              // OR Element.findUsingName("password");
    public Element login = new Element("id=login_button");
                              // OR Element.findUsingId("login_button");
    public Element message = new Element(By.id("error_message"));
                              // OR Element.findUsing("#error_message");
                              // OR Element.findUsingId("error_message");

}
```
```java
LoginPage loginPage = new LoginPage();
```
#### Chainable actions for an element using fluent interface
- Reduce the line of codes by chaining actions on the same element
    ```java
    loginPage.username.waitToBeVisible().clear().sendKeys("rafaat");
    loginPage.password.waitToBeVisible().clear().sendKeys("pa$$word").submit();
    loginPage.login.waitToBeClickable().click();
    String errorMessage = loginPage.message.waitToBeVisible(5).getText();
    ```
- Hover and Drag-Drop
    ```java
    homePage.menu.waitToBeVisible().hover();`
    homePage.lastTodoItem.scrollIntoView().dragTo(homePage.topRow);`
    ```

- Dropdown Select
    ```java
    homePage.dropdown.selectByValue(value);
    homePage.dropdown.selectByText(text);
    homePage.dropdown.selectByIndex(index);
    ```

#### Take screenshots of the page, or a single element
```java
browser.takeScreenshot();
browser.takeScreenshot(true); //use true for scrolling screenshots
homePage.menu.takeScreenshot();
```

#### Use existing webdriver
If you are already using webdriver, it is very easy to hook it up with Frisky-Browser.
```java
WebDriver driver = new ChromeDriver();
Browser browser = Browser.setWebDriver(driver);
browser.open("https://www.friskysoft.com");
```
```java
DesiredCapabilities capabilities = DesiredCapabilities.chrome();
WebDriver driver = new RemoteWebDriver(url, capabilities);
Browser browser = Browser.setWebDriver(driver);
browser.open("https://www.friskysoft.com");
```

#### Easy use of remote webdriver hub
Just specify the Hub URL and Browser type (and OS type as optional)
```java
Browser browser = Browser.newRemoteDriver("http://localhost:4444/wd/hub", BrowserType.CHROME);
browser.open("https://www.friskysoft.com");
```

#### Execute sync or async javascripts
You can run any sync or async javascripts on the page you are testing. Frisky-Browser also injects jquery on page load, so you can use the jquery functions as well.
```java
browser.executeScript(script);
browser.executeAsyncScript(script);
```
#### Easier switching between iframes

#### And many more...

⚠️ See `Browser` and `Element` classes for a list of all supported methods.

### Sample test
```java
@Test
public void loginTest() {

    Browser browser = Browser.newLocalDriver("chrome")
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

Please feel free to report issues to [rafaat123@gmail.com](mailto:rafaat123@gmail.com). Contributions are always welcome!
