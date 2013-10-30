package com.martin.noten;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.Toast;

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

public class RemoveMark extends ListActivity implements
		android.content.DialogInterface.OnClickListener {

	String noten;
	String entries[], current[];
	int id, kont_us, iSemester, pos;

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
		getData();
		prepareList();
		createList();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void getData() {
		Bundle received = getIntent().getExtras();
		DatabaseHandler db = new DatabaseHandler(this);

		id = received.getInt("id");
		iSemester = received.getInt("semester");
		Fach fach = db.getFach(id);

		if (iSemester == 1) {
			noten = fach.getNoten1();
		} else {
			noten = fach.getNoten2();
		}

	}

	private void prepareList() {
		entries = noten.split("\n");
	}

	private void createList() {
		setListAdapter(new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, entries));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		pos = position;
		AlertDialog.Builder dg = new AlertDialog.Builder(this);
		dg.setTitle("Note entfernen");
		dg.setMessage("Willst du die Note '" + entries[position]
				+ "' wirklich entfernen?");
		dg.setPositiveButton("Ja", this);
		dg.setNegativeButton("Nein", null);
		dg.show();
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		String[] new_list = entries;
		new_list[pos] = "rien";
		String newNoten = "";
		for (int i = 0; i < new_list.length; i++) {
			if (!(new_list[i].contentEquals("rien"))) {
				newNoten = newNoten + new_list[i] + "\n";
			}
		}

		if (newNoten.contentEquals("")) {
			newNoten = "-";
		}

		DatabaseHandler db = new DatabaseHandler(this);
		Fach updated = db.getFach(id);
		if (iSemester == 1) {
			updated.setNoten1(newNoten);
		} else {
			updated.setNoten2(newNoten);
		}

		db.updateFach(updated);

		Toast t = Toast.makeText(RemoveMark.this, "Note entfernt",
				Toast.LENGTH_SHORT);
		t.show();

		finish();
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
