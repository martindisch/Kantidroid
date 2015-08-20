package com.martin.kantidroid.logic;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.martin.kantidroid.R;
import com.martin.kantidroid.ui.main.MainActivity;
import com.martin.kantidroid.ui.motd.MOTD;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Background extends IntentService {

    public Background() {
        super(Background.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Util.isNetworkAvailable(getApplicationContext())) {
            String motd = "";
            try {
                URL urlmotd = new URL(getString(R.string.motd_url));
                InputStream in;
                BufferedReader reader;

                HttpURLConnection conh = (HttpURLConnection) urlmotd.openConnection();
                in = conh.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    motd += line;
                }
                reader.close();
                if (!motd.contentEquals("")) {
                    checkMOTD(motd);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkMOTD(String motd) {
        String[] lines = motd.split("/");
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        if (!Util.getSeen(getApplicationContext(), lines[0]) && !lines[0].contains("html") && !lines[1].contains("html")) {
            int idCounter = prefs.getInt("idCounter", 0);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_comment);
            mBuilder.setContentTitle(lines[0]);
            mBuilder.setContentText(lines[1].replace("*", " "));
            mBuilder.setAutoCancel(true);
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            Intent iMOTD = new Intent(Background.this, MOTD.class);
            Bundle data = new Bundle();
            data.putStringArray("data", lines);
            iMOTD.putExtras(data);
            stackBuilder.addNextIntent(iMOTD);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(idCounter, mBuilder.build());
            idCounter++;

            // Remember notification id
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("idCounter", idCounter);
            editor.commit();
            Util.setSeen(getApplicationContext(), lines[0]);
        }
    }
}