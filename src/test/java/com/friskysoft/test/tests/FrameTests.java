package com.friskysoft.test.tests;

import com.friskysoft.test.framework.BaseTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FrameTests extends BaseTests {

    @BeforeMethod
    public void openUrl() {
        browser.open(baseUrl + framesPath);
    }

    @Test
    public void switchBetweenFrames() {
        framesPage.frameA.switchTo();
        assert framesPage.textA.getText().equals("iframe-A");

        browser.switchToDefaultContent();
        framesPage.frameB.switchTo();
        assert framesPage.textB.getText().equals("iframe-B");

        framesPage.frameC.switchTo();
        assert framesPage.textC.getText().equals("iframe-C");

        browser.switchToParent().switchToParent();
        framesPage.frameA.switchTo();
        assert framesPage.textA.getText().equals("iframe-A");
    }

    @Test
    public void autoSwitchFramesIncludingParent() {
        framesPage.frameA.switchTo();
        assert framesPage.textA.getText().equals("iframe-A");

        framesPage.frameB.switchTo();
        assert framesPage.textB.getText().equals("iframe-B");

        framesPage.frameC.switchTo();
        assert framesPage.textC.getText().equals("iframe-C");

        framesPage.frameA.switchTo();
        assert framesPage.textA.getText().equals("iframe-A");

        framesPage.frameD.switchTo();
        assert framesPage.textD.getText().equals("iframe-D");

        framesPage.frameC.switchTo();
        assert framesPage.textC.getText().equals("iframe-C");

        framesPage.frameB.switchTo();
        assert framesPage.textB.getText().equals("iframe-B");
    }
}
