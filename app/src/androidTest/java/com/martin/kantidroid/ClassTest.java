package com.martin.kantidroid;

import android.test.AndroidTestCase;

import com.martin.kantidroid.logic.Util;

public class ClassTest extends AndroidTestCase {

    String case1 = "6 gI";
    String case2 = "0 Fh";
    String case3 = "7He";
    String case4 = "3Hb";
    String case5 = "5Fa";
    String case6 = "4 F";
    String case7 = "4Gq";
    String case8 = "3  Gc";
    String case9 = " 3Fh";
    String case10 = "4Gi ";
    String case11 = "4 Gk ";
    String case12 = "";
    String case13 = "66Gc";
    String case14 = "5 5c";
    String case15 = "lkö";

    public void testNumber() throws Exception {
        assertEquals(0, Util.getNumberIndex(case1));
        assertEquals(-1, Util.getNumberIndex(case2));
        assertEquals(-1, Util.getNumberIndex(case3));
        assertEquals(0, Util.getNumberIndex(case4));
        assertEquals(0, Util.getNumberIndex(case5));
        assertEquals(0, Util.getNumberIndex(case6));
        assertEquals(0, Util.getNumberIndex(case7));
        assertEquals(0, Util.getNumberIndex(case8));
        assertEquals(1, Util.getNumberIndex(case9));
        assertEquals(0, Util.getNumberIndex(case10));
        assertEquals(0, Util.getNumberIndex(case11));
        assertEquals(-1, Util.getNumberIndex(case12));
        assertEquals(-1, Util.getNumberIndex(case13));
        assertEquals(-1, Util.getNumberIndex(case14));
        assertEquals(-1, Util.getNumberIndex(case15));
    }

    public void testDepartment() throws Exception {
        assertEquals(2, Util.getDepartmentIndex(case1, Util.getNumberIndex(case1)));
        assertEquals(-1, Util.getDepartmentIndex(case2, Util.getNumberIndex(case2)));
        assertEquals(-1, Util.getDepartmentIndex(case3, Util.getNumberIndex(case3)));
        assertEquals(1, Util.getDepartmentIndex(case4, Util.getNumberIndex(case4)));
        assertEquals(1, Util.getDepartmentIndex(case5, Util.getNumberIndex(case5)));
        assertEquals(-1, Util.getDepartmentIndex(case6, Util.getNumberIndex(case6)));
        assertEquals(1, Util.getDepartmentIndex(case7, Util.getNumberIndex(case7)));
        assertEquals(-1, Util.getDepartmentIndex(case8, Util.getNumberIndex(case8)));
        assertEquals(-1, Util.getDepartmentIndex(case9, Util.getNumberIndex(case9)));
        assertEquals(-1, Util.getDepartmentIndex(case10, Util.getNumberIndex(case10)));
        assertEquals(-1, Util.getDepartmentIndex(case11, Util.getNumberIndex(case11)));
        assertEquals(-1, Util.getDepartmentIndex(case12, Util.getNumberIndex(case12)));
        assertEquals(-1, Util.getDepartmentIndex(case13, Util.getNumberIndex(case13)));
        assertEquals(-1, Util.getDepartmentIndex(case14, Util.getNumberIndex(case14)));
        assertEquals(-1, Util.getDepartmentIndex(case15, Util.getNumberIndex(case15)));
    }

    public void testLevel() throws Exception {
        assertEquals(3, Util.getLevelIndex(case1, Util.getDepartmentIndex(case1, Util.getNumberIndex(case1))));
        assertEquals(-1, Util.getLevelIndex(case2, Util.getDepartmentIndex(case2, Util.getNumberIndex(case2))));
        assertEquals(-1, Util.getLevelIndex(case3, Util.getDepartmentIndex(case3, Util.getNumberIndex(case3))));
        assertEquals(2, Util.getLevelIndex(case4, Util.getDepartmentIndex(case4, Util.getNumberIndex(case4))));
        assertEquals(2, Util.getLevelIndex(case5, Util.getDepartmentIndex(case5, Util.getNumberIndex(case5))));
        assertEquals(-1, Util.getLevelIndex(case6, Util.getDepartmentIndex(case6, Util.getNumberIndex(case6))));
        assertEquals(-1, Util.getLevelIndex(case7, Util.getDepartmentIndex(case7, Util.getNumberIndex(case7))));
        assertEquals(-1, Util.getLevelIndex(case8, Util.getDepartmentIndex(case8, Util.getNumberIndex(case8))));
        assertEquals(-1, Util.getLevelIndex(case9, Util.getDepartmentIndex(case9, Util.getNumberIndex(case9))));
        assertEquals(-1, Util.getLevelIndex(case10, Util.getDepartmentIndex(case10, Util.getNumberIndex(case10))));
        assertEquals(-1, Util.getLevelIndex(case11, Util.getDepartmentIndex(case11, Util.getNumberIndex(case11))));
        assertEquals(-1, Util.getLevelIndex(case12, Util.getDepartmentIndex(case12, Util.getNumberIndex(case12))));
        assertEquals(-1, Util.getLevelIndex(case13, Util.getDepartmentIndex(case13, Util.getNumberIndex(case13))));
        assertEquals(-1, Util.getLevelIndex(case14, Util.getDepartmentIndex(case14, Util.getNumberIndex(case14))));
        assertEquals(-1, Util.getLevelIndex(case15, Util.getDepartmentIndex(case15, Util.getNumberIndex(case15))));
    }

    public void testClassUrl() throws Exception {
        assertEquals("6 Gi", Util.getClassUrl(case1));
        assertEquals("error", Util.getClassUrl(case2));
        assertEquals("error", Util.getClassUrl(case3));
        assertEquals("3 Hb", Util.getClassUrl(case4));
        assertEquals("5 Fa", Util.getClassUrl(case5));
        assertEquals("error", Util.getClassUrl(case6));
        assertEquals("error", Util.getClassUrl(case7));
        assertEquals("error", Util.getClassUrl(case8));
        assertEquals("error", Util.getClassUrl(case9));
        assertEquals("error", Util.getClassUrl(case10));
        assertEquals("error", Util.getClassUrl(case11));
        assertEquals("error", Util.getClassUrl(case12));
        assertEquals("error", Util.getClassUrl(case13));
        assertEquals("error", Util.getClassUrl(case14));
        assertEquals("error", Util.getClassUrl(case15));
    }
}
