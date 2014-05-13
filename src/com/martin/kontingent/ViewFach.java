package com.martin.kontingent;

import java.util.Calendar;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class ViewFach extends Activity implements OnClickListener, android.content.DialogInterface.OnClickListener, OnDateSetListener {

	TextView tvTitle, tvUsage;
	ImageButton ibAddKont;
	ListView lvUsage;
	NumberPicker picker;
	int id, kont_us, picked_number;
	boolean invoke;
	String fname, fkont, date_new, date_old, addition;
	Typeface tf;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewfach);
		getData();
		initialize();
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.kont_viewfach);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getData() {
		Bundle received = getIntent().getExtras();
		DatabaseHandler db = new DatabaseHandler(this);

		id = received.getInt("id");
		invoke = received.getBoolean("invoke");
		Fach fach = db.getFach(id);

		fname = fach.getName();
		fkont = fach.getKont_us() + "/" + fach.getKont_av();
		date_old = fach.getDates();

		kont_us = Integer.parseInt(fach.getKont_us());
	}

	private void initialize() {
		tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

		tvTitle = (TextView) findViewById(R.id.tvFachTitle);
		tvTitle.setTypeface(tf);
		ibAddKont = (ImageButton) findViewById(R.id.ibAddKont);
		ibAddKont.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				useKont();
			}
		});
		tvUsage = (TextView) findViewById(R.id.tvViewUsage);
		lvUsage = (ListView) findViewById(R.id.lvViewUsage);

		updateText();
		if (invoke) {
			invokedUse();
		}
	}

	private void updateText() {
		tvTitle.setText(fname);
		tvUsage.setText(fkont);
		if (!date_old.contentEquals("empty")) {
			String[] entries = date_old.split("\n");
			String[] dates = new String[entries.length];
			String[] usages = new String[entries.length];
			for (int i = 0; i < entries.length; i++) {
				dates[i] = entries[i].split(" - ")[0];
				usages[i] = entries[i].split(" - ")[1];
			}

			ViewFachAdapter adapter = new ViewFachAdapter(this, dates, usages);
			lvUsage.setAdapter(adapter);
		}

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		picked_number = picker.getValue();
		Calendar c = Calendar.getInstance();
		DatePickerDialog dg = new DatePickerDialog();
		dg.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		dg.setOnDateSetListener(this);
		dg.show(this);
	}

	private void setKont() {
		if (date_old.contains(addition)) {
			Toast t = Toast.makeText(ViewFach.this, "Eintrag bereits vorhanden", Toast.LENGTH_SHORT);
			t.show();
		} else {
			DatabaseHandler db = new DatabaseHandler(this);
			Fach updated = db.getFach(id);
			int new_value = kont_us + picked_number;
			updated.setKont_us(Integer.toString(new_value));
			updated.setDates(date_new);
			db.updateFach(updated);
			getData();
			updateText();
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getData();
		updateText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.menu_viewfach, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void invokedUse() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Anzahl genommenes Kontingent");
		View view = LayoutInflater.inflate(this, R.layout.numberpicker);
		picker = (NumberPicker) view.findViewById(R.id.numberPicker);
		picker.setMinValue(1);
		picker.setMaxValue(4);
		picker.setValue(1);
		builder.setView(view);
		builder.setNegativeButton("Abbrechen", null);
		builder.setPositiveButton("OK", this);
		builder.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iViewRemove:
			if (!(kont_us == 0)) {
				Bundle data = new Bundle();
				data.putString("name", fname);
				data.putString("dates", date_old);
				data.putInt("id", id);
				Intent i = new Intent(ViewFach.this, RemoveUsage.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				i.putExtras(data);
				startActivity(i);
			} else {
				Toast t = Toast.makeText(this, "Kein Kontingent zu entfernen", Toast.LENGTH_SHORT);
				t.show();
			}
			break;
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View arg0) {
		// Rien

	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
		String singularPlural = " Lektion";

		if (picked_number > 1) {
			singularPlural = " Lektionen";
		}
		addition = dayOfMonth + "." + (monthOfYear + 1) + "." + year + " - " + picked_number + singularPlural + "\n";

		if (date_old.contentEquals("empty")) {
			date_new = addition;
		} else {
			date_new = date_old + addition;
		}
		setKont();
	}

	private void useKont() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Anzahl genommenes Kontingent");
		View view = LayoutInflater.inflate(this, R.layout.numberpicker);
		picker = (NumberPicker) view.findViewById(R.id.numberPicker);
		picker.setMinValue(1);
		picker.setMaxValue(4);
		picker.setValue(1);
		builder.setView(view);
		builder.setNegativeButton("Abbrechen", null);
		builder.setPositiveButton("OK", this);
		builder.show();
	}
}
