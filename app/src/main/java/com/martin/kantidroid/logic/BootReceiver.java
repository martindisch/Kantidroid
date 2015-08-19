package com.martin.kantidroid.logic;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("FFF", "BootReceiver receiving something");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("FFF", "Receiving boot completed, setting alarm manager");
            Intent newIntent = new Intent(context.getApplicationContext(), Background.class);
            PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
    }
}
