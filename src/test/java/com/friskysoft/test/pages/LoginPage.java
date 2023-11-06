package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;
import com.friskysoft.test.utils.TestConstants;
import org.openqa.selenium.By;

public class LoginPage {

    public Element blankLocator = new Element("");
    public Element nullLocator = new Element((String) null);
    public Element form = new Element("form.login");
    public Element contentHeader = new Element(".content #header");

    public Element username = new Element("#username");
    public Element password = new Element("#password");
    public Element submit = new Element("#submit-button button");

    public Element usernameCss = new Element(".login [name=username]");
    public Element passwordCss = new Element("#password");
    public Element submitCss = new Element("#submit-button button");

    public Element usernameXpath = new Element("//*[@id='username']");
    public Element passwordXpath = new Element("(//input[@name='password'])[1]");
    public Element submitXpath = Element.findUsingXpath("//*[@id='submit-button']//button");

    public Element usernameUsingAttribute = Element.findUsingAttribute("data_test", "user_box");
    public Element passwordUsingAttribute = Element.findUsingAttribute("input", "data_test", "pw_box");
    public Element submitBtnContainingText = Element.findContainingText("button", "Login");

    public Element usernameId = new Element(By.id("username"));
    public Element passwordId = new Element("id=password");

    public Element usernameName = Element.findUsingName("username");
    public Element passwordName = Element.findUsing("name=password");

    public Element flashMessage = Element.find("#flash-message");

    public Element notPresentElement = new Element("#not-present");
    public Element disabledElement = new Element("#disabled-button button");

    public LoginPage login(String username, String password) {
        this.username.clear().type(username);
        this.password.clear().type(password);
        this.submit.click();
        return this;
    }

    public LoginPage login() {
        this.username.clear().type(TestConstants.TEST_USERNAME);
        this.password.clear().type(TestConstants.TEST_PASSWORD);
        this.submit.click();
        return this;
    }
}
