package com.martin.kantidroid.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Primer {

    public static void runOnFirstTime(final Context context) {
        // Run all of this in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = context.getSharedPreferences("Primer", Context.MODE_PRIVATE);
                if (!sp.getBoolean("opened", false)) {
                    // Do stuff on first startup
                    removePrefsDB(context);

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("opened", true);
                    editor.commit();
                }

            }
        }).start();
    }

    private static void removePrefsDB(Context context) {
        String[] prefs = new String[] {
                "check",
                "MarkSettings",
                "KISS",
                "Kantidroid",
                "Rem",
                "mysettings"
        };
        String[] db = new String[] {
                "kontManager",
                "markManager",
        };

        SharedPreferences sp;
        SharedPreferences.Editor editor;
        for (int i = 0; i < prefs.length; i++) {
            sp = context.getSharedPreferences(prefs[i], Context.MODE_PRIVATE);
            editor = sp.edit();
            editor.clear();
            editor.commit();
        }

        for (int i = 0; i < db.length; i++) {
            context.deleteDatabase(db[i]);
        }
    }

    // TODO: Add getSeen/setSeen here
}
