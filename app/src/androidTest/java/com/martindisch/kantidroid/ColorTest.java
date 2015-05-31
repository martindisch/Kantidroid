package com.martindisch.kantidroid;

import android.graphics.Color;
import android.test.AndroidTestCase;

import com.martindisch.kantidroid.logic.Util;

public class ColorTest extends AndroidTestCase {

    public void testDarkColor() throws Exception {
        int color = Util.getDark(getContext(), "0");
    }

    public void testLightColor() throws Exception {
        int color = Util.getLight(getContext(), "0");
    }
}
