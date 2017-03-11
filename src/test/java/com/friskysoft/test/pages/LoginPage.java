package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;
import com.friskysoft.test.utils.TestConstants;
import org.openqa.selenium.By;

public class LoginPage {

    public static final Element username = new Element("#username");
    public static final Element password = new Element("#password");
    public static final Element submit = new Element("#submit-button button");

    public static final Element username_css = new Element(".login [name=username]");
    public static final Element password_css = new Element("#password");
    public static final Element submit_css = new Element("#submit-button button");

    public static final Element username_xpath = new Element("//*[@id='username']");
    public static final Element password_xpath = new Element("//input[@name='password']");
    public static final Element submit_xpath = new Element("//*[@id='submit-button']//button");

    public static final Element username_id = new Element(By.id("username"));
    public static final Element password_id = new Element("id=password");

    public static final Element flash_message = new Element("#flash-message");

}
