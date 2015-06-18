package com.martin.kantidroid.logic;

import android.content.Context;
import android.graphics.Color;

import com.martin.kantidroid.R;

public class Util {

    public static int getDark(Context context, String index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors_dark)[Integer.parseInt(index)]);
    }

    public static int getLight(Context context, String index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors_light)[Integer.parseInt(index)]);
    }

    public static int getNormal(Context context, String index) {
        return Color.parseColor(context.getResources().getStringArray(R.array.colors)[Integer.parseInt(index)]);
    }

    public static String formatKont(String used, String available) {
        String formatted;
        if (available.contentEquals("")) {
            formatted = "-";
        }
        else {
            if (used.contentEquals("")) {
                formatted = "0/" + available;
            }
            else {
                formatted = used + "/" + available;
            }
        }
        return formatted;
    }
}
