package com.martin.noten;

import java.util.Calendar;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog.OnDateSetListener;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class AddMark extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnDateSetListener {

	Button bSave, bCancel, bOwnRelevance;
	Spinner sRelevance;
	EditText etMark;
	CheckBox cbAnother;
	int id;
	boolean OwnRelevance = false;
	double dOwnRelevance;
	int iSemester;
	Fach fach;
	String sSelectedRelevance;
	DatabaseHandler db;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_mark);
		getData();
		initialize();
		getCheckbox();
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.noten_viewfach_addmark);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getData() {
		Bundle received = getIntent().getExtras();
		id = received.getInt("id");
		iSemester = received.getInt("semester");
	}

	private void initialize() {
		bSave = (Button) findViewById(R.id.bAddSave);
		bCancel = (Button) findViewById(R.id.bAddCancel);
		/* TODO:
		bOwnRelevance = (Button) findViewById(R.id.bOwnRelevance);
		sRelevance = (Spinner) findViewById(R.id.sRelevance);*/
		etMark = (EditText) findViewById(R.id.etAddMark);
		cbAnother = (CheckBox) findViewById(R.id.cbAnotherMark);
		bSave.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		bOwnRelevance.setOnClickListener(this);
		cbAnother.setOnCheckedChangeListener(this);

		DatabaseHandler db = new DatabaseHandler(this);
		Fach fach = db.getFach(id);
		setTitle("Note für " + fach.getName() + " hinzufügen");
	}

	private void getCheckbox() {
		SharedPreferences settings = getSharedPreferences("MarkSettings",
				getApplicationContext().MODE_PRIVATE);
		boolean bAnother = settings.getBoolean("anotherMark", true);

		if (bAnother == true) {
			cbAnother.setChecked(true);
		} else {
			cbAnother.setChecked(false);
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bAddSave:
			if (!etMark.getText().toString().contentEquals("")) {
				double sMark = Double.parseDouble(etMark.getText().toString());

				if (sMark >= 1 && sMark <= 6.3) {
					db = new DatabaseHandler(this);
					fach = db.getFach(id);

					sSelectedRelevance = "";
					if (OwnRelevance) {
						sSelectedRelevance = String.valueOf(dOwnRelevance);
					} else {
						switch (sRelevance.getSelectedItemPosition()) {
						case 0:
							sSelectedRelevance = "1.0";
							break;
						case 1:
							sSelectedRelevance = "0.5";
							break;
						case 2:
							sSelectedRelevance = "2.0";
							break;
						}
					}

					Calendar c = Calendar.getInstance();
					DatePickerDialog dg = new DatePickerDialog();
					dg.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
							c.get(Calendar.DAY_OF_MONTH));
					dg.setOnDateSetListener(this);
					dg.show(this);

				} else {
					Toast t = Toast.makeText(AddMark.this, "Ungültige Note",
							Toast.LENGTH_SHORT);
					t.show();
				}
			} else {
				Toast t = Toast.makeText(this, "Leeres Feld",
						Toast.LENGTH_SHORT);
				t.show();
			}

			break;
		case R.id.bAddCancel:
			finish();
			break;
			/* TODO:
		case R.id.bOwnRelevance:
			AlertDialog.Builder inp = new AlertDialog.Builder(this);
			inp.setTitle("Relevanz");
			inp.setMessage("Gib an, wie viel diese Note zählen soll (1, 0.5, 0.2 etc.)");
			final EditText rel = new EditText(this);
			rel.setInputType(InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL);
			inp.setView(rel);
			inp.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!rel.getText().toString().contentEquals("")) {
						dOwnRelevance = Double.parseDouble(rel.getText()
								.toString());
						sRelevance.setEnabled(false);
						OwnRelevance = true;

						String choices[] = { rel.getText().toString() + "x" };

						ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
								getApplicationContext(),
								android.R.layout.simple_spinner_item, choices);
						sRelevance.setAdapter(spinnerArrayAdapter);
					} else {
						Toast t = Toast.makeText(AddMark.this,
								"Gib einen Wert ein", Toast.LENGTH_SHORT);
						t.show();
					}
				}
			});
			inp.setNegativeButton("Abbrechen", null);
			inp.show();
			break;*/
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences settings = getSharedPreferences("MarkSettings",
				getApplicationContext().MODE_PRIVATE);

		SharedPreferences.Editor editor = settings.edit();
		if (cbAnother.isChecked()) {
			editor.putBoolean("anotherMark", true);
		} else {
			editor.putBoolean("anotherMark", false);
		}

		editor.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear,
			int dayOfMonth) {
		String date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;

		if (iSemester == 1) {
			String sMarksOld = fach.getNoten1();
			String sMarksNew;
			String sEntry = etMark.getText().toString() + " - "
					+ sSelectedRelevance + " - " + date + "\n";
			if (sMarksOld.contentEquals("-")) {
				sMarksNew = sEntry;
			} else {
				sMarksNew = sMarksOld + sEntry;
			}

			fach.setNoten1(sMarksNew);
			db.updateFach(fach);
		} else {
			String sMarksOld = fach.getNoten2();
			String sMarksNew;
			String sEntry = etMark.getText().toString() + " - "
					+ sSelectedRelevance + " - " + date + "\n";
			if (sMarksOld.contentEquals("-")) {
				sMarksNew = sEntry;
			} else {
				sMarksNew = sMarksOld + sEntry;
			}

			fach.setNoten2(sMarksNew);
			db.updateFach(fach);
		}

		if (cbAnother.isChecked()) {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(this, R.array.relevance_entries,
							R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
			sRelevance.setAdapter(adapter);

			etMark.setText("");
			sRelevance.setEnabled(true);
			OwnRelevance = false;
			sRelevance.setSelection(0);
			Toast t = Toast.makeText(AddMark.this, "Note gespeichert",
					Toast.LENGTH_SHORT);
			t.show();
			etMark.requestFocus();
		} else {
			finish();
		}
	}

}
