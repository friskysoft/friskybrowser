# friskybrowser

*Make writing of your selenium tests easy and quick!*


**Sample:**

```java

public class GooglePage {

    public static final Element searchBox = new Element("input[name=q]");
    public static final Element searchResults = new Element("div.g");
    public static final Element searchResultLinks = new Element("div.g a");
    public static final Element searchButton = new Element("input[value='Google Search']");

}

@BeforeClass
public void setupBrowser() throws Exception {
    browser = Browser.newInstance(BrowserType.CHROME)
            .setPageLoadTimeout(30, TimeUnit.SECONDS)
            .setImplicitWait(5, TimeUnit.SECONDS);
}

@BeforeMethod
public void openPage() {
    browser.open("https://www.google.com/?complete=0");
    browser.takeScreenshot();
}

@Test
public void sampleTest() {
    GooglePage.searchBox.waitToBePresent().sendKeys("Selenium");
    GooglePage.searchButton.waitToBeClickable().click();
    GooglePage.searchResults.waitToBePresent(10);

    String actualText = GooglePage.searchResults.getFirst().getText();
    assertTrue(actualText.contains("Selenium"));

    String actualLink = GooglePage.searchResultLinks.getFirst().getLink();
    assertTrue(actualLink.contains("seleniumhq.org"));
}

@AfterClass
public void teardownBrowser() {
    browser.takeScreenshot();
    browser.destroy();
}

```


**Dependencies:**

- Java 8 or above
- Selenium 3 or above
