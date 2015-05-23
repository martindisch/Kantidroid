package com.martin.kantidroid.ui;

public class NavDrawerItem {
    private String mText;
    private int mIcon;

    public NavDrawerItem(String mText, int mIcon) {
        this.mText = mText;
        this.mIcon = mIcon;
    }

    public String getText() {
        return mText;
    }

    public int getIcon() {
        return mIcon;
    }
}
