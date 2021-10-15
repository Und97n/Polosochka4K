package com.polosochka;

public class DisplayMode {
    private final int contentWidth, contentHeight;

    public DisplayMode(int contentWidth, int contentHeight) {
        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public int getContentWidth() {
        return contentWidth;
    }
}
