package com.martin.kantidroid.logic;

import android.content.Context;
import android.graphics.Color;

import com.martin.kantidroid.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
        } else {
            if (used.contentEquals("")) {
                formatted = "0/" + available;
            } else {
                formatted = getAmoutUsed(used) + "/" + available;
            }
        }
        return formatted;
    }

    public static int getAmoutUsed(String kontSemester) {
        int used = 0;
        String[] used_splitted = kontSemester.split("\n");
        for (int i = 0; i < used_splitted.length; i++) {
            used += Integer.parseInt(used_splitted[i].split(" - ")[1]);
        }
        return used;
    }

    public static double getRequired(Context context, int id, int semester, String relevance, String goal) {
        DatabaseHandler db = new DatabaseHandler(context);
        Fach fach = db.getFach(id);

        double upper_term;
        double dRelevance = Double.parseDouble(relevance);
        double dGoal = Double.parseDouble(goal);

        if (semester == 1) {
            String sMarks = fach.getNoten1();

            String[] entries = sMarks.split("\n");
            int count = entries.length;
            double subtraktion = 0;
            double multiplikatoren = 0;

            for (int i = 0; i < count; i++) {
                String[] item = entries[i].split(" - ");
                subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
                multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
            }
            upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;
        } else {
            String sMarks = fach.getNoten2();

            String[] entries = sMarks.split("\n");
            int count = entries.length;
            double subtraktion = 0;
            double multiplikatoren = 0;

            for (int i = 0; i < count; i++) {
                String[] item = entries[i].split(" - ");
                subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
                multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
            }
            upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;
        }

        double needed = upper_term / dRelevance;

        BigDecimal bd = new BigDecimal(needed);
        return Double.parseDouble(bd.setScale(2, RoundingMode.HALF_UP).toString());
    }

    public static void setSeen(Context c, String key) {
        c.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).edit().putBoolean(key, true).commit();
    }

    public static boolean getSeen(Context c, String key) {
        return c.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).contains(key);
    }
}
