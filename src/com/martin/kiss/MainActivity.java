package com.martin.kiss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.widget.ArrayAdapter;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.Toast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class MainActivity extends ListActivity {

	String KISS = "";
	List<String> actualOrder;
	boolean done;
	ProgressDialog pd;

	@Override
	protected void onStop() {
		super.onStop();
		Intent rIntent = new Intent(this, WidgetProvider.class);
		rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
		rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(rIntent);
	}

	@Override
	protected void onCreate(Bundle sSavedInstanceState) {
		super.onCreate(sSavedInstanceState);
		pd = new ProgressDialog(this);
		pd.setMessage("KISS wird geladen...");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		}
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.kiss_main);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		if (getIntent().hasExtra("Internal_call")) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		actualOrder = new ArrayList<String>();
		createList();
		done = false;
	}

	private void startList() {
		ArrayList<Map<String, String>> list = buildData();
		String[] from = { "name", "ausfall" };
		int[] to = { R.id.tvLeft, R.id.tvRight };

		MyAdapter adapter = new MyAdapter(this, list, R.layout.overview_list_item, from, to);
		setListAdapter(adapter);
		done = true;
	}

	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		SharedPreferences spKISS = this.getSharedPreferences("KISS", Context.MODE_PRIVATE);

		String sLehrer = spKISS.getString("lehrer", "");

		if (!sLehrer.contentEquals("")) {
			String[] entries = sLehrer.split("-");
			// iterate through all entries and add active ones first
			for (int i = 0; i < entries.length; i++) {
				if (checkLehrer(entries[i]).contentEquals("Gelistet")) {
					list.add(putData(entries[i], "Gelistet"));
					actualOrder.add(entries[i]);
				}
			}
			// iterate a second time and append the inactive ones
			for (int i = 0; i < entries.length; i++) {
				if (checkLehrer(entries[i]).contentEquals("Nicht gelistet")) {
					list.add(putData(entries[i], "Nicht gelistet"));
					actualOrder.add(entries[i]);
				}
			}
		}
		return list;
	}

	private HashMap<String, String> putData(String name, String ausfall) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("name", name);
		item.put("ausfall", ausfall);
		return item;
	}

	private String checkLehrer(String name) {
		String ausfall = "Nicht gelistet";
		String current_kiss = KISS;

		if (KISS.contentEquals("")) {
			SharedPreferences spKISS = getSharedPreferences("KISS", Context.MODE_PRIVATE);
			current_kiss = spKISS.getString("KISS", "");
		}

		if (current_kiss.contains(name)) {
			ausfall = "Gelistet";
		}
		return ausfall;
	}

	private void createList() {
		final Handler handler = new Handler();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			pd.show();
		}

		Thread t = new Thread(new Runnable() {

			String result = "";

			@Override
			public void run() {
				try {
					if (isNetworkAvailable()) {
						getApplicationContext();
						SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
						if (!spKISS.getString("lehrer", "").contentEquals("")) {
							URL url = new URL("https://kpf.bks-campus.ch/infoscreen");
							HttpURLConnection con = (HttpURLConnection) url.openConnection();
							InputStream in = con.getInputStream();
							BufferedReader reader = null;
							reader = new BufferedReader(new InputStreamReader(in));
							String line = "";
							while ((line = reader.readLine()) != null) {
								result += line;
							}
							if (reader != null) {
								reader.close();
							}
						}
					} else {
						getApplicationContext();
						SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
						final String sDate = spKISS.getString("last_refresh", "never");
						KISS = "";
						if (!sDate.contentEquals("never")) {
							handler.post(new Runnable() {

								@Override
								public void run() {
									Toast t = Toast.makeText(getApplicationContext(), "Internet ist nicht verfügbar, greife auf Cache vom " + sDate + " zurück", Toast.LENGTH_SHORT);
									t.show();
								}

							});
						} else {
							handler.post(new Runnable() {

								@Override
								public void run() {
									Toast t = Toast.makeText(MainActivity.this, "Internet ist nicht verfügbar und es ist kein Cache vorhanden", Toast.LENGTH_SHORT);
									t.show();
								}

							});

						}

					}
				} catch (final Exception e) {
					KISS = "";
					getApplicationContext();
					SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
					final String sDate = spKISS.getString("last_refresh", "never");
					if (!sDate.contentEquals("never")) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								Toast t = Toast.makeText(getApplicationContext(), "Mit Netzwerk verbunden aber KISS nicht verfügbar, greife auf Cache vom " + sDate + " zurück", Toast.LENGTH_SHORT);
								t.show();
							}

						});

					} else {
						handler.post(new Runnable() {

							@Override
							public void run() {
								Toast t = Toast.makeText(getApplicationContext(), "Mit Netzwerk verbunden aber KISS nicht verfügbar und es ist kein Cache vorhanden", Toast.LENGTH_SHORT);
								t.show();
							}

						});

					}
				}
				if (!result.contentEquals("")) {
					getApplicationContext();
					SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = spKISS.edit();
					editor.putString("KISS", result);
					editor.putString("last_refresh", DateFormat.getDateTimeInstance().format(new Date()));
					editor.commit();
					KISS = result;
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						startList();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							setSupportProgressBarIndeterminateVisibility(false);
						} else {
							pd.hide();
						}
					}

				});
			}

		});
		t.start();
		checkLehrer();
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
					String url = "https://kpf.bks-campus.ch/infoscreen";
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
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.main_kiss, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iResetCounter:
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Zähler zurücksetzen");
			dg.setMessage("Willst du die 'Anzahl im KISS gestanden' für alle Personen zurücksetzen?");
			dg.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					getApplicationContext();
					SharedPreferences spRem = getApplicationContext().getSharedPreferences("Rem", Context.MODE_PRIVATE);
					SharedPreferences.Editor ed = spRem.edit();
					ed.clear();
					ed.commit();
					Toast t = Toast.makeText(getApplicationContext(), "Zähler zurückgesetzt", Toast.LENGTH_SHORT);
					t.show();
				}

			});
			dg.setNegativeButton("Nein", null);
			dg.show();
			break;
		case R.id.iAdd:
			Intent iAdd = new Intent(MainActivity.this, Add_Lehrer.class);
			iAdd.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iAdd);
			break;
		case R.id.iRefresh:
			createList();
			break;
		case R.id.iRemove:
			Intent iRemove = new Intent(MainActivity.this, Remove_Lehrer.class);
			iRemove.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iRemove);
			break;
		case R.id.iInterval:
			SharedPreferences spKISS = this.getSharedPreferences("KISS", Context.MODE_PRIVATE);
			final NumberPicker np = new NumberPicker(this);
			np.setMinValue(1);
			np.setMaxValue(9000);
			np.setValue(spKISS.getInt("interval", 40));
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Intervall in Minuten");
			builder.setView(np);
			builder.setNegativeButton("Abbrechen", null);
			builder.setMessage("In diesem Zeitabstand wird im Hintergrund das KISS überprüft");
			builder.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					getApplicationContext();
					SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = spKISS.edit();
					editor.putInt("interval", np.getValue());
					editor.commit();
				}

			});
			builder.show();
			break;
		case R.id.iShowKISS:
			/*
			 * Intent iShow = new Intent(MainActivity.this,
			 * InfoscreenChoice.class);
			 * iShow.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 * startActivity(iShow);
			 */

			AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
			final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1);
			arrayAdapter.add("Im Browser öffnen");
			arrayAdapter.add("Aus dem Cache anzeigen");
			builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						String url = "https://kpf.bks-campus.ch/infoscreen";
						Intent iKISS = new Intent(Intent.ACTION_VIEW);
						iKISS.setData(Uri.parse(url));
						startActivity(iKISS);
						break;
					case 1:
						Intent iCache = new Intent(MainActivity.this, KissView.class);
						iCache.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(iCache);
						break;
					}
				}
			});
			builderSingle.show();
			break;
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (done) {
			String name = actualOrder.get(position);
			Bundle bData = new Bundle();
			bData.putString("name", name);
			Intent i = new Intent(MainActivity.this, EditLehrer.class);
			i.putExtras(bData);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
		} else {
			Toast.makeText(this, "Liste wird aktualisiert ...", Toast.LENGTH_SHORT).show();
		}
	}

}
