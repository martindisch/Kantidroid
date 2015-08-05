package com.martin.kantidroid;

import android.test.AndroidTestCase;

import com.martin.kantidroid.logic.Fach;

public class Fachtest extends AndroidTestCase {

    public void testAdd() throws Exception {
        Fach fach = new Fach("Deutsch", "true");
        fach.addMark(1, "5.2 - 1.0 - 20150521");
        fach.addMark(1, "6 - 0.5 - 20150522");
        fach.addMark(2, "3.1 - 0.25 - 20150523");
        assertEquals("First semester not as expected", fach.getNoten1(), "5.2 - 1.0 - 20150521\n6 - 0.5 - 20150522\n");
        assertEquals("Second semester not as expected", fach.getNoten2(), "3.1 - 0.25 - 20150523\n");
        String[] returned1 = fach.getNotenEntries(1);
        String[] expected1 = new String[] {"5.2 - 1.0 - 20150521", "6 - 0.5 - 20150522"};
        assertTrue("getNotenEntries didn't return as expected for first semester", arraysEqual(returned1, expected1));
        String[] returned2 = fach.getNotenEntries(2);
        String[] expected2 =  new String[] {"3.1 - 0.25 - 20150523"};
        assertTrue("getNotenEntries didn't return as expected for second semester", arraysEqual(returned2, expected2));

        fach = new Fach("Deutsch", "true");
        assertEquals("Not nothing", "", fach.getNoten1());
        assertTrue("No empty array", arraysEqual(fach.getNotenEntries(2), new String[] {}));
    }

    public void testRemove() throws Exception {
        Fach fach = new Fach("Deutsch", "true");
        fach.addMark(1, "5.2 - 1.0 - 20150521");
        fach.addMark(1, "6 - 0.5 - 20150522");
        fach.addMark(2, "3.1 - 0.25 - 20150523");
        fach.removeMark(1, "5.2 - 1.0 - 20150521");
        assertEquals("Not the same", "6 - 0.5 - 20150522\n", fach.getNoten1());
        fach.removeMark(1, "6 - 0.5 - 20150522");
        assertEquals("Not the same", "", fach.getNoten1());
        fach.removeMark(2, "3.1 - 0.25 - 20150523");
        assertTrue("Array not empty", arraysEqual(fach.getNotenEntries(2), new String[] {}));
    }

    public void testCreation() throws Exception {
        Fach fach = new Fach(0, "Deutsch", "De", "R.color.blue", "", "", "", "3.1 - 0.25 - 20150523\n1.25 - 2 - 20150412\n", "", "", "false", "", "", "");
        assertEquals("First semester not as expected", "", fach.getNoten1());
        assertEquals("Second semester not as expected", "3.1 - 0.25 - 20150523\n1.25 - 2 - 20150412\n", fach.getNoten2());
        fach.removeMark(2, "1.25 - 2 - 20150412");
        assertEquals("Second semester not as expected after removal", "3.1 - 0.25 - 20150523\n", fach.getNoten2());
        assertTrue("Array not as expected", arraysEqual(new String[]{"3.1 - 0.25 - 20150523"}, fach.getNotenEntries(2)));
    }

    public void testCalculation() throws Exception {
        Fach fach = new Fach("Mathematik", "true");
        fach.addMark(1, "5 - 1 - 20150520");
        fach.addMark(1, "4 - 1 - 20150520");
        String result = fach.getMathAverage1();
        String expected = "4.50";
        assertEquals("Result not correct", expected, result);
        fach.addMark(1, "6 - 0.5 - 20150513");
        assertEquals("Result not correct", "4.80", fach.getMathAverage1());
    }

    private boolean arraysEqual(String[] a1, String[] a2) {
        if (a1.length != a2.length) {
            return false;
        }
        for (int i = 0; i < a1.length; i++) {
            if (!a1[i].contentEquals(a2[i])) {
                return false;
            }
        }
        return true;
    }
}