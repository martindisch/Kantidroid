package com.martin.kontingent;

import java.util.List;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class RemoveEntry extends ListActivity implements
		android.content.DialogInterface.OnClickListener {

	Fach selected = null;

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
		createList();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void createList() {
		DatabaseHandler db = new DatabaseHandler(this);
		int count = db.getFachCount();
		List<Fach> faecher = db.getAllFaecher(getApplicationContext());
		String[] names = new String[count];
		for (int i = 0; i < count; i++) {
			Fach entry = faecher.get(i);
			names[i] = entry.getName();
		}
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, names));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		DatabaseHandler db = new DatabaseHandler(this);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext());

		selected = db.getFach(faecher.get(position).getID());

		AlertDialog.Builder dg = new AlertDialog.Builder(this);
		dg.setTitle("Fach löschen");
		dg.setMessage("Willst du das Fach " + selected.getName()
				+ " wirklich löschen?");
		dg.setPositiveButton("Ja", this);
		dg.setNegativeButton("Nein", null);
		dg.show();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		DatabaseHandler db = new DatabaseHandler(this);
		db.deleteFach(selected);
		createList();
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
