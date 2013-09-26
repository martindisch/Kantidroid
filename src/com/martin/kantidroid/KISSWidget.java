package com.martin.kantidroid;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class KISSWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		// Get the intent for a click-event
		Intent intent = new Intent(context, com.martin.kiss.MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);

		// Get the views for the widget
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_kiss_layout);
		// set the intent for the click-event
		views.setOnClickPendingIntent(R.id.wkiss_layout, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds[0], views);
	}

}