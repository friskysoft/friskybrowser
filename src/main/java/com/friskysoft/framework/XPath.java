package com.friskysoft.framework;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class XPath {

    private final List<Node> nodes = new LinkedList<>();
    private String cachedPath = null;
    private int cachedHash = -1;

    public static final String TEXT = "text()";
    public static final String CLASS = "@class";
    public static final String ID = "@id";
    public static final String TITLE = "@title";
    public static final String NAME = "@name";
    public static final String TYPE = "@type";
    public static final String PARENT = "..";
    public static final String ALL = "*";

    public static XPath root() {
        return new XPath().addNode(Node.newInstance().skipLevel(false));
    }

    public static XPath root(String tag) {
        return new XPath().addNode(new Node(tag).skipLevel(false));
    }

    public static XPath anyTag() {
        return new XPath().addNode(Node.newInstance().skipLevel(true));
    }

    public static XPath any(String tag) {
        return new XPath().addNode(Node.newInstance().tag(tag).skipLevel(true));
    }

    private Node leaf() {
        if (!nodes.isEmpty()) {
            return nodes.get(nodes.size() - 1);
        } else {
            final Node leaf = Node.newInstance();
            addNode(leaf);
            return leaf;
        }
    }

    public XPath child() {
        this.addNode(Node.newInstance().skipLevel(false));
        return this;
    }

    public XPath child(String tag) {
        this.addNode(Node.newInstance().tag(tag).skipLevel(false));
        return this;
    }

    public XPath nested() {
        this.addNode(Node.newInstance());
        return this;
    }

    public XPath nested(String tag) {
        this.addNode(Node.newInstance().tag(tag));
        return this;
    }

    public XPath parent() {
        this.addNode(Node.newInstance().tag(PARENT).skipLevel(false));
        return this;
    }

    public XPath sibling() {
        return this.parent().child();
    }

    public XPath sibling(String tag) {
        return this.parent().child(tag);
    }

    public XPath withName(String name) {
        leaf().addQualifier(new Qualifier(NAME).is(name));
        return this;
    }

    public XPath withType(String type) {
        leaf().addQualifier(new Qualifier(TYPE).is(type));
        return this;
    }

    public XPath withTitle(String title) {
        leaf().addQualifier(new Qualifier(TITLE).is(title));
        return this;
    }

    public XPath withId(String id) {
        leaf().addQualifier(new Qualifier(ID).is(id));
        return this;
    }

    public XPath withAttribute(String attribute) {
        leaf().addQualifier(new Qualifier("@" + attribute));
        return this;
    }

    public XPath equalTo(String value) {
        leaf().lastQualifier().is(value);
        return this;
    }

    public XPath greaterThan(int value) {
        leaf().lastQualifier().greaterThan(value);
        return this;
    }

    public XPath lessThan(int value) {
        leaf().lastQualifier().lessThan(value);
        return this;
    }

    public XPath withIndex(int index) {
        leaf().addQualifier(new Qualifier().index(index));
        return this;
    }

    public XPath withText(String value) {
        leaf().addQualifier(new Qualifier().textIs(value));
        return this;
    }

    public XPath containingText(String value) {
        leaf().addQualifier(new Qualifier().textContains(value));
        return this;
    }

    public XPath containingClass(String value) {
        leaf().addQualifier(new Qualifier().classContains(value));
        return this;
    }

    public static class Node {
        private boolean skipLevel = true;
        private String tag = ALL;
        private final List<Qualifier> qualifierList = new LinkedList<>();

        public Node() {

        }

        public Node(String tag) {
            this.tag = tag;
        }

        public static Node newInstance() {
            return new Node();
        }

        public Node skipLevel(boolean skipLevel) {
            this.skipLevel = skipLevel;
            return this;
        }

        public Node tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Qualifier lastQualifier() {
            if (!qualifierList.isEmpty()) {
                return qualifierList.get(qualifierList.size() - 1);
            } else {
                final Qualifier last = Qualifier.newInstance();
                addQualifier(last);
                return last;
            }
        }

        public Node addQualifier(Qualifier qualifier) {
            this.qualifierList.add(qualifier);
            return this;
        }

        @Override
        public String toString() {
            final String qualifierString = qualifierList
                    .stream()
                    .map(Qualifier::toString)
                    .collect(Collectors.joining(""));
            return (skipLevel ? "//" : "/") + this.tag + qualifierString;
        }

        @Override
        public int hashCode() {
            return Objects.hash(skipLevel, tag, qualifierList);
        }
    }

    public static class Qualifier {
        private String key;
        private String value;
        private Operator operator;

        public static Qualifier newInstance() {
            return new Qualifier();
        }

        public Qualifier() {

        }

        public Qualifier(String key) {
            this.key = key;
        }

        public Qualifier key(String key) {
            this.key = key;
            return this;
        }

        public Qualifier textIs(String value) {
            this.key = TEXT;
            return is(value);
        }

        public Qualifier textContains(String value) {
            this.key = TEXT;
            return contains(value);
        }

        public Qualifier classIs(String value) {
            this.key = CLASS;
            return is(value);
        }

        public Qualifier classContains(String value) {
            this.key = CLASS;
            return contains(value);
        }

        public Qualifier is(String value) {
            assert key != null;
            this.operator = Operator.EQUALS;
            this.value = value;
            return this;
        }

        public Qualifier contains(String value) {
            assert key != null;
            this.operator = Operator.CONTAINS;
            this.value = value;
            return this;
        }

        public Qualifier index(int index) {
            assert index >= 0;
            this.operator = Operator.INDEX;
            this.value = String.valueOf(index);
            return this;
        }

        public Qualifier greaterThan(int value) {
            assert key != null;
            this.operator = Operator.GREATER_THAN;
            this.value = String.valueOf(value);
            return this;
        }

        public Qualifier lessThan(int value) {
            assert key != null;
            this.operator = Operator.LESS_THAN;
            this.value = String.valueOf(value);
            return this;
        }

        @Override
        public String toString() {
            if (value == null) {
                return String.format("[%s]", key);
            } else if (operator == Operator.CONTAINS) {
                return String.format("[contains(%s,'%s')]", key, value);
            } else if (operator == Operator.INDEX) {
                return String.format("[%s]", value);
            } else if (operator == Operator.GREATER_THAN) {
                return String.format("[%s>%s]", key, value);
            } else if (operator == Operator.LESS_THAN) {
                return String.format("[%s<%s]", key, value);
            } else {
                return String.format("[%s='%s']", key, value);
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value, operator);
        }
    }

    public enum Operator {
        EQUALS, CONTAINS, INDEX, GREATER_THAN, LESS_THAN
    }

    public static XPath newInstance() {
        return new XPath();
    }

    public XPath addNode(Node node) {
        this.nodes.add(node);
        return this;
    }

    public String compile() {
        if (this.hashCode() == cachedHash) {
            return cachedPath;
        } else {
            final String path = nodes
                    .stream()
                    .map(Node::toString)
                    .collect(Collectors.joining(""));
            this.cachedPath = path;
            this.cachedHash = hashCode();
            return path;
        }
    }

    public XPath validate() {
        final String path = this.compile();
        try {
            javax.xml.xpath.XPathFactory factory = javax.xml.xpath.XPathFactory.newInstance();
            javax.xml.xpath.XPath xpath = factory.newXPath();
            xpath.compile(path);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid xpath: " + path, ex);
        }
        return this;
    }

    public String build() {
        this.validate();
        return this.compile();
    }

    @Override
    public String toString() {
        return this.compile();
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes);
    }
}
