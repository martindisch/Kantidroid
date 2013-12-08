package com.martin.kontingent;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AutoCompleteTextView;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.Toast;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class CreateEntry extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	AutoCompleteTextView fach;
	Button save, cancel;
	CheckBox another;
	RadioGroup rGroup, rgOther;
	RadioButton rb2, rb4, rb6, rb8, rbOther;
	EditText etOther;
	boolean checked;

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
		setContentView(R.layout.create_entry);
		initialize();
		getCheckbox();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initialize() {
		fach = (AutoCompleteTextView) findViewById(R.id.etAddFach);
		save = (Button) findViewById(R.id.bAddSave);
		cancel = (Button) findViewById(R.id.bAddCancel);
		another = (CheckBox) findViewById(R.id.cbAnother);
		rGroup = (RadioGroup) findViewById(R.id.rgKontingent);
		rb2 = (RadioButton) findViewById(R.id.rb2);
		rb4 = (RadioButton) findViewById(R.id.rb4);
		rb6 = (RadioButton) findViewById(R.id.rb6);
		rb8 = (RadioButton) findViewById(R.id.rb8);
		rbOther = (RadioButton) findViewById(R.id.rbOther);
		etOther = (EditText) findViewById(R.id.etOther);
		rgOther = (RadioGroup) findViewById(R.id.rgOther);

		save.setOnClickListener(this);
		cancel.setOnClickListener(this);
		another.setOnCheckedChangeListener(this);
		rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (!(checkedId == -1)) {
					rgOther.clearCheck();
				}
			}

		});
		rgOther.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (!(checkedId == -1)) {
					rGroup.clearCheck();
				}
			}
		});

		String[] names = getResources().getStringArray(R.array.faecher_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, names);
		fach.setAdapter(adapter);

		checked = false;
	}

	private void getCheckbox() {
		SharedPreferences settings = getSharedPreferences("mysettings",
				Context.MODE_PRIVATE);
		boolean bAnother = settings.getBoolean("another", true);

		if (bAnother == true) {
			another.setChecked(true);
		} else {
			another.setChecked(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bAddSave:
			/*
			 * TODO: String sFach = fach.getText().toString(); String sKont_av =
			 * kontingent.getText().toString();
			 * 
			 * if (!sFach.contentEquals("") && !sKont_av.contentEquals("")) {
			 * 
			 * Fach ffach = new Fach(sFach, sKont_av); DatabaseHandler db = new
			 * DatabaseHandler(this);
			 * 
			 * db.addFach(ffach);
			 * 
			 * if (another.isChecked()) { fach.setText("");
			 * kontingent.setText(""); Toast t =
			 * Toast.makeText(CreateEntry.this, "Fach gespeichert",
			 * Toast.LENGTH_SHORT); t.show(); fach.requestFocus(); } else {
			 * finish(); } } else { Toast t = Toast.makeText(CreateEntry.this,
			 * "Fülle beide Felder aus", Toast.LENGTH_SHORT); t.show(); }
			 */
			break;
		case R.id.bAddCancel:
			finish();
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		SharedPreferences settings = getSharedPreferences("mysettings",
				getApplicationContext().MODE_PRIVATE);

		SharedPreferences.Editor editor = settings.edit();
		if (another.isChecked()) {
			editor.putBoolean("another", true);
		} else {
			editor.putBoolean("another", false);
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
