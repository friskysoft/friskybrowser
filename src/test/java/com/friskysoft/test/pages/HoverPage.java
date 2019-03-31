package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;

public class HoverPage {

    public Element parent = Element.findUsingId("parent");
    public Element popup = Element.findUsingCss("#parent #popup");

}
