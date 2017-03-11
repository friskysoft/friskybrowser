package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;
import com.friskysoft.test.utils.TestConstants;

public class HomePage {

    public static final Element username_css = new Element(".login [name=username]");
    public static final Element password_css = new Element("#password");
    public static final Element submit_css = new Element("#submit-button button");

    public static final Element username_xpath = new Element("//*[@id='username']");
    public static final Element password_xpath = new Element("//input[name=username]");
    public static final Element submit_xpath = new Element("//submit-button//button");

}
