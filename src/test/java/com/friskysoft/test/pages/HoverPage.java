package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;

public class HoverPage {

    public Element parent = Element.find("id=parent");
    public Element popup = Element.find("xpath=//*[@id='parent']//*[@id='popup']");

}
