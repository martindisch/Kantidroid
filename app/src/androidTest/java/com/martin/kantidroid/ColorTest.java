package com.martin.kantidroid;

import android.graphics.Color;
import android.test.AndroidTestCase;
import android.util.Log;

import com.martin.kantidroid.logic.Util;

public class ColorTest extends AndroidTestCase {

    public void testDarkColor() throws Exception {
        int color = Util.getDark(Color.parseColor("#d50000"));
    }

    public void testLightColor() throws Exception {
        int color = Util.getLight(Color.parseColor("#d50000"));
    }
}
