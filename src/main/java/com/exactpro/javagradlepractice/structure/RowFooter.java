package com.exactpro.javagradlepractice.structure;

public class RowFooter {

    String tag;
    int numberOfTrades;
    int dataLength;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(int numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
}

