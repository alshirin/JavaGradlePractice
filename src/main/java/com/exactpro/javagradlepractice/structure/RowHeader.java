package com.exactpro.javagradlepractice.structure;

import java.util.Date;

public class RowHeader {
    public enum Header
    {
        Tag(),
        FileVersion(),
        CreationDate(),
        Comment()
    }
    String tag;
    String version;
    Date creationDate;
    String comment;

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}

