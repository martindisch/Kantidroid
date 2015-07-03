package com.martin.kantidroid.logic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.martin.kantidroid.R;
import com.martin.kantidroid.ui.main.MainActivity;
import com.martin.kantidroid.ui.overview.OverviewFragment;
import com.martin.kantidroid.ui.util.ChangelogFragment;

public class Primer {

    public static void runOnFirstTime(final Context context) {
        // Run all of this in a separate thread
        new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sp = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
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
                            DatabaseHandler db = new DatabaseHandler(context);
                            Fach subject;
                            String[] subjects = context.getResources().getStringArray(R.array.subjects_standard);
                            String[] subjects_short = context.getResources().getStringArray(R.array.subjects_standard_short);
                            for (int y = 0; y < subjects.length; y++) {
                                subject = new Fach(subjects[y], "true");
                                subject.setShort(subjects_short[y]);
                                subject.setColor(y + "");
                                subject.setKontAvailable("4");
                                db.addFach(subject);
                            }

                            // Let MainActivity reload the data after we've created new subjects
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            ((MainActivity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((OverviewFragment) fragmentManager.findFragmentByTag("overview")).loadData();

                                }
                            });
                        }
                    }).start();
                }
            });
            builder.setNegativeButton(R.string.no, null);
            builder.show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
                        if (!Util.getSeen(context, versionCode + "")) {
                            ChangelogFragment changelog = new ChangelogFragment();
                            changelog.show(((MainActivity) context).getSupportFragmentManager(), "changelog");
                            Util.setSeen(context, versionCode + "");
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
