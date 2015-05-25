package com.martin.kantidroid.logic;

import android.graphics.Color;

public class Util {

    private static float[] mTempColor = new float[3];

    public static int getDark(int original) {
        Color.colorToHSV(original, mTempColor);
        mTempColor[2] = mTempColor[2] - (float) 0.2;
        return Color.HSVToColor(mTempColor);
    }

    public static int getLight(int original) {
        Color.colorToHSV(original, mTempColor);
        mTempColor[2] = 1;
        mTempColor[1] = mTempColor[1] - (float) 0.35;
        return Color.HSVToColor(mTempColor);
    }
}
