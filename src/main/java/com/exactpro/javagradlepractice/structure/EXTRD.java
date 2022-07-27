package com.exactpro.javagradlepractice.structure;

public class EXTRD extends TRADE {

    public enum Header
    {
        tag(),
        version(),
        dateTime(),
        direction(),
        itemID(),
        price(),
        quantity(),
        buyer(),
        seller(),
        comment(),
        nestedTags()
    }
    String version;
    String nestedTags;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNestedTags() {
        return nestedTags;
    }

    public void setNestedTags(String nestedTags) {
        this.nestedTags = nestedTags;
    }
}
