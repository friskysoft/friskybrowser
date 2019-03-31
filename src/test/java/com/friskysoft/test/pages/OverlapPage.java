package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;

public class OverlapPage {

    public Element searchButton = Element.findUsingCss("button.primary.search-submit");
    public Element buttonBlocker = Element.findUsingCss(".button-blocker");

}
