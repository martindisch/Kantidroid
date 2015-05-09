package com.martin.noten;

import java.math.BigDecimal;

public class Fach {

    // private Variablen
    private int _id;
    private String _name;
    private String _noten1;
    private String _math_average1;
    private String _real_average1;
    private String _noten2;
    private String _math_average2;
    private String _real_average2;
    private String _zeugnis;
    private String _promotionsrelevant;

    // leerer constructor
    public Fach() {

    }

    // constructor mit id
    public Fach(int id, String name, String noten1, String math_average1, String real_average1, String noten2, String math_average2, String real_average2, String zeugnis, String promotionsrelevant) {
        this._id = id;
        this._name = name;
        this._noten1 = noten1;
        this._math_average1 = math_average1;
        this._real_average1 = real_average1;
        this._noten2 = noten2;
        this._math_average2 = math_average2;
        this._real_average2 = real_average2;
        this._zeugnis = zeugnis;
        this._promotionsrelevant = promotionsrelevant;
    }

    // constructor für addFach
    public Fach(String name, String promotionsrelevant) {
        this._name = name;
        this._promotionsrelevant = promotionsrelevant;
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
        String schnitt = "-";
        // Falls beide Semester ausgefüllt
        if (!this.getRealAverage1().contentEquals("-") && !this.getRealAverage2().contentEquals("-")) {
            double RealAverage = Math.round((Double.parseDouble(getRealAverage1()) + Double.parseDouble(getRealAverage2())) / 2 * 4) / 4f;
            schnitt = RealAverage + "";
        }

        // Falls nur erstes Semester ausgefüllt
        if (this.getRealAverage2().contentEquals("-") && !this.getRealAverage1().contentEquals("-")) {
            schnitt = this.getRealAverage1() + "";
        }

        // Falls nur zweites Semester ausgefüllt
        if (!this.getRealAverage2().contentEquals("-") && this.getRealAverage1().contentEquals("-")) {
            schnitt = this.getRealAverage2() + "";
        }
        String result = "";
        if (schnitt.contentEquals("-")) {
            result = "-";
        } else {
            result = schnitt;
        }
        return result;
    }

    public void setZeugnis(String zeugnis) {
        this._zeugnis = String.valueOf(zeugnis);
    }

    public String getMathAverage1() {
        String return_value;
        if (!this._noten1.contentEquals("-")) {
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
            BigDecimal MathAverage = addition.divide(teiler, 4, BigDecimal.ROUND_HALF_UP);
            return_value = String.valueOf(MathAverage);
        } else {
            return_value = "-";
        }
        return return_value;
    }

    public void setMathAverage1(String math_average) {
        this._math_average1 = String.valueOf(math_average);
    }

    public String getMathAverage2() {
        String return_value;
        if (!this._noten2.contentEquals("-")) {
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
            BigDecimal MathAverage = addition.divide(teiler, 4, BigDecimal.ROUND_HALF_UP);
            return_value = String.valueOf(MathAverage);
        } else {
            return_value = "-";
        }
        return return_value;
    }

    public void setMathAverage2(String math_average) {
        this._math_average2 = String.valueOf(math_average);
    }

    public String getRealAverage1() {
        String return_value;
        if (!this.getMathAverage1().contentEquals("-")) {
            double MathAverage = Double.parseDouble(this.getMathAverage1());
            /*
			 * DecimalFormat oneDForm = new DecimalFormat("#.#"); double
			 * RealAverage = Double.valueOf(oneDForm.format(MathAverage));
			 */
            double RealAverage = 0.5 * Math.round(MathAverage / 0.5);
            return_value = String.valueOf(RealAverage);
        } else {
            return_value = "-";
        }
        return return_value;
    }

    public void setRealAverage1(String real_average) {
        this._real_average1 = String.valueOf(real_average);
    }

    public String getRealAverage2() {
        String return_value;
        if (!this.getMathAverage2().contentEquals("-")) {
            double MathAverage = Double.parseDouble(this.getMathAverage2());
			/*
			 * DecimalFormat oneDForm = new DecimalFormat("#.#"); double
			 * RealAverage = Double.valueOf(oneDForm.format(MathAverage));
			 */
            double RealAverage = 0.5 * Math.round(MathAverage / 0.5);
            return_value = String.valueOf(RealAverage);
        } else {
            return_value = "-";
        }
        return return_value;
    }

    public void setRealAverage2(String real_average) {
        this._real_average2 = String.valueOf(real_average);
    }
}
