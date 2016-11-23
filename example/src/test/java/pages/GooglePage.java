package pages;

import com.friskysoft.framework.Element;

public class GooglePage {

    public static final Element searchBox = new Element("input[name=q]");
    public static final Element searchResults = new Element("div.g");
    public static final Element searchResultLinks = new Element("div.g a");
    public static final Element searchButton = new Element("input[value='Google Search']");

}
