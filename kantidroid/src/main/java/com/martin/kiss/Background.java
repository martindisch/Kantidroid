package com.martin.kiss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.DateUtils;
import android.text.format.Time;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.MOTD;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class Background extends IntentService {

	public Background() {
		super(Background.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (isNetworkAvailable()) {
			String result = "";
			String motd = "";
			try {
				URL url = new URL("https://kiss.bks-campus.ch/infoscreen");
				URL urlmotd = new URL("http://androiddev.bplaced.net/motd.txt");

				// KISS
				
				// Why can't they have certificates that are valid for their hostnames?!
				HostnameVerifier hostnameVerifier = new HostnameVerifier() {
				    @Override
				    public boolean verify(String hostname, SSLSession session) {
				        return true;
				    }
				};
				
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setHostnameVerifier(hostnameVerifier);
				InputStream in = con.getInputStream();
				BufferedReader reader = null;
				reader = new BufferedReader(new InputStreamReader(in));
				String line = "";
				while ((line = reader.readLine()) != null) {
					result += line;
				}

				// MOTD
				HttpURLConnection conh = (HttpURLConnection) urlmotd.openConnection();
				in = conh.getInputStream();
				reader = null;
				reader = new BufferedReader(new InputStreamReader(in));
				line = "";
				while ((line = reader.readLine()) != null) {
					motd += line;
				}
				if (reader != null) {
					reader.close();
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
			if (!result.contentEquals("")) {
				getApplicationContext();
				SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spKISS.edit();
				editor.putString("KISS", result);
				editor.putString("last_refresh", DateFormat.getDateTimeInstance().format(new Date()));
				editor.commit();
				checkLehrer();
			}
			if (!motd.contentEquals("")) {
				checkMOTD(motd);
			}
		}
		ScheduleNextUpdate();
	}

	private void checkMOTD(String motd) {
		String[] lines = motd.split("/");
		SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
		Check check = new Check();
		if (!check.getSeen(lines[0], this) && !lines[0].contains("html") && !lines[1].contains("html")) {
			int idCounter = spKISS.getInt("idCounter", 0);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
			mBuilder.setSmallIcon(R.drawable.kantidroid_ico);
			mBuilder.setContentTitle(lines[0]);
			mBuilder.setContentText(lines[1]);
			mBuilder.setAutoCancel(true);
			long[] pattern = { 0, 300, 200, 300 };
			mBuilder.setVibrate(pattern);
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
			SharedPreferences.Editor ieditor = spKISS.edit();
			ieditor.putInt("idCounter", idCounter);
			ieditor.commit();
			check.setSeen(lines[0], this);
		}

	}

	private void ScheduleNextUpdate() {
		SharedPreferences spKISS = this.getSharedPreferences("KISS", Context.MODE_PRIVATE);
		Intent intent = new Intent(this, this.getClass());
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long currentTimeMillis = System.currentTimeMillis();
		long nextUpdateTimeMillis = currentTimeMillis + spKISS.getInt("interval", 40) * DateUtils.MINUTE_IN_MILLIS;
		Time nextUpdateTime = new Time();
		nextUpdateTime.set(nextUpdateTimeMillis);

		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
	}

	private void checkLehrer() {
		SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
		String sLehrer = spKISS.getString("lehrer", "");
		String sKISS = spKISS.getString("KISS", "");
		String sNoti = spKISS.getString("noti", "");
		String sNewList = sNoti;

		if (!sLehrer.contentEquals("")) {
			String[] entries = sLehrer.split("-");

			if (!sNoti.contentEquals("")) {
				String[] sNotiList = sNoti.split("-");
				for (int c = 0; c < sNotiList.length; c++) {
					if (!sKISS.contains(sNotiList[c])) {
						if (sNoti.contains("-" + sNotiList[c])) {
							sNewList = sNoti.replace("-" + sNotiList[c], "");
						} else if (sNoti.contains(sNotiList[c] + "-")) {
							sNewList = sNoti.replace(sNotiList[c] + "-", "");
						} else {
							sNewList = "";
						}
					}
				}
				SharedPreferences.Editor editor = spKISS.edit();
				editor.putString("noti", sNewList);
				editor.commit();

			}

			int idCounter = spKISS.getInt("idCounter", 0);

			sNoti = spKISS.getString("noti", "");
			for (int i = 0; i < entries.length; i++) {
				if (sKISS.contains(entries[i]) && !sNoti.contains(entries[i])) {
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
					mBuilder.setSmallIcon(R.drawable.kiss_ico);
					mBuilder.setContentTitle("KISS");
					mBuilder.setContentText(entries[i] + " ist im KISS verzeichnet.");
					mBuilder.setAutoCancel(true);
					long[] pattern = { 0, 300, 200, 300 };
					mBuilder.setVibrate(pattern);
					mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
					mBuilder.setDefaults(Notification.DEFAULT_SOUND);
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
					stackBuilder.addParentStack(MainActivity.class);
					String url = "https://kiss.bks-campus.ch/infoscreen";
					Intent iKISS = new Intent(Intent.ACTION_VIEW);
					iKISS.setData(Uri.parse(url));
					stackBuilder.addNextIntent(iKISS);
					PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					mNotificationManager.notify(idCounter, mBuilder.build());
					idCounter++;

					// Remember notification
					SharedPreferences.Editor ieditor = spKISS.edit();
					String oldNoti = spKISS.getString("noti", "");
					if (!oldNoti.contentEquals("")) {
						ieditor.putString("noti", oldNoti + "-" + entries[i]);
					} else {
						ieditor.putString("noti", entries[i]);
					}
					ieditor.putInt("idCounter", idCounter);
					ieditor.commit();
					getApplicationContext();
					// Remember
					SharedPreferences spRem = getApplicationContext().getSharedPreferences("Rem", Context.MODE_PRIVATE);
					SharedPreferences.Editor RemEdit = spRem.edit();
					int iAusfaelle = spRem.getInt(entries[i], 0);
					iAusfaelle++;
					RemEdit.putInt(entries[i], iAusfaelle);
					RemEdit.commit();
				}
			}
			Intent rIntent = new Intent(this, WidgetProvider.class);
			rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
			int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
			rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
			sendBroadcast(rIntent);
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
