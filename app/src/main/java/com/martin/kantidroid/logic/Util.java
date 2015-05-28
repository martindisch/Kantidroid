package com.martin.kantidroid.logic;

import android.content.Context;
import android.graphics.Color;

import com.martin.kantidroid.R;

public class Util {

    public static int getDark(Context context, int index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors_dark)[index]);
    }

    public static int getLight(Context context, int index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors_light)[index]);
    }
}
