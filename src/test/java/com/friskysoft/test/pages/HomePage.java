package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;

public class HomePage {

    public Element categoryDropdown = Element.findUsing("#category");
    public Element searchBox = Element.findUsingId("search");
    public Element searchButton = Element.findUsingCss("button.primary.search-submit");
    public Element flashMessage = Element.findUsingId("flash-message");
    public Element spinner = Element.findUsingId("spinner");
    public Element searchResult = Element.findUsingId("search-result");

}
