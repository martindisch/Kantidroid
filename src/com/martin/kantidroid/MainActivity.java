package com.martin.kantidroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.AlertDialog.Builder;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.example.kontingentprototype.Overview;
import com.martin.noten.Fach;
import com.martin.noten.Main;
import com.martin.noten.PromoCheck;
import com.martin.noten.PromoRes;

public class MainActivity extends Activity implements OnClickListener {

	ImageButton ibKontingent, ibKISS, ibNoten;
	TextView tvSchn, promoviert, pluspunkte, tvUsage, tvUsage2, tvKISS;
	View separator;
	double schn = 0;
	Resources res;
	Fach entry;
	ProgressBar pb;
	WidgetContainer wcKont, wcNot, wcKISS;

	@Override
	protected void onStop() {
		super.onStop();
		Intent rIntent = new Intent(this, WidgetProvider.class);
		rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int[] ids = AppWidgetManager.getInstance(getApplication())
				.getAppWidgetIds(
						new ComponentName(getApplication(),
								WidgetProvider.class));
		rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(rIntent);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kdroid_main);
		wcKont = (WidgetContainer) findViewById(R.id.wContainerKontingent);
		wcNot = (WidgetContainer) findViewById(R.id.wContainerNoten);
		wcKISS = (WidgetContainer) findViewById(R.id.wContainerKISS);
		wcKont.setOnClickListener(this);
		wcNot.setOnClickListener(this);
		wcKISS.setOnClickListener(this);
		ibKontingent = (ImageButton) findViewById(R.id.ibKontingent);
		ibKISS = (ImageButton) findViewById(R.id.ibKISS);
		ibNoten = (ImageButton) findViewById(R.id.ibNoten);
		ibKontingent.setOnClickListener(this);
		ibKISS.setOnClickListener(this);
		ibNoten.setOnClickListener(this);
		tvSchn = (TextView) findViewById(R.id.tvViewSchn);
		promoviert = (TextView) findViewById(R.id.tvPromoviert);
		pluspunkte = (TextView) findViewById(R.id.tvPlusP);
		separator = findViewById(R.id.vSeparator);
		pb = (ProgressBar) findViewById(R.id.pbUsage);
		tvUsage = (TextView) findViewById(R.id.tvUsage);
		tvUsage2 = (TextView) findViewById(R.id.tvViewText);
		tvKISS = (TextView) findViewById(R.id.tvKISS);
		res = getResources();
		initialize();
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.kdroid_main);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		if (!check.getSeen("1.16", this)) {
			AlertDialog.Builder dgc = new AlertDialog.Builder(this);
			dgc.setTitle("Changelog");
			dgc.setNeutralButton("Schliessen", null);
			dgc.setMessage(R.string.changelog);
			dgc.show();
			check.setSeen("1.16", this);
		}

		// Enable networking without secondary thread
		// Usually very bad style, but for smaller tasks it doesn't matter that
		// much
		// For correct implementation of networking, see com.martin.kiss
		// In com.martin.kantidroid.TTManager however, networking is currently
		// done on the main thread, because the downloaded files are extremely
		// small
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initialize();
	}

	private void initialize() {

		// Noten

		SharedPreferences spNoten = getSharedPreferences("MarkSettings",
				MODE_PRIVATE);
		String sAbteilung = spNoten.getString("Abteilung", "Gym");
		PromoCheck prCheck = new PromoCheck(this);
		PromoRes prResult = null;
		if (sAbteilung.contentEquals("Gym")) {
			prResult = prCheck.getGym(2);
		} else if (sAbteilung.contentEquals("HMS")) {
			prResult = prCheck.getHMS(2);
		} else {
			prResult = prCheck.getFMS(2);
		}
		promoviert.setText(prResult.sMessage);
		promoviert.setTextColor(res.getColor(prResult.iColor));
		pluspunkte.setText(prResult.sPP);
		pluspunkte.setTextColor(res.getColor(prResult.iColor));
		separator.setBackgroundColor(res.getColor(prResult.iColor));
		tvSchn.setText(prResult.sSchnitt);

		// Kontingent

		com.example.kontingentprototype.DatabaseHandler dbK = new com.example.kontingentprototype.DatabaseHandler(
				this);
		int countK = dbK.getFachCount();

		int totalK = 0;
		int used = 0;
		int iPercentage = 0;
		double dPercentage = 0;

		List<com.example.kontingentprototype.Fach> faecherK = dbK
				.getAllFaecher(this);
		com.example.kontingentprototype.Fach entry = null;

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
			iPercentage = (int) ((used * 100) / totalK);
			dPercentage = (double) Math.round((double) used * 100 / totalK
					* 100) / 100;
		}
		tvUsage.setText(dPercentage + "% des Kontingents benutzt");
		if (überzogen == 0) {
			tvUsage.setTextColor(res.getColor(R.color.holo_green_light));
		} else {
			tvUsage.setTextColor(res.getColor(R.color.holo_red_light));
			String before = tvUsage.getText().toString();
			String einzmehrz = " Fach überzogen";
			if (überzogen > 1) {
				einzmehrz = " Fächern überzogen";
			}
			tvUsage.setText(before + "\nKontingent in " + überzogen + einzmehrz);
		}
		pb.setMax(100);
		pb.setProgress(iPercentage);

		tvUsage2.setText(used + "/" + totalK);

		// KISS

		SharedPreferences spKISS = this.getSharedPreferences("KISS",
				this.MODE_PRIVATE);
		String sLehrer = spKISS.getString("lehrer", "");
		String liste = "";

		if (!sLehrer.contentEquals("")) {
			String[] entries = sLehrer.split("-");
			for (int i = 0; i < entries.length; i++) {
				String current_kiss = spKISS.getString("KISS", "");

				if (current_kiss.contains(entries[i])) {
					liste += entries[i] + "\n";
				}
			}
			if (!liste.contentEquals("")) {
				tvKISS.setText(liste);
				tvKISS.setTextColor(Color.RED);
			} else {
				tvKISS.setText("-");
				tvKISS.setTextColor(Color.BLACK);
			}
		} else {
			tvKISS.setText("-");
			tvKISS.setTextColor(Color.BLACK);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibKontingent:
			Intent i = new Intent(MainActivity.this, Overview.class);
			i.putExtra("Internal_call", true);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		case R.id.ibKISS:
			Intent i2 = new Intent(MainActivity.this,
					com.martin.kiss.MainActivity.class);
			i2.putExtra("Internal_call", true);
			i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i2);
			break;
		case R.id.ibNoten:
			Intent i3 = new Intent(MainActivity.this, Main.class);
			i3.putExtra("Internal_call", true);
			i3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i3);
			break;
		case R.id.wContainerKontingent:
			Intent i4 = new Intent(MainActivity.this, Overview.class);
			i4.putExtra("Internal_call", true);
			i4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i4);
			break;
		case R.id.wContainerNoten:
			Intent i5 = new Intent(MainActivity.this, Main.class);
			i5.putExtra("Internal_call", true);
			i5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i5);
			break;
		case R.id.wContainerKISS:
			Intent i6 = new Intent(MainActivity.this,
					com.martin.kiss.MainActivity.class);
			i6.putExtra("Internal_call", true);
			i6.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i6);
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflator = getSupportMenuInflater();
		inflator.inflate(R.menu.kdroid_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId,
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iTimetable:
			LayoutInflater ttinflater = this.getLayoutInflater();
			View ttDialog = ttinflater.inflate(R.layout.ttdialog);
			final Spinner ttsYear = (Spinner) ttDialog
					.findViewById(R.id.ttsYear);
			final EditText ttetClass = (EditText) ttDialog
					.findViewById(R.id.ttetClass);
			SharedPreferences sp = getApplicationContext()
					.getSharedPreferences("Kantidroid",
							getApplicationContext().MODE_PRIVATE);
			ttsYear.setSelection(sp.getInt("yearindex", 0));
			ttetClass.setText(sp.getString("class", ""));
			AlertDialog.Builder ttdg = new AlertDialog.Builder(this);
			ttdg.setTitle("Stundenplan");
			ttdg.setView(ttDialog);
			ttdg.setNegativeButton("Abbrechen", null);
			ttdg.setPositiveButton("Ansehen",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String sYear = res.getStringArray(R.array.years)[ttsYear
									.getSelectedItemPosition()];
							String sClass = ttetClass.getText().toString();
							SharedPreferences sp = getApplicationContext()
									.getSharedPreferences(
											"Kantidroid",
											getApplicationContext().MODE_PRIVATE);
							SharedPreferences.Editor editor = sp.edit();
							editor.putInt("yearindex",
									ttsYear.getSelectedItemPosition());
							editor.putString("class", sClass);
							editor.commit();
							TTManager ttm = new TTManager();
							sClass = ttm.parseClass(sClass);
							if (sClass.contentEquals("Error")) {
								Toast.makeText(
										getApplicationContext(),
										"Stundenplan für diese Klasse konnte nicht gefunden werden",
										Toast.LENGTH_SHORT).show();
							} else {
								if (ttm.checkTT(sClass, sYear)) {
									Toast.makeText(
											getApplicationContext(),
											"Stundenplan bereits heruntergeladen, wird geöffnet...",
											Toast.LENGTH_SHORT).show();
									File SDCardRoot = new File(Environment
											.getExternalStorageDirectory(),
											"/Kantidroid/");
									File file = new File(SDCardRoot, sYear
											+ sClass + ".pdf");

									Intent intent = new Intent(
											Intent.ACTION_VIEW);
									intent.setDataAndType(Uri.fromFile(file),
											"application/pdf");

									startActivity(intent);
								} else {
									if (isNetworkAvailable()) {
										if (ttm.downloadTT(sClass, sYear)) {
											Toast.makeText(
													getApplicationContext(),
													"Stundenplan wird heruntergeladen und geöffnet...",
													Toast.LENGTH_SHORT).show();
											File SDCardRoot = new File(
													Environment
															.getExternalStorageDirectory(),
													"/Kantidroid/");
											File file = new File(SDCardRoot,
													sYear + sClass + ".pdf");
											Intent intent = new Intent(
													Intent.ACTION_VIEW);
											intent.setDataAndType(
													Uri.fromFile(file),
													"application/pdf");

											startActivity(intent);
										} else {
											Toast.makeText(
													getApplicationContext(),
													"Stundenplan für diese Klasse konnte nicht gefunden werden",
													Toast.LENGTH_SHORT).show();
										}
									}
									else {
										Toast.makeText(
												getApplicationContext(),
												"Keine Internetverbindung",
												Toast.LENGTH_SHORT).show();
									}
								}
							}
						}

					});
			ttdg.show();
			break;
		case R.id.iAbout:
			AlertDialog.Builder infodg = new AlertDialog.Builder(this);
			LayoutInflater inflater = this.getLayoutInflater();
			infodg.setTitle("Über");
			infodg.setView(inflater.inflate(R.layout.about_dialog));
			infodg.setNeutralButton("Schliessen", null);
			infodg.show();
			break;
		case R.id.iShortcut:
			AlertDialog.Builder delDg = new AlertDialog.Builder(this);
			delDg.setTitle("Zurücksetzen");
			delDg.setMessage("Willst du wirklich alle Daten (Noten, Kontingent, KISS, etc.) löschen?");
			delDg.setNegativeButton("Nein", null);
			delDg.setPositiveButton("Ja",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							clearApplicationData();
							Toast t = Toast.makeText(getApplicationContext(),
									"Alle Daten gelöscht", Toast.LENGTH_SHORT);
							t.show();
						}

					});
			delDg.show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void clearApplicationData() {
		File cache = getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {
				if (!s.equals("lib")) {
					deleteDir(new File(appDir, s));
				}
			}
		}
		SharedPreferences sp = getSharedPreferences("KISS", this.MODE_PRIVATE);
		Editor ed = sp.edit();
		ed.clear();
		ed.commit();
		initialize();
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
