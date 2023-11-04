package com.friskysoft.test.tests;

import com.friskysoft.framework.Browser;
import com.friskysoft.framework.BrowserType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Set;

import static com.friskysoft.framework.BrowserType.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BrowserTypeTests {

    @DataProvider
    public Object[][] browserTypesData() {
        return new Object[][]{
                {"headless", CHROME_HEADLESS},
                {"chrome headless", CHROME_HEADLESS},
                {"htmlunit", CHROME_HEADLESS},
                {"phantomjs", CHROME_HEADLESS},
                {"chrome", CHROME},
                {"ff headless", FIREFOX_HEADLESS},
                {"headless ff", FIREFOX_HEADLESS},
                {"firefox headless", FIREFOX_HEADLESS},
                {"headless firefox ", FIREFOX_HEADLESS},
                {"ff", FIREFOX},
                {"firefox", FIREFOX},
                {"headless", CHROME_HEADLESS},
                {"ie", IE},
                {"edge", EDGE},
                {"safari", SAFARI},
        };
    }

    @Test(dataProvider = "browserTypesData")
    public void parseFromString(String name, BrowserType expected) {
        assertThat(BrowserType.from(name)).as(name).isEqualTo(expected);
    }

    @Test(dataProvider = "browserTypesData")
    public void createLocalDriver(String name, BrowserType expected) {
        boolean win = Browser.getPlatformType() == Browser.PlatformType.WINDOWS;
        boolean mac = Browser.getPlatformType() == Browser.PlatformType.MAC;
        if ((Set.of(IE, EDGE).contains(expected) && !win) || (expected == SAFARI && !mac)) {
            // not supported by os, so skip
            return;
        }
        if (Set.of(CHROME, FIREFOX).contains(expected) && !win && !mac) {
            // only headless supported, so skip
            return;
        }
        Browser browser = Browser.newLocalDriver(name);
        browser.get("https://google.com");
        Browser.close(browser);
    }
}
