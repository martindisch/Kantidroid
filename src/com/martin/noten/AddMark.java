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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

// This is the ugliest, most hacky part of the whole project.
// That's because I hate it and absolutely don't want to spend
// a single second longer working on it.

public class AddMark extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	Button bSave, bCancel;
	Spinner sDate;
	EditText etMark, etOther;
	CheckBox cbAnother;
	RadioGroup rgGewichtung;
	int id;
	int iSemester;
	Fach fach;
	String sSelectedRelevance;
	DatabaseHandler db;
	String selectedDate, sMark;
	ArrayAdapter adapter;
	boolean textChanged, firsttime;
	AddMark crap;
	RadioButton rbGanz;

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
		firsttime = true;
		crap = this;
		sSelectedRelevance = "1";
		sMark = "0";
		textChanged = false;
		Calendar c = Calendar.getInstance();
		selectedDate = c.get(Calendar.DAY_OF_MONTH) + "."
				+ (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);
		bSave = (Button) findViewById(R.id.bAddSave);
		rbGanz = (RadioButton) findViewById(R.id.rbGanz);
		bCancel = (Button) findViewById(R.id.bAddCancel);
		sDate = (Spinner) findViewById(R.id.sDatumMark);
		etOther = (EditText) findViewById(R.id.etAndere);
		rgGewichtung = (RadioGroup) findViewById(R.id.rgGewichtung);
		etMark = (EditText) findViewById(R.id.etMark);
		cbAnother = (CheckBox) findViewById(R.id.cbAnotherMark);
		bSave.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		adapter = new ArrayAdapter(this, R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		adapter.add(selectedDate);
		sDate.setAdapter(adapter);
		cbAnother.setOnCheckedChangeListener(this);

		etMark.setText("");
		etOther.setText("");

		sDate.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Calendar c = Calendar.getInstance();
					DatePickerDialog dg = new DatePickerDialog();
					dg.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
							c.get(Calendar.DAY_OF_MONTH));
					dg.setOnDateSetListener(new OnDateSetListener() {

						@Override
						public void onDateSet(DatePickerDialog dialog,
								int year, int monthOfYear, int dayOfMonth) {
							selectedDate = dayOfMonth + "." + (monthOfYear + 1)
									+ "." + year;
							adapter.clear();
							adapter.add(selectedDate);
						}
					});
					dg.show(crap);
				}
				return true;
			}
		});

		etMark.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				sMark = s.toString();
			}
		});

		etOther.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().contentEquals("")) {
					sSelectedRelevance = s.toString();
					textChanged = true;
					for (int i = 0; i < 3; i++) {
						rgGewichtung.getChildAt(i).setEnabled(false);
					}
				}
			}
		});

		rgGewichtung
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (!textChanged && !firsttime) {
							switch (checkedId) {
							case R.id.rbDoppelt:
								sSelectedRelevance = "2";
								break;
							case R.id.rbGanz:
								sSelectedRelevance = "1";
								break;
							case R.id.rbHalb:
								sSelectedRelevance = "0.5";
								break;
							}
							etOther.setEnabled(false);
						}
					}
				});

		DatabaseHandler db = new DatabaseHandler(this);
		Fach fach = db.getFach(id);
		setTitle("Note für " + fach.getName() + " hinzufügen");

		for (int i = 0; i < rgGewichtung.getChildCount(); i++) {
			rgGewichtung.getChildAt(i).setEnabled(true);
		}
		rgGewichtung.clearCheck();

		firsttime = false;
	}

	private void getCheckbox() {
		SharedPreferences settings = getSharedPreferences("MarkSettings",
				Context.MODE_PRIVATE);
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
			if (!(sMark.contentEquals("") || sMark.contentEquals("0") || sSelectedRelevance
					.contentEquals("0"))) {
				double dMark = Double.parseDouble(sMark);

				if (dMark >= 1 && dMark <= 6.3) {
					db = new DatabaseHandler(this);
					fach = db.getFach(id);

					if (iSemester == 1) {
						String sMarksOld = fach.getNoten1();
						String sMarksNew;
						String sEntry = etMark.getText().toString() + " - "
								+ sSelectedRelevance + " - " + selectedDate
								+ "\n";
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
								+ sSelectedRelevance + " - " + selectedDate
								+ "\n";
						if (sMarksOld.contentEquals("-")) {
							sMarksNew = sEntry;
						} else {
							sMarksNew = sMarksOld + sEntry;
						}

						fach.setNoten2(sMarksNew);
						db.updateFach(fach);
					}

					if (cbAnother.isChecked()) {
						initialize();
						Toast t = Toast.makeText(AddMark.this,
								"Note gespeichert", Toast.LENGTH_SHORT);
						t.show();
						etMark.requestFocus();
					} else {
						finish();
					}

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

}
