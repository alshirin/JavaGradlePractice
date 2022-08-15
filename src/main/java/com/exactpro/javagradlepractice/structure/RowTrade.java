package com.exactpro.javagradlepractice.structure;

import java.util.Date;

public class RowTrade {
    public enum Header
    {
        Tag(),
        DateTime(),
        Direction(),
        ItemID(),
        Price(),
        Quantity(),
        Buyer(),
        Seller(),
        Comment()
    }
    String tag;
    Date dateTime;
    String direction;
    String itemID;
    double price;
    int quantity;
    String buyer;
    String seller;
    String comment;


    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public Date getTradeDateTime() {
        return dateTime;
    }

    public void setTradeDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

