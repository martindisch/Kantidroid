package com.martin.kantidroid;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.RemoteViews;

import com.martin.noten.Fach;
import com.martin.noten.PromoCheck;
import com.martin.noten.PromoRes;

public class WidgetProvider extends AppWidgetProvider {

	double schn = 0;
	Fach entry;
	Resources res;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		res = context.getResources();

		final int wcount = appWidgetIds.length;

		// Iterate through all instances of the widget
		for (int y = 0; y < wcount; y++) {
			// Get the intent for a click-event
			Intent notIntent = new Intent(context, com.martin.noten.Main.class);
			Intent kontIntent = new Intent(context, com.martin.kontingent.Overview.class);
			Intent kissIntent = new Intent(context, com.martin.kiss.MainActivity.class);
			Intent acnotIntent = new Intent(context, com.martin.noten.AddSelect.class);
			Intent ackontIntent = new Intent(context, com.martin.kontingent.AddSelect.class);

			PendingIntent penNotIntent = PendingIntent.getActivity(context, 0, notIntent, 0);
			PendingIntent penKontIntent = PendingIntent.getActivity(context, 0, kontIntent, 0);
			PendingIntent penKissIntent = PendingIntent.getActivity(context, 0, kissIntent, 0);
			PendingIntent penacNotIntent = PendingIntent.getActivity(context, 0, acnotIntent, 0);
			PendingIntent penacKontIntent = PendingIntent.getActivity(context, 0, ackontIntent, 0);

			// Get the views for the widget
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			// set the intent for the click-event
			views.setOnClickPendingIntent(R.id.llCardNoten, penNotIntent);
			views.setOnClickPendingIntent(R.id.llCardKontingent, penKontIntent);
			views.setOnClickPendingIntent(R.id.llCardKISS, penKissIntent);
			views.setOnClickPendingIntent(R.id.ibNotenAdd, penacNotIntent);
			views.setOnClickPendingIntent(R.id.ibKontAdd, penacKontIntent);

			// Noten

			SharedPreferences spNoten = context.getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
			String sAbteilung = spNoten.getString("Abteilung", "Gym");
			PromoCheck prCheck = new PromoCheck(context);
			PromoRes prResult = null;
			if (sAbteilung.contentEquals("Gym")) {
				prResult = prCheck.getGym(2);
			} else if (sAbteilung.contentEquals("HMS")) {
				prResult = prCheck.getHMS(2);
			} else {
				prResult = prCheck.getFMS(2);
			}
			views.setTextViewText(R.id.tvNotBig, "+ " + prResult.sPP.split("/")[0]);
			/*
			 * if (prResult.iColor == R.color.holo_green_light) {
			 * pluspunkte.setTextColor(res.getColor(R.color.holo_orange_light));
			 * } else { pluspunkte.setTextColor(res.getColor(prResult.iColor));
			 * }
			 */
			views.setTextViewText(R.id.tvNotSmall, "Ø " + prResult.sSchnitt);

			// Kontingent

			com.martin.kontingent.DatabaseHandler dbK = new com.martin.kontingent.DatabaseHandler(context);
			int countK = dbK.getFachCount();

			int totalK = 0;
			int used = 0;
			double dPercentage = 0;

			List<com.martin.kontingent.Fach> faecherK = dbK.getAllFaecher(context);
			com.martin.kontingent.Fach entry = null;

			for (int i = 0; i < countK; i++) {
				entry = faecherK.get(i);
				if (!entry.getKont_av().contentEquals("-")) {
					totalK = totalK + Integer.parseInt(entry.getKont_av());
				}
				if (!entry.getKont_us().contentEquals("-")) {
					used = used + Integer.parseInt(entry.getKont_us());
				}
			}

			if (!(totalK == 0)) {
				dPercentage = (double) Math.round((double) used * 100 / totalK * 100) / 100;
			}
			views.setTextViewText(R.id.tvKontSmall, dPercentage + "%");
			views.setTextViewText(R.id.tvKontBig, used + "/" + totalK);

			// KISS

			SharedPreferences spKISS = context.getSharedPreferences("KISS", Context.MODE_PRIVATE);
			String sLehrer = spKISS.getString("lehrer", "");

			if (!sLehrer.contentEquals("")) {
				String[] entries = sLehrer.split("-");
				String[] imKISS = { "", "" };
				int c = 0;
				for (int i = 0; i < entries.length; i++) {
					String current_kiss = spKISS.getString("KISS", "");

					if (current_kiss.contains(entries[i]) && c < 2) {
						imKISS[c] = entries[i];
						c++; // ftw :D
					}
				}
				switch (c) {
				case 0:
					views.setTextColor(R.id.tvKISS_1_name, res.getColor(R.color.primary_text_holo_light));
					views.setTextColor(R.id.tvKISS_1_message, res.getColor(R.color.primary_text_holo_light));
					views.setTextViewText(R.id.tvKISS_1_name, "-");
					views.setTextViewText(R.id.tvKISS_1_message, "");
					views.setTextViewText(R.id.tvKISS_2, "");
					break;
				case 1:
					views.setTextColor(R.id.tvKISS_1_name, res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_1_message, res.getColor(R.color.holo_red_light));
					views.setTextViewText(R.id.tvKISS_1_name, imKISS[0]);
					views.setTextViewText(R.id.tvKISS_1_message, " ist im KISS gelistet");
					views.setTextViewText(R.id.tvKISS_2, "");
					break;
				case 2:
					views.setTextColor(R.id.tvKISS_1_name, res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_1_message, res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_2, res.getColor(R.color.holo_red_light));
					views.setTextViewText(R.id.tvKISS_1_message, " ist im KISS gelistet");
					views.setTextViewText(R.id.tvKISS_2, "");
					views.setTextViewText(R.id.tvKISS_2, "Weitere sind ebenfalls im KISS gelistet");
					break;
				}
			}
			appWidgetManager.updateAppWidget(appWidgetIds[y], views);
		}
	}
}
