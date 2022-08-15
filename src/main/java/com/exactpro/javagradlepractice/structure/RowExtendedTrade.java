package com.exactpro.javagradlepractice.structure;

public class RowExtendedTrade extends RowTrade {

    public enum Header
    {
        Tag(),
        Version(),
        DateTime(),
        Direction(),
        ItemID(),
        Price(),
        Quantity(),
        Buyer(),
        Seller(),
        Comment(),
        NestedTags()
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
