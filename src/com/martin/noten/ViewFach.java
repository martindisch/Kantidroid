package com.martin.noten;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.Toast;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
=======
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

>>>>>>> beta
import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class ViewFach extends Activity {

	TextView name, noten, math_average, real_average, relevance,
			promotionsfach, dates;
	NumberPicker picker;
	int id, iSemester;
	String fname, fnoten, addition, fmath_average, freal_average,
			fpromotionsfach;
	String[] entries, mark;

	@Override
	protected void onStop() {
		super.onStop();
		Intent rIntent = new Intent(this, WidgetProvider.class);
		rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
<<<<<<< HEAD
		int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
		rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(rIntent);
	}
	
=======
		int[] ids = AppWidgetManager.getInstance(getApplication())
				.getAppWidgetIds(
						new ComponentName(getApplication(),
								WidgetProvider.class));
		rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(rIntent);
	}

>>>>>>> beta
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewfach_noten);
		getData();
		initialize();
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.noten_viewfach);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getData() {
		Bundle received = getIntent().getExtras();
		DatabaseHandler db = new DatabaseHandler(this);

		id = received.getInt("id");
		iSemester = received.getInt("semester");
		Fach fach = db.getFach(id);

		fname = fach.getName();
		if (iSemester == 1) {
			fnoten = fach.getNoten1();
			if (!fnoten.contentEquals("-")) {
				entries = fnoten.split("\n");
			}
			fmath_average = fach.getMathAverage1();
			freal_average = fach.getRealAverage1();
			fpromotionsfach = fach.getPromotionsrelevant();
		} else {
			fnoten = fach.getNoten2();
			if (!fnoten.contentEquals("-")) {
				entries = fnoten.split("\n");
			}
			fmath_average = fach.getMathAverage2();
			freal_average = fach.getRealAverage2();
			fpromotionsfach = fach.getPromotionsrelevant();
		}
	}

	private void initialize() {
		name = (TextView) findViewById(R.id.tvViewName);
		noten = (TextView) findViewById(R.id.tvViewNoten);
		relevance = (TextView) findViewById(R.id.tvViewGewichtung);
		math_average = (TextView) findViewById(R.id.tvViewMathAverage);
		real_average = (TextView) findViewById(R.id.tvViewRealAverage);
		promotionsfach = (TextView) findViewById(R.id.tvViewPromotionsfach);
		dates = (TextView) findViewById(R.id.tvViewDate);
		updateText();
	}

	private void updateText() {
		String dispNote = "-";
		String dispGewichtung = "-";
		String dispDate = "-";
		name.setText(fname);
		if (!fnoten.contentEquals("-")) {
			dispNote = "";
			dispGewichtung = "";
			dispDate = "";
			for (int i = 0; i < entries.length; i++) {
				mark = entries[i].split(" - ");
				dispNote += mark[0] + "\n";
				dispGewichtung += mark[1] + "\n";
				dispDate += mark[2] + "\n";
			}
		}

		if (fpromotionsfach.contentEquals("true")) {
			promotionsfach.setText("Ja");
		} else {
			promotionsfach.setText("Nein");
		}

		noten.setText(dispNote);
		relevance.setText(dispGewichtung);
		math_average.setText(fmath_average);
		real_average.setText(freal_average);
		dates.setText(dispDate);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getData();
		updateText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
<<<<<<< HEAD
		com.actionbarsherlock.view.MenuInflater inflator = getSupportMenuInflater();
=======
		MenuInflater inflator = getMenuInflater();
>>>>>>> beta
		inflator.inflate(R.menu.viewmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
<<<<<<< HEAD
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
=======
	public boolean onOptionsItemSelected(MenuItem item) {
>>>>>>> beta
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mViewAdd:
			Bundle data = new Bundle();
			data.putInt("id", id);
			data.putInt("semester", iSemester);
			Intent i = new Intent(ViewFach.this, AddMark.class);
			i.putExtras(data);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		case R.id.mViewDiscard:
			DatabaseHandler db = new DatabaseHandler(this);
			Fach check = db.getFach(id);
			if (iSemester == 1) {
				if (!check.getNoten1().contentEquals("-")) {
					Bundle data2 = new Bundle();
					data2.putInt("id", id);
					data2.putInt("semester", iSemester);
					Intent i2 = new Intent(ViewFach.this, RemoveMark.class);
					i2.putExtras(data2);
					i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i2);
				} else {
					Toast t = Toast.makeText(ViewFach.this,
							"Keine Note zu entfernen", Toast.LENGTH_SHORT);
					t.show();
				}
			} else {
				if (!check.getNoten2().contentEquals("-")) {
					Bundle data2 = new Bundle();
					data2.putInt("id", id);
					data2.putInt("semester", iSemester);
					Intent i2 = new Intent(ViewFach.this, RemoveMark.class);
					i2.putExtras(data2);
					i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i2);
				} else {
					Toast t = Toast.makeText(ViewFach.this,
							"Keine Note zu entfernen", Toast.LENGTH_SHORT);
					t.show();
				}
			}
			break;
		case R.id.mViewInfo:
			DatabaseHandler dba = new DatabaseHandler(this);
			Fach checka = dba.getFach(id);
			if (iSemester == 1) {
				if (!checka.getNoten1().contentEquals("-")) {
					Bundle data2 = new Bundle();
					data2.putInt("id", id);
					data2.putInt("semester", iSemester);
					Intent i2 = new Intent(ViewFach.this, Guessing.class);
					i2.putExtras(data2);
					i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i2);
				} else {
					Toast t = Toast.makeText(ViewFach.this,
							"Keine Noten vorhanden", Toast.LENGTH_SHORT);
					t.show();
				}
			} else {
				if (!checka.getNoten2().contentEquals("-")) {
					Bundle data2 = new Bundle();
					data2.putInt("id", id);
					data2.putInt("semester", iSemester);
					Intent i2 = new Intent(ViewFach.this, Guessing.class);
					i2.putExtras(data2);
					i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i2);
				} else {
					Toast t = Toast.makeText(ViewFach.this,
							"Keine Noten vorhanden", Toast.LENGTH_SHORT);
					t.show();
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
