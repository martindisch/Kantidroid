package com.martin.kantidroid.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.martin.kantidroid.R;

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

                    DatabaseHandler db = new DatabaseHandler(context);
                    Fach subject;
                    String[] subjects = context.getResources().getStringArray(R.array.subjects_standard);
                    String[] subjects_short = context.getResources().getStringArray(R.array.subjects_standard_short);
                    for (int i = 0; i < subjects.length; i++) {
                        subject = new Fach(subjects[i], "true");
                        subject.setShort(subjects_short[i]);
                        subject.setCol
                        db.addFach(subject);
                    }

                    // Remember first startup
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("opened", true);
                    editor.commit();
                }

            }
        }).start();
    }

    private static void removePrefsDB(Context context) {
        String[] prefs = context.getResources().getStringArray(R.array.old_prefs);
        String[] db = context.getResources().getStringArray(R.array.old_db);

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
