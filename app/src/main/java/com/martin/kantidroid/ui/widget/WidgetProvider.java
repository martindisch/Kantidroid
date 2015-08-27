package com.martin.kantidroid.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.main.MainActivity;

import java.io.File;

public class WidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach on-click listeners
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.llTop, pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_pp, pendingIntent);
            views.setOnClickPendingIntent(R.id.widget_kont, pendingIntent);

            SharedPreferences prefs = context.getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
            File f = new File(prefs.getString("last_timetable", "nope"));
            if (f.exists()) {
                String className = f.getName().replace(".pdf", "");
                views.setTextViewText(R.id.widget_timetable, className);
                MimeTypeMap myMime = MimeTypeMap.getSingleton();
                Intent newIntent = new Intent(Intent.ACTION_VIEW);
                String mimeType = myMime.getMimeTypeFromExtension(Util.fileExt(f).substring(1));
                newIntent.setDataAndType(Uri.fromFile(f), mimeType);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pdfIntent = PendingIntent.getActivity(context, 0, newIntent, 0);
                views.setOnClickPendingIntent(R.id.widget_timetable, pdfIntent);
            } else {
                views.setTextViewText(R.id.widget_timetable, "-");
            }

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
