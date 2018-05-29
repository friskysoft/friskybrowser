package com.friskysoft.test.pages;

import com.friskysoft.framework.Element;

public class FramesPage {

    public Element frameA = new Element("#frame-a");
    public Element frameB = new Element("#frame-b");
    public Element frameC = new Element("#frame-c").setParentFrame(frameB);
    public Element frameD = new Element("#frame-d").setParentFrame(frameC);

    public Element textA = new Element("#text-a");
    public Element textB = new Element("#text-b");
    public Element textC = new Element("#text-c");
    public Element textD = new Element("#text-d");

}
