package com.martin.kantidroid.logic;

import android.content.Context;
import android.content.SharedPreferences;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class Fach {

    private int _id;
    private String _name;
    private String _short;
    private String _color;
    private String _noten1;
    private String _math_average1;
    private String _real_average1;
    private String _noten2;
    private String _math_average2;
    private String _real_average2;
    private String _promotionsrelevant;
    private String _kont1;
    private String _kont2;
    private String _kont;

    // empty constructor
    public Fach() {
    }

    // constructor setting everything
    public Fach(int id, String name, String nameShort, String color, String noten1, String math_average1, String real_average1, String noten2, String math_average2, String real_average2, String promotionsrelevant, String kont1, String kont2, String kont) {
        this._id = id;
        this._name = name;
        this._short = nameShort;
        this._color = color;
        this._noten1 = noten1;
        this._math_average1 = math_average1;
        this._real_average1 = real_average1;
        this._noten2 = noten2;
        this._math_average2 = math_average2;
        this._real_average2 = real_average2;
        this._promotionsrelevant = promotionsrelevant;
        this._kont1 = kont1;
        this._kont2 = kont2;
        this._kont = kont;
    }

    // constructor for addFach
    public Fach(String name, String promotionsrelevant) {
        this._name = name;
        this._short = "";
        this._color = "";
        this._noten1 = "";
        this._math_average1 = "";
        this._real_average1 = "";
        this._noten2 = "";
        this._math_average2 = "";
        this._real_average2 = "";
        this._promotionsrelevant = promotionsrelevant;
        this._kont1 = "";
        this._kont2 = "";
        this._kont = "";
    }

    // New constructor with important properties
    public Fach(String name, String name_short, String color, String counts, String kont) {
        this._name = name;
        this._short = name_short;
        this._color = color;
        this._noten1 = "";
        this._math_average1 = "";
        this._real_average1 = "";
        this._noten2 = "";
        this._math_average2 = "";
        this._real_average2 = "";
        this._promotionsrelevant = counts;
        this._kont1 = "";
        this._kont2 = "";
        this._kont = kont;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }

    public String getNoten1() {
        return this._noten1;
    }

    public void setNoten1(String noten) {
        this._noten1 = noten;
    }

    public String getNoten2() {
        return this._noten2;
    }

    public void setNoten2(String noten) {
        this._noten2 = noten;
    }

    public String getPromotionsrelevant() {
        return this._promotionsrelevant;
    }

    public void setPromotionsrelevant(String promotionsrelevant) {
        this._promotionsrelevant = promotionsrelevant;
    }

    public String getZeugnis() {
        String schnitt = "";
        String realAverage1 = getRealAverage1();
        String realAverage2 = getRealAverage2();
        // In case both semesters have data
        if (!realAverage1.contentEquals("") && !realAverage2.contentEquals("")) {
            double RealAverage = (Double.parseDouble(realAverage1) + Double.parseDouble(realAverage2)) / 2;
            schnitt = RealAverage + "";
        }

        // In case only first semester has data
        if (realAverage2.contentEquals("") && !realAverage1.contentEquals("")) {
            schnitt = realAverage1 + "";
        }

        // In case only second semester has data
        if (!realAverage2.contentEquals("") && realAverage1.contentEquals("")) {
            schnitt = realAverage2 + "";
        }
        String result;
        if (schnitt.contentEquals("")) {
            result = "";
        } else {
            result = schnitt;
        }
        return result;
    }

    public String getMathAverage1() {
        String return_value;
        if (!this._noten1.contentEquals("")) {
            String[] entries = this._noten1.split("\n");
            int count = entries.length;
            BigDecimal addition = BigDecimal.valueOf(0);
            BigDecimal teiler = BigDecimal.valueOf(0);

            for (int i = 0; i < count; i++) {
                String[] item = entries[i].split(" - ");
                BigDecimal multiplikator = BigDecimal.valueOf(Double.parseDouble(item[1]));
                addition = addition.add(multiplikator.multiply(BigDecimal.valueOf(Double.parseDouble(item[0].replace(",", ".")))));
                teiler = teiler.add(multiplikator);
            }
            BigDecimal MathAverage = addition.divide(teiler, 2, BigDecimal.ROUND_HALF_UP);
            return_value = String.valueOf(MathAverage);
        } else {
            return_value = "";
        }
        return return_value;
    }

    public void setMathAverage1(String math_average) {
        this._math_average1 = String.valueOf(math_average);
    }

    public String getMathAverage2() {
        String return_value;
        if (!this._noten2.contentEquals("")) {
            String[] entries = this._noten2.split("\n");
            int count = entries.length;
            BigDecimal addition = BigDecimal.valueOf(0);
            BigDecimal teiler = BigDecimal.valueOf(0);

            for (int i = 0; i < count; i++) {
                String[] item = entries[i].split(" - ");
                BigDecimal multiplikator = BigDecimal.valueOf(Double.parseDouble(item[1]));
                addition = addition.add(multiplikator.multiply(BigDecimal.valueOf(Double.parseDouble(item[0].replace(",", ".")))));
                teiler = teiler.add(multiplikator);
            }
            BigDecimal MathAverage = addition.divide(teiler, 2, BigDecimal.ROUND_HALF_UP);
            return_value = String.valueOf(MathAverage);
        } else {
            return_value = "";
        }
        return return_value;
    }

    public void setMathAverage2(String math_average) {
        this._math_average2 = String.valueOf(math_average);
    }

    public String getRealAverage1() {
        String return_value;
        String average = getMathAverage1();
        if (!average.contentEquals("")) {
            double MathAverage = Double.parseDouble(average);
            /*
             * DecimalFormat oneDForm = new DecimalFormat("#.#"); double
			 * RealAverage = Double.valueOf(oneDForm.format(MathAverage));
			 */
            double RealAverage = 0.5 * Math.round(MathAverage / 0.5);
            return_value = String.valueOf(RealAverage);
        } else {
            return_value = "";
        }
        return return_value;
    }

    public void setRealAverage1(String real_average) {
        this._real_average1 = String.valueOf(real_average);
    }

    public String getRealAverage2() {
        String return_value;
        String average = getMathAverage2();
        if (!average.contentEquals("")) {
            double MathAverage = Double.parseDouble(average);
            /*
             * DecimalFormat oneDForm = new DecimalFormat("#.#"); double
			 * RealAverage = Double.valueOf(oneDForm.format(MathAverage));
			 */
            double RealAverage = 0.5 * Math.round(MathAverage / 0.5);
            return_value = String.valueOf(RealAverage);
        } else {
            return_value = "";
        }
        return return_value;
    }

    public void setRealAverage2(String real_average) {
        this._real_average2 = String.valueOf(real_average);
    }

    public String getShort() {
        return _short;
    }

    public void setShort(String _short) {
        this._short = _short;
    }

    public String getColor() {
        return _color;
    }

    public void setColor(String _index) {
        this._color = _index;
    }

    public String getKont1() {
        return _kont1;
    }

    public void setKont1(String _kont1) {
        this._kont1 = _kont1;
    }

    public String getKont2() {
        return _kont2;
    }

    public void setKont2(String _kont2) {
        this._kont2 = _kont2;
    }

    public String getKontAvailable(Context context, boolean isDisplay) {
        SharedPreferences sp = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        if (!_kont.contentEquals("") && isDisplay && sp.getBoolean("kontShortage", false)) {
            int kont = Integer.parseInt(_kont);
            kont = (int) Math.ceil(kont * 0.75);
            return kont + "";
        }
        return _kont;
    }

    public void setKontAvailable(String kont) {
        this._kont = kont;
    }

    public String[] getNotenEntries(int semester) {
        String compact;
        if (semester == 1) {
            compact = getNoten1();
        } else {
            compact = getNoten2();
        }
        if (compact != null && !compact.contentEquals("")) {
            return compact.split("\n");
        }
        return new String[]{};
    }

    public void addMark(int semester, String entry) {
        if (semester == 1) {
            String before = getNoten1();
            if (before == null || before.contentEquals("")) {
                before = "";
            }
            setNoten1(before + entry + "\n");
        } else {
            String before = getNoten2();
            if (before == null || before.contentEquals("")) {
                before = "";
            }
            setNoten2(before + entry + "\n");
        }
    }

    public void removeMark(int semester, String entry) {
        ArrayList<String> oldEntries = new ArrayList<>(Arrays.asList(getNotenEntries(semester)));
        int deletedIndex = oldEntries.indexOf(entry);
        if (semester == 1) {
            setNoten1(arrayToString(oldEntries, deletedIndex));
        } else {
            setNoten2(arrayToString(oldEntries, deletedIndex));
        }
    }

    private String arrayToString(ArrayList<String> entries, int notIncludedIndex) {
        String newContent = "";
        for (int i = 0; i < entries.size(); i++) {
            if (i != notIncludedIndex) {
                newContent += entries.get(i) + "\n";
            }
        }
        return newContent;
    }

    public String[] getKontEntries(int semester) {
        String compact;
        if (semester == 1) {
            compact = getKont1();
        } else {
            compact = getKont2();
        }
        if (compact != null && !compact.contentEquals("")) {
            return compact.split("\n");
        }
        return new String[]{};
    }

    public void addKont(int semester, String entry) {
        if (semester == 1) {
            String before = getKont1();
            if (before == null || before.contentEquals("")) {
                before = "";
            }
            setKont1(before + entry + "\n");
        } else {
            String before = getKont2();
            if (before == null || before.contentEquals("")) {
                before = "";
            }
            setKont2(before + entry + "\n");
        }
    }

    public void removeKont(int semester, String entry) {
        ArrayList<String> oldEntries = new ArrayList<>(Arrays.asList(getKontEntries(semester)));
        int deletedIndex = oldEntries.indexOf(entry);
        if (semester == 1) {
            setKont1(arrayToString(oldEntries, deletedIndex));
        } else {
            setKont2(arrayToString(oldEntries, deletedIndex));
        }
    }

}
