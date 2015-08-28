package com.martin.kantidroid.logic;

import android.content.Context;

import com.martin.kantidroid.R;

import java.util.List;
import java.util.Locale;

public class PromoCheck {
    private final Context context;

    public PromoCheck(Context context) {
        super();
        this.context = context;
    }

    public PromoRes getPromo(int semester) {
        int department = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).getInt("department", 0);
        switch (department) {
            case 1:
                return getHMS(semester);
            case 2:
                return getFMS(semester);
        }
        return getGym(semester);
    }

    public String[] getPromoFinal() {
        int department = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).getInt("department", 0);
        switch (department) {
            case 1:
                PromoRes hms = getHMS(3);
                return new String[]{hms.sMessage, hms.sPP, hms.sSchnitt};
            case 2:
                PromoRes fms = getFMS(3);
                return new String[]{fms.sMessage, fms.sPP, fms.sSchnitt};
        }
        Fach entry;
        double plus = 0;
        double minus = 0;
        int total_minus = 0;
        double schn;
        double total = 0;
        int fcount = 0;
        int mppcount = 0;

        String sMessage = "Bestanden";
        String sPP;
        String sSchnitt;
        String sZeugnis;

        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getFachCount();
        List<Fach> faecher = db.getAllFaecher(context, 1);
        for (int i = 0; i < count; i++) {
            entry = faecher.get(i);
            if (entry.getPromotionsrelevant().contentEquals("true")) {
                sZeugnis = entry.getZeugnis();
                if (!sZeugnis.contentEquals("")) {
                    schn = (Double.parseDouble(sZeugnis));
                    if (schn > 4) {
                        plus += schn - 4;
                    }
                    if (schn < 4) {
                        minus += 4 - schn;
                        total_minus++;
                    }
                    total += (Double.parseDouble(sZeugnis));
                    fcount++;
                }

                mppcount++;
            }
        }
        if (!((minus * 2) > plus)) {
            if (total_minus <= 3) {
                sMessage = "Bestanden";
            }
            if (total_minus == 4) {
                sMessage = "Bestanden falls in OG\n4 ungenügende Noten";
            }
            if (total_minus > 4) {
                sMessage = "Nicht bestanden\nMehr als 4 Ungenügende";
            }
        } else {
            sMessage = "Nicht bestanden\nZu wenig Pluspunkte";
        }

        double PP_result = plus - (2 * minus);
        sPP = PP_result + "/" + (mppcount * 2);

        if (fcount > 0) {
            sSchnitt = "Ø " + (String.format(Locale.getDefault(), "%.2f", total / fcount));
        } else {
            sSchnitt = ("-");
        }
        return new String[]{sMessage, sPP, sSchnitt};
    }

    private PromoRes getGym(int iSemester) {
        Fach entry;
        double plus = 0;
        double minus = 0;
        int total_minus = 0;
        double schn;
        double total = 0;
        int fcount = 0;
        int mppcount = 0;

        String sMessage = "Promoviert";
        int iColor = R.color.promo_white;
        String sPP;
        String sSchnitt;
        String realAverage1, realAverage2;

        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getFachCount();
        List<Fach> faecher = db.getAllFaecher(context, iSemester);
        for (int i = 0; i < count; i++) {
            entry = faecher.get(i);
            if (entry.getPromotionsrelevant().contentEquals("true")) {

                switch (iSemester) {
                    case 1:
                        realAverage1 = entry.getRealAverage1();
                        if (!realAverage1.contentEquals("")) {
                            if (Double.parseDouble(realAverage1) > 4) {
                                plus += (Double.parseDouble(realAverage1) - 4);
                            }
                            if (Double.parseDouble(realAverage1) < 4) {
                                minus += (4 - Double.parseDouble(realAverage1));
                                total_minus++;
                            }
                            total += Double.parseDouble(realAverage1);
                            fcount++;
                        }
                        break;
                    case 2:
                        realAverage2 = entry.getRealAverage2();
                        if (!realAverage2.contentEquals("")) {
                            if (Double.parseDouble(realAverage2) > 4) {
                                plus += Double.parseDouble(realAverage2) - 4;
                            }
                            if (Double.parseDouble(realAverage2) < 4) {
                                minus += 4 - Double.parseDouble(realAverage2);
                                total_minus++;
                            }
                            total += Double.parseDouble(realAverage2);
                            fcount++;
                        }
                        break;
                    case 3:
                        // Falls beide Semester ausgefüllt
                        realAverage1 = entry.getRealAverage1();
                        realAverage2 = entry.getRealAverage2();
                        if (!realAverage1.contentEquals("") && !realAverage2.contentEquals("")) {
                            schn = (Double.parseDouble(realAverage1) + Double.parseDouble(realAverage2)) / 2;
                            if (schn > 4) {
                                plus += schn - 4;
                            }
                            if (schn < 4) {
                                minus += 4 - schn;
                                total_minus++;
                            }
                            total += (Double.parseDouble(realAverage1) + Double.parseDouble(realAverage2)) / 2;
                            fcount++;
                        }

                        // Falls nur erstes Semester ausgefüllt
                        if (realAverage2.contentEquals("") && !realAverage1.contentEquals("")) {
                            if (Double.parseDouble(realAverage1) > 4) {
                                plus += (Double.parseDouble(realAverage1) - 4);
                            }
                            if (Double.parseDouble(realAverage1) < 4) {
                                minus += (4 - Double.parseDouble(realAverage1));
                                total_minus++;
                            }
                            total += Double.parseDouble(realAverage1);
                            fcount++;
                        }

                        // Falls nur zweites Semester ausgefüllt
                        if (!realAverage2.contentEquals("") && realAverage1.contentEquals("")) {
                            if (Double.parseDouble(realAverage2) > 4) {
                                plus += Double.parseDouble(realAverage2) - 4;
                            }
                            if (Double.parseDouble(realAverage2) < 4) {
                                minus += 4 - Double.parseDouble(realAverage2);
                                total_minus++;
                            }
                            total += Double.parseDouble(realAverage2);
                            fcount++;
                        }
                        break;
                }

                mppcount++;
            }
        }
        if (!((minus * 2) > plus)) {
            if (total_minus <= 3) {
                sMessage = "Promoviert";
                iColor = R.color.promo_white;
            }
            if (total_minus == 4) {
                sMessage = "Promoviert falls in OG\n4 ungenügende Noten";
                iColor = R.color.promo_black;
            }

            switch (iSemester) {
                case 1:
                    if (total_minus > 4) {
                        sMessage = "Mehr als 4 Ungenügende\nVorsicht 2. Semester";
                        iColor = R.color.promo_black;
                    }
                    break;
                case 2:
                    if (total_minus > 4) {
                        sMessage = "Nicht promoviert\nMehr als 4 Ungenügende";
                        iColor = R.color.promo_black;
                    }
                    break;
                case 3:
                    if (getGym(1).sMessage.contains("Nicht promoviert") || getGym(2).sMessage.contains("Nicht promoviert")) {
                        sMessage = "Nicht promoviert";
                        iColor = R.color.promo_black;
                    }
                    break;
            }
        } else {
            sMessage = "Nicht promoviert\nZu wenig Pluspunkte";
            iColor = R.color.promo_black;
        }

        double PP_result = plus - (2 * minus);
        sPP = PP_result + "/" + (mppcount * 2);

        if (fcount > 0) {
            sSchnitt = (String.format(Locale.getDefault(), "%.4f", total / fcount));
        } else {
            sSchnitt = ("-");
        }

        return new PromoRes(sMessage, iColor, sPP, sSchnitt, getKont(faecher, iSemester));
    }

    private PromoRes getHMS(int iSemester) {
        Fach entry;
        double minus = 0;
        int total_minus = 0;
        double schn;
        double total = 0;
        int fcount = 0;

        String sMessage;
        int iColor;
        String sPP;
        String sSchnitt;

        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getFachCount();
        List<Fach> faecher = db.getAllFaecher(context, 2);
        for (int i = 0; i < count; i++) {
            entry = faecher.get(i);
            if (entry.getPromotionsrelevant().contentEquals("true")) {

                switch (iSemester) {
                    case 1:
                        // Falls erstes Semester ausgefüllt
                        if (!entry.getRealAverage1().contentEquals("")) {
                            if (Double.parseDouble(entry.getRealAverage1()) < 4) {
                                minus += (4 - Double.parseDouble(entry.getRealAverage1()));
                                total_minus++;
                            }
                            total += Double.parseDouble(entry.getRealAverage1());
                            fcount++;
                        }
                        break;
                    case 2:
                        // Falls zweites Semester ausgefüllt
                        if (!entry.getRealAverage2().contentEquals("")) {
                            if (Double.parseDouble(entry.getRealAverage2()) < 4) {
                                minus += 4 - Double.parseDouble(entry.getRealAverage2());
                                total_minus++;
                            }
                            total += Double.parseDouble(entry.getRealAverage2());
                            fcount++;
                        }
                        break;
                    case 3:
                        if (!entry.getZeugnis().contentEquals("")) {
                            schn = Double.parseDouble(entry.getZeugnis());
                            if (schn < 4) {
                                minus += 4 - schn;
                                total_minus++;
                            }
                            total += schn;
                            fcount++;
                        }
                        break;
                }
            }
        }
        if (!((total / fcount) < 4)) {
            if (total_minus <= 3) {
                sMessage = "Promoviert";
                iColor = R.color.promo_white;
            } else {
                sMessage = "Nicht promoviert\nMehr als 3 Ungenügende";
                iColor = R.color.promo_black;
            }
            if (minus > 2.5) {
                sMessage = "Nicht Promoviert\nMehr als 2.5 Minuspunkte";
                iColor = R.color.promo_black;
            }
        } else {
            sMessage = "Nicht promoviert\nGesamtschnitt unter 4";
            iColor = R.color.promo_black;
        }

        if (fcount > 0) {
            sSchnitt = (String.format(Locale.getDefault(), "%.4f", total / fcount));
        } else {
            sSchnitt = ("-");
        }

        // HMS, also keine Pluspunkte anzeigen
        sPP = sSchnitt;
        if (iSemester == 3) {
            sPP = "-";
        }

        return new PromoRes(sMessage, iColor, sPP, sSchnitt, getKont(faecher, iSemester));
    }

    private PromoRes getFMS(int iSemester) {
        Fach entry;
        double minus = 0;
        double schn;
        double total = 0;
        int fcount = 0;

        String sMessage;
        int iColor;
        String sPP;
        String sSchnitt;

        DatabaseHandler db = new DatabaseHandler(context);
        int count = db.getFachCount();
        List<Fach> faecher = db.getAllFaecher(context, 2);
        for (int i = 0; i < count; i++) {
            entry = faecher.get(i);
            if (entry.getPromotionsrelevant().contentEquals("true")) {

                switch (iSemester) {
                    case 1:
                        // Falls erstes Semester ausgefüllt
                        if (!entry.getRealAverage1().contentEquals("")) {
                            if (Double.parseDouble(entry.getRealAverage1()) < 4) {
                                minus += (4 - Double.parseDouble(entry.getRealAverage1()));
                            }
                            total += Double.parseDouble(entry.getRealAverage1());
                            fcount++;
                        }
                        break;
                    case 2:
                        // Falls zweites Semester ausgefüllt
                        if (!entry.getRealAverage2().contentEquals("")) {
                            if (Double.parseDouble(entry.getRealAverage2()) < 4) {
                                minus += 4 - Double.parseDouble(entry.getRealAverage2());
                            }
                            total += Double.parseDouble(entry.getRealAverage2());
                            fcount++;
                        }
                        break;
                    case 3:
                        if (!entry.getZeugnis().contentEquals("")) {
                            schn = Double.parseDouble(entry.getZeugnis());
                            if (schn < 4) {
                                minus += 4 - schn;
                            }
                            total += schn;
                            fcount++;
                        }
                        break;
                }
            }
        }
        if (!(minus > 2.5)) {
            if (!((total / fcount) < 4)) {
                sMessage = "Promoviert";
                iColor = R.color.promo_white;
            } else {
                sMessage = "Nicht Promoviert\nPromotionsschnitt unter 4";
                iColor = R.color.promo_black;
            }
        } else {
            sMessage = "Nicht promoviert\nMehr als 2.5 Minuspunkte";
            iColor = R.color.promo_black;
        }

        if (fcount > 0) {
            sSchnitt = (String.format(Locale.getDefault(), "%.4f", total / fcount));
        } else {
            sSchnitt = ("-");
        }

        // FMS, also keine Pluspunkte anzeigen
        sPP = sSchnitt;
        if (iSemester == 3) {
            sPP = "-";
        }
        return new PromoRes(sMessage, iColor, sPP, sSchnitt, getKont(faecher, iSemester));
    }

    private String getKont(List<Fach> subjects, int semester) {
        int totalK = 0;
        int used = 0;
        double dPercentage = 0;

        Fach entry;
        String kontUsed;

        for (int i = 0; i < subjects.size(); i++) {
            entry = subjects.get(i);
            if (semester == 1) {
                kontUsed = entry.getKont1();
            } else {
                kontUsed = entry.getKont2();
            }
            if (!entry.getKontAvailable().contentEquals("")) {
                totalK = totalK + Integer.parseInt(entry.getKontAvailable());
            }
            if (!kontUsed.contentEquals("")) {
                used += Util.getAmoutUsed(kontUsed);
            }
        }

        if (!(totalK == 0)) {
            dPercentage = (double) Math.round((double) used * 100 / totalK * 100) / 100;
        }
        return (used + "/" + totalK);
    }
}