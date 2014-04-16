package com.martin.kiss;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class Remove_Lehrer extends ListActivity {

	String sList;
	String old_noti;
	String[] sNames;

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
		createList();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void createList() {
		SharedPreferences spKISS = this.getSharedPreferences("KISS",
				Context.MODE_PRIVATE);
		sList = spKISS.getString("lehrer", "");
		old_noti = spKISS.getString("noti", "");
		sNames = sList.split("-");
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, sNames));
	}

	@Override
	protected void onListItemClick(ListView l, View v, final int position,
			long id) {
		super.onListItemClick(l, v, position, id);

		AlertDialog.Builder dg = new AlertDialog.Builder(this);
		dg.setTitle("Lehrer entfernen");
		dg.setMessage("Willst du " + sNames[position] + " wirklich entfernen?");
		dg.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String sNewList;
				String new_noti;
				if (sList.contains("-" + sNames[position])) {
					sNewList = sList.replace("-" + sNames[position], "");
				} else if (sList.contains(sNames[position] + "-")) {
					sNewList = sList.replace(sNames[position] + "-", "");
				} else {
					sNewList = "";
				}
				if (old_noti.contains("-" + sNames[position])) {
					new_noti = old_noti.replace("-" + sNames[position], "");
				} else if (old_noti.contains(sNames[position] + "-")) {
					new_noti = old_noti.replace(sNames[position] + "-", "");
				} else {
					new_noti = "";
				}
				getApplicationContext();
				SharedPreferences spKISS = getApplicationContext()
						.getSharedPreferences("KISS", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spKISS.edit();
				editor.putString("lehrer", sNewList);
				editor.putString("noti", new_noti);
				editor.remove(sNames[position]);
				editor.commit();
				createList();
			}

		});
		dg.setNegativeButton("Nein", null);
		dg.show();
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
