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

import com.martin.noten.DatabaseHandler;
import com.martin.noten.Fach;
import com.martin.noten.PromoCheck;
import com.martin.noten.PromoRes;

public class WidgetProvider extends AppWidgetProvider {

	double schn = 0;
	Fach entry;
	Resources res;
<<<<<<< HEAD
	
=======

>>>>>>> beta
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		res = context.getResources();
<<<<<<< HEAD
		
		final int wcount = appWidgetIds.length;
		
		// Iterate through all instances of the widget
		for (int y = 0; y < wcount; y++) {
			// Get the intent for a click-event
			Intent intent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			
			// Get the views for the widget
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
			// set the intent for the click-event
			views.setOnClickPendingIntent(R.id.wLayout, pendingIntent);
			
			// Noten

			double plus = 0;
			double minus = 0;
			int total_minus = 0;
			double total = 0;
			int fcount = 0;
			schn = 0;
			boolean critical = false;
			DatabaseHandler db = new DatabaseHandler(context);
			int count = db.getFachCount();
			List<Fach> faecher = db.getAllFaecher(context, 3);
			for (int i = 0; i < count; i++) {
				entry = faecher.get(i);
				if (entry.getPromotionsrelevant().contentEquals("true")) {
					// Falls beide Semester ausgefüllt
					critical = true;
					if (!entry.getRealAverage1().contentEquals("-")
							&& !entry.getRealAverage2().contentEquals("-")) {
						schn = (Double.parseDouble(entry.getRealAverage1()) + Double
								.parseDouble(entry.getRealAverage2())) / 2;
						if (schn > 4) {
							plus += schn - 4;
						}
						if (schn < 4) {
							minus += 4 - schn;
							total_minus++;
						}
						total += (Double.parseDouble(entry.getMathAverage1()) + Double
								.parseDouble(entry.getMathAverage2())) / 2;
						fcount++;
					}

					// Falls nur erstes Semester ausgefüllt
					if (entry.getRealAverage2().contentEquals("-")
							&& !entry.getRealAverage1().contentEquals("-")) {
						if (Double.parseDouble(entry.getRealAverage1()) > 4) {
							plus += (Double.parseDouble(entry.getRealAverage1()) - 4);
						}
						if (Double.parseDouble(entry.getRealAverage1()) < 4) {
							minus += (4 - Double.parseDouble(entry
									.getRealAverage1()));
							total_minus++;
						}
						total += Double.parseDouble(entry.getMathAverage1());
						fcount++;
					}

					// Falls nur zweites Semester ausgefüllt
					critical = true;
					if (!entry.getRealAverage2().contentEquals("-")
							&& entry.getRealAverage1().contentEquals("-")) {
						if (Double.parseDouble(entry.getRealAverage2()) > 4) {
							plus += Double.parseDouble(entry.getRealAverage2()) - 4;
						}
						if (Double.parseDouble(entry.getRealAverage2()) < 4) {
							minus += 4 - Double
									.parseDouble(entry.getRealAverage2());
							total_minus++;
						}
						total += Double.parseDouble(entry.getMathAverage2());
						fcount++;
					}
				}
			}
			if (!((minus * 2) > plus)) {
				if (total_minus <= 3) {
					views.setTextColor(R.id.tvPP, res.getColor(R.color.holo_green_light));
				}
				if (total_minus == 4) {
					views.setTextColor(R.id.tvPP, res.getColor(R.color.holo_orange_light));
				}
				if (total_minus > 4 && !critical) {
					views.setTextColor(R.id.tvPP, res.getColor(R.color.holo_orange_light));
				}
				if (total_minus > 4 && critical) {
					views.setTextColor(R.id.tvPP, res.getColor(R.color.holo_red_light));
				}

			} else {
				views.setTextColor(R.id.tvPP, res.getColor(R.color.holo_red_light));
			}

			double PP_result = plus - (2 * minus);
			views.setTextViewText(R.id.tvPP, PP_result + "");
			
			SharedPreferences spNoten = context.getSharedPreferences("MarkSettings", context.MODE_PRIVATE);
=======

		final int wcount = appWidgetIds.length;

		// Iterate through all instances of the widget
		for (int y = 0; y < wcount; y++) {
			// Get the intent for a click-event
			Intent notIntent = new Intent(context, com.martin.noten.Main.class);
			Intent kontIntent = new Intent(context,
					com.martin.kontingent.Overview.class);
			Intent kissIntent = new Intent(context,
					com.martin.kiss.MainActivity.class);
			Intent acnotIntent = new Intent(context,
					com.martin.noten.AddSelect.class);
			Intent ackontIntent = new Intent(context,
					com.martin.kontingent.AddSelect.class);

			PendingIntent penNotIntent = PendingIntent.getActivity(context, 0,
					notIntent, 0);
			PendingIntent penKontIntent = PendingIntent.getActivity(context, 0,
					kontIntent, 0);
			PendingIntent penKissIntent = PendingIntent.getActivity(context, 0,
					kissIntent, 0);
			PendingIntent penacNotIntent = PendingIntent.getActivity(context,
					0, acnotIntent, 0);
			PendingIntent penacKontIntent = PendingIntent.getActivity(context,
					0, ackontIntent, 0);

			// Get the views for the widget
			RemoteViews views = new RemoteViews(context.getPackageName(),
					R.layout.widget_layout);
			// set the intent for the click-event
			views.setOnClickPendingIntent(R.id.llCardNoten, penNotIntent);
			views.setOnClickPendingIntent(R.id.llCardKontingent, penKontIntent);
			views.setOnClickPendingIntent(R.id.llCardKISS, penKissIntent);
			views.setOnClickPendingIntent(R.id.ibNotenAdd, penacNotIntent);
			views.setOnClickPendingIntent(R.id.ibKontAdd, penacKontIntent);

			// Noten

			SharedPreferences spNoten = context.getSharedPreferences(
					"MarkSettings", context.MODE_PRIVATE);
>>>>>>> beta
			String sAbteilung = spNoten.getString("Abteilung", "Gym");
			PromoCheck prCheck = new PromoCheck(context);
			PromoRes prResult = null;
			if (sAbteilung.contentEquals("Gym")) {
				prResult = prCheck.getGym(2);
<<<<<<< HEAD
			}
			else if (sAbteilung.contentEquals("HMS")) {
				prResult = prCheck.getHMS(2);
			}
			else {
				prResult = prCheck.getFMS(2);
			}
			views.setTextColor(R.id.tvPP, res.getColor(prResult.iColor));
			views.setTextViewText(R.id.tvPP, prResult.sPP.replace(" Pluspunkte", ""));
			
			// Kontingent
			
			com.example.kontingentprototype.DatabaseHandler dbK = new com.example.kontingentprototype.DatabaseHandler(
=======
			} else if (sAbteilung.contentEquals("HMS")) {
				prResult = prCheck.getHMS(2);
			} else {
				prResult = prCheck.getFMS(2);
			}
			views.setTextViewText(R.id.tvNotBig, "+ "
					+ prResult.sPP.split("/")[0]);
			/*
			 * if (prResult.iColor == R.color.holo_green_light) {
			 * pluspunkte.setTextColor(res.getColor(R.color.holo_orange_light));
			 * } else { pluspunkte.setTextColor(res.getColor(prResult.iColor));
			 * }
			 */
			views.setTextViewText(R.id.tvNotSmall, "Ø " + prResult.sSchnitt);

			// Kontingent

			com.martin.kontingent.DatabaseHandler dbK = new com.martin.kontingent.DatabaseHandler(
>>>>>>> beta
					context);
			int countK = dbK.getFachCount();

			int totalK = 0;
			int used = 0;
<<<<<<< HEAD
			int percentage = 0;

			List<com.example.kontingentprototype.Fach> faecherK = dbK
					.getAllFaecher(context);
			com.example.kontingentprototype.Fach entry = null;
=======
			int iPercentage = 0;
			double dPercentage = 0;

			List<com.martin.kontingent.Fach> faecherK = dbK
					.getAllFaecher(context);
			com.martin.kontingent.Fach entry = null;
>>>>>>> beta

			int überzogen = 0;

			for (int i = 0; i < countK; i++) {
				entry = faecherK.get(i);
				if (!entry.getKont_av().contentEquals("-")) {
					totalK = totalK + Integer.parseInt(entry.getKont_av());
				}
				if (!entry.getKont_us().contentEquals("-")) {
					used = used + Integer.parseInt(entry.getKont_us());

					// Check für überzogen
					if (Integer.parseInt(entry.getKont_us()) > Integer
							.parseInt(entry.getKont_av())) {
						überzogen++;
					}
				}
			}

			if (!(totalK == 0)) {
<<<<<<< HEAD
				percentage = (int) ((used * 100) / totalK);
			}

			if (überzogen == 0) {
				views.setTextColor(R.id.tvKont, res.getColor(R.color.holo_green_light));
			} else {
				views.setTextColor(R.id.tvKont, res.getColor(R.color.holo_red_light));
			}

			views.setTextViewText(R.id.tvKont, used + "/" + totalK);
			
=======
				iPercentage = (int) ((used * 100) / totalK);
				dPercentage = (double) Math.round((double) used * 100 / totalK
						* 100) / 100;
			}
			views.setTextViewText(R.id.tvKontSmall, dPercentage + "%");
			views.setTextViewText(R.id.tvKontBig, used + "/" + totalK);
			/*
			 * if (überzogen == 0) {
			 * tvUsage.setTextColor(res.getColor(R.color.holo_orange_light)); }
			 * else {
			 * tvUsage.setTextColor(res.getColor(R.color.holo_red_light));
			 * String before = tvUsage.getText().toString(); String einzmehrz =
			 * " Fach überzogen"; if (überzogen > 1) { einzmehrz =
			 * " Fächern überzogen"; } tvUsage.setText(before +
			 * "\nKontingent in " + überzogen + einzmehrz); }
			 */

>>>>>>> beta
			// KISS

			SharedPreferences spKISS = context.getSharedPreferences("KISS",
					context.MODE_PRIVATE);
			String sLehrer = spKISS.getString("lehrer", "");

			if (!sLehrer.contentEquals("")) {
				String[] entries = sLehrer.split("-");
<<<<<<< HEAD
				String[] imKISS = { "", ""};
=======
				String[] imKISS = { "", "" };
>>>>>>> beta
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
<<<<<<< HEAD
					views.setTextColor(R.id.tvKISS_1, res.getColor(R.color.primary_text_holo_light));
					views.setTextViewText(R.id.tvKISS_1, "-");
					views.setTextViewText(R.id.tvKISS_2, "");
					break;
				case 1:
					views.setTextColor(R.id.tvKISS_1, res.getColor(R.color.holo_red_light));
					views.setTextViewText(R.id.tvKISS_1, imKISS[0] + "");
					views.setTextViewText(R.id.tvKISS_2, "");
					break;
				case 2:
					views.setTextColor(R.id.tvKISS_1, res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_2, res.getColor(R.color.holo_red_light));
					views.setTextViewText(R.id.tvKISS_1, imKISS[0] + "");
					views.setTextViewText(R.id.tvKISS_2, imKISS[1] + "");
					break;
				}
			} 
=======
					views.setTextColor(R.id.tvKISS_1_name,
							res.getColor(R.color.primary_text_holo_light));
					views.setTextColor(R.id.tvKISS_1_message,
							res.getColor(R.color.primary_text_holo_light));
					views.setTextViewText(R.id.tvKISS_1_name, "-");
					views.setTextViewText(R.id.tvKISS_1_message, "");
					views.setTextViewText(R.id.tvKISS_2, "");
					break;
				case 1:
					views.setTextColor(R.id.tvKISS_1_name,
							res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_1_message,
							res.getColor(R.color.holo_red_light));
					views.setTextViewText(R.id.tvKISS_1_name, imKISS[0]);
					views.setTextViewText(R.id.tvKISS_1_message,
							" ist im KISS gelistet");
					views.setTextViewText(R.id.tvKISS_2, "");
					break;
				case 2:
					views.setTextColor(R.id.tvKISS_1_name,
							res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_1_message,
							res.getColor(R.color.holo_red_light));
					views.setTextColor(R.id.tvKISS_2,
							res.getColor(R.color.holo_red_light));
					views.setTextViewText(R.id.tvKISS_1_message,
							" ist im KISS gelistet");
					views.setTextViewText(R.id.tvKISS_2, "");
					views.setTextViewText(R.id.tvKISS_2,
							"Weitere sind ebenfalls im KISS gelistet");
					break;
				}
			}
>>>>>>> beta
			appWidgetManager.updateAppWidget(appWidgetIds[y], views);
		}
	}
}
