package pages;

import com.friskysoft.framework.Element;

public class CarsPage {

    // home page
    public static final Element homePage = new Element("div.page");
    public static final Element makeDropdown = new Element(".sw-input-group-make select");
    public static final Element zipInput = new Element(".sw-input-group-zip input");
    public static final Element searchSubmit = new Element("input[value=Search]");

    // results page
    public static final Element searchResultListing = new Element("#listings");
    public static final Element searchResultTitle = new Element("//h1");

}
