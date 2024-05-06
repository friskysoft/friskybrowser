package com.friskysoft.framework;

import org.apache.commons.lang3.StringUtils;

public enum BrowserType {

    CHROME,
    CHROME_HEADLESS,
    FIREFOX,
    FIREFOX_HEADLESS,
    EDGE,
    SAFARI,
    IE,
    UNKNOWN;

    public static BrowserType from(final String value) {

        if (StringUtils.isBlank(value)) {
            return UNKNOWN;
        }

        if (StringUtils.containsAnyIgnoreCase(value, "htmlunit", "phantom") ||
                StringUtils.equalsIgnoreCase(value, "headless")) {
            return CHROME_HEADLESS;
        } else if (StringUtils.containsAnyIgnoreCase(value, "chrome")) {
            if (StringUtils.containsAnyIgnoreCase(value, "headless", "unit")) {
                return CHROME_HEADLESS;
            } else {
                return CHROME;
            }
        } else if (StringUtils.containsAnyIgnoreCase(value, "firefox", "ff")) {
            if (StringUtils.containsAnyIgnoreCase(value, "headless", "unit")) {
                return FIREFOX_HEADLESS;
            } else {
                return FIREFOX;
            }
        } else if (StringUtils.containsAnyIgnoreCase(value, "safari")) {
            return SAFARI;
        } else if (StringUtils.containsAnyIgnoreCase(value, "edge")) {
            return EDGE;
        } else if (StringUtils.containsAnyIgnoreCase(value, "ie", "internet", "explorer")) {
            return IE;
        }

        return UNKNOWN;
    }
}
