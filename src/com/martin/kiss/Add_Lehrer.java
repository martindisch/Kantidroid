package com.martin.kiss;

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
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class Add_Lehrer extends Activity implements OnClickListener, OnCheckedChangeListener {

	Button bSave, bCancel;
	AutoCompleteTextView etName;
	CheckBox cbAnother;

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
		setContentView(R.layout.add_lehrer);
		init();
		getCB();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void init() {
		bSave = (Button) findViewById(R.id.bAddSave);
		bCancel = (Button) findViewById(R.id.bAddCancel);
		etName = (AutoCompleteTextView) findViewById(R.id.etAddName);
		cbAnother = (CheckBox) findViewById(R.id.cbAddAnother);
		bSave.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		cbAnother.setOnCheckedChangeListener(this);
		String[] names = getResources().getStringArray(R.array.lehrer_list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, names);
		etName.setAdapter(adapter);
	}

	private void getCB() {
		getApplicationContext();
		SharedPreferences settings = getSharedPreferences("KISS", Context.MODE_PRIVATE);
		boolean bAnother = settings.getBoolean("another", true);

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
			if (!etName.getText().toString().contentEquals("")) {
				getApplicationContext();
				SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spKISS.edit();
				if (spKISS.getString("lehrer", "").contentEquals("")) {
					editor.putString("lehrer", etName.getText().toString());
				} else {
					String old = spKISS.getString("lehrer", "");
					editor.putString("lehrer", old + "-" + etName.getText().toString());
				}
				editor.commit();
				if (cbAnother.isChecked()) {
					etName.setText("");
					Toast t = Toast.makeText(this, "Person gespeichert", Toast.LENGTH_SHORT);
					t.show();
					etName.requestFocus();
				} else {
					finish();
				}
			} else {
				Toast t = Toast.makeText(this, "Gib einen Namen ein", Toast.LENGTH_SHORT);
				t.show();
			}

			break;
		case R.id.bAddCancel:
			finish();
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		getApplicationContext();
		SharedPreferences settings = getSharedPreferences("KISS", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();

		if (cbAnother.isChecked()) {
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
