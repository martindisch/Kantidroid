package com.martin.kiss;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class EditLehrer extends Activity implements OnClickListener {

	String sName;
	EditText etName;
	Button bSave, bCancel;
	TextView tvCount;

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
	protected void onCreate(Bundle sSavedInstanceState) {
		super.onCreate(sSavedInstanceState);
		setContentView(R.layout.edit_lehrer);
		getData();
		initialize();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initialize() {
		SharedPreferences spRem = getApplicationContext().getSharedPreferences(
				"Rem", getApplicationContext().MODE_PRIVATE);
		etName = (EditText) findViewById(R.id.etEditName);
		tvCount = (TextView) findViewById(R.id.tvEditCount);
		bSave = (Button) findViewById(R.id.bEditSave);
		bCancel = (Button) findViewById(R.id.bEditCancel);
		bSave.setOnClickListener(this);
		bCancel.setOnClickListener(this);
		etName.setText(sName);
		tvCount.setText(String.valueOf(spRem.getInt(sName, 0)));
	}

	private void getData() {
		Bundle bReceived = getIntent().getExtras();
		sName = bReceived.getString("name");
	}

	@Override
	public void onClick(View v) {
		String et = etName.getText().toString();
		switch (v.getId()) {
		case R.id.bEditSave:
			if (!etName.getText().toString().contentEquals("")) {
				SharedPreferences spKISS = getApplicationContext()
						.getSharedPreferences("KISS",
								getApplicationContext().MODE_PRIVATE);
				SharedPreferences.Editor editor = spKISS.edit();
				if (!sName.contentEquals(etName.getText().toString())) {
					SharedPreferences spRem = getApplicationContext()
							.getSharedPreferences("Rem",
									getApplicationContext().MODE_PRIVATE);
					SharedPreferences.Editor ed = spRem.edit();
					ed.putInt(etName.getText().toString(),
							spRem.getInt(sName, 0));
					ed.remove(sName);
					ed.commit();
					String sOld_List = spKISS.getString("lehrer", "");
					String sNew_List = sOld_List.replace(sName, etName
							.getText().toString());
					editor.putString("lehrer", sNew_List);
					editor.commit();
					Toast t = Toast.makeText(this, "Person gespeichert",
							Toast.LENGTH_SHORT);
					t.show();
				}

				finish();
			} else {
				Toast t = Toast.makeText(this, "Gib einen Namen ein",
						Toast.LENGTH_SHORT);
				t.show();
			}
			break;
		case R.id.bEditCancel:
			finish();
			break;
		}
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
