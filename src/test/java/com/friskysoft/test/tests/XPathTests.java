package com.friskysoft.test.tests;

import com.friskysoft.framework.XPath;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class XPathTests {

    @Test
    public void nodeToString() {
        XPath.Node node = new XPath.Node();
        assertThat(node).hasToString("//*");

        node.skipLevel(false);
        assertThat(node).hasToString("/*");

        node.tag("div");
        assertThat(node).hasToString("/div");

        node.skipLevel(true);
        assertThat(node).hasToString("//div");

        node.addQualifier(new XPath.Qualifier().key("@id").is("test"));
        assertThat(node).hasToString("//div[@id='test']");

        node.addQualifier(new XPath.Qualifier().key("text()").contains("hello"));
        assertThat(node).hasToString("//div[@id='test'][contains(text(),'hello')]");
    }

    @Test
    public void xpathToString() {
        XPath.Node submitBtn = new XPath.Node().tag("button").addQualifier(new XPath.Qualifier("@type").is("submit"));
        assertThat(submitBtn).hasToString("//button[@type='submit']");

        XPath.Node bodyNode = XPath.Node.newInstance().tag("body");
        assertThat(bodyNode).hasToString("//body");

        XPath.Node formNode = XPath.Node.newInstance().tag("form").addQualifier(new XPath.Qualifier("@id").is("login"));
        assertThat(formNode).hasToString("//form[@id='login']");

        submitBtn = new XPath.Node()
                .tag("button")
                .addQualifier(new XPath.Qualifier().textIs("Login"))
                .skipLevel(false);
        assertThat(submitBtn).hasToString("/button[text()='Login']");
        submitBtn.skipLevel(false);

        XPath xpath = XPath.newInstance()
                .addNode(bodyNode)
                .addNode(formNode)
                .addNode(submitBtn);

        String path1 = xpath.build();
        assertThat(path1).isEqualTo("//body//form[@id='login']/button[text()='Login']");

        //use cache
        String path2 = xpath.build();
        assertThat(path2).isEqualTo("//body//form[@id='login']/button[text()='Login']");

        //renew cache
        String path3 = xpath.addNode(XPath.Node.newInstance().tag("label").skipLevel(false)).build();
        assertThat(path3).isEqualTo("//body//form[@id='login']/button[text()='Login']/label");
    }

    @Test
    public void xpathFluentMethods() {
        XPath x;

        x = XPath.anyTag();
        assertThat(x.build()).isEqualTo("//*");

        x = XPath.any("div");
        assertThat(x.build()).isEqualTo("//div");

        x = XPath.root();
        assertThat(x.build()).isEqualTo("/*");

        x = XPath.root("html");
        assertThat(x.build()).isEqualTo("/html");

        x = x.nested("body");
        assertThat(x.build()).isEqualTo("/html//body");

        x = x.withName("main");
        assertThat(x.build()).isEqualTo("/html//body[@name='main']");

        x = x.withType("app");
        assertThat(x.build()).isEqualTo("/html//body[@name='main'][@type='app']");

        x = x.child().withId("nav");
        assertThat(x.build()).isEqualTo("/html//body[@name='main'][@type='app']/*[@id='nav']");

        x = x.nested("div").withTitle("dashboard");
        assertThat(x.build()).isEqualTo("/html//body[@name='main'][@type='app']/*[@id='nav']//div[@title='dashboard']");

        x = XPath.any("div");
        assertThat(x.build()).isEqualTo("//div");

        x = XPath.any("div").withText("hello").containingClass("heading");
        assertThat(x.build()).isEqualTo("//div[text()='hello'][contains(@class,'heading')]");

        x = XPath.any("div").withAttribute("title");
        assertThat(x.build()).isEqualTo("//div[@title]");

        x = XPath.any("div").withAttribute("title").equalTo("hello");
        assertThat(x.build()).isEqualTo("//div[@title='hello']");

        x = XPath
                .any("div").withAttribute("title").equalTo("hello")
                .nested().containingText("test").withAttribute("type").equalTo("placeholder");
        assertThat(x.build()).isEqualTo("//div[@title='hello']//*[contains(text(),'test')][@type='placeholder']");

        x = XPath
                .any("div").withAttribute("title").equalTo("hello")
                .child("span").containingText("test")
                .child("i");
        assertThat(x.build()).isEqualTo("//div[@title='hello']/span[contains(text(),'test')]/i");

        x = XPath
                .any("div").withAttribute("title").equalTo("hello")
                .parent();
        assertThat(x.build()).isEqualTo("//div[@title='hello']/..");

        x = XPath
                .any("div").withAttribute("title").equalTo("hello")
                .sibling();
        assertThat(x.build()).isEqualTo("//div[@title='hello']/../*");

        x = XPath
                .any("div").withAttribute("title").equalTo("hello")
                .sibling("div").withIndex(5);
        assertThat(x.build()).isEqualTo("//div[@title='hello']/../div[5]");

        x = XPath.anyTag().withAttribute("level").greaterThan(5);
        assertThat(x.build()).isEqualTo("//*[@level>5]");

        x = XPath.any("div").withAttribute("level").lessThan(10);
        assertThat(x.build()).isEqualTo("//div[@level<10]");

        x = new XPath().withAttribute("name").equalTo("test");
        assertThat(x.build()).isEqualTo("//*[@name='test']");

    }
}
