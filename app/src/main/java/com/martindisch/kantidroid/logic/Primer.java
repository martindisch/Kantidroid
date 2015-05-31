package com.martindisch.kantidroid.logic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.martindisch.kantidroid.R;
import com.martindisch.kantidroid.ui.main.MainActivity;
import com.martindisch.kantidroid.ui.overview.OverviewFragment;

public class Primer {

    public static void runOnFirstTime(final Context context) {
        // Run all of this in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = context.getSharedPreferences("Primer", Context.MODE_PRIVATE);
                if (!sp.getBoolean("opened", false)) {
                    // Do stuff on first startup
                    // removePrefsDB(context);

                    // Remember first startup
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("opened", true);
                    editor.commit();
                }

            }
        }).start();
    }

    public static void runEveryTime(final Context context) {
        DatabaseHandler db = new DatabaseHandler(context);
        if (db.getAllFaecher(context, 1).size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.ask_autosubjects);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            // TODO: Maybe set kont_av as well

                            DatabaseHandler db = new DatabaseHandler(context);
                            Fach subject;
                            String[] subjects = context.getResources().getStringArray(R.array.subjects_standard);
                            String[] subjects_short = context.getResources().getStringArray(R.array.subjects_standard_short);
                            for (int y = 0; y < subjects.length; y++) {
                                subject = new Fach(subjects[y], "true");
                                subject.setShort(subjects_short[y]);
                                subject.setColor(y + "");
                                db.addFach(subject);
                            }

                            // Let MainActivity reload the data after we've created new subjects
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((OverviewFragment) fragmentManager.findFragmentByTag(String.valueOf(1))).loadData();
                                }
                            });
                        }
                    }).start();
                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        }
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
