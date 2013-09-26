package com.martin.noten;

import java.util.List;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.view.MenuItem;
import com.martin.kantidroid.R;

public class EditSelect extends ListActivity {

	Fach selected = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createList();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void createList() {
		DatabaseHandler db = new DatabaseHandler(this);
		int count = db.getFachCount();
		SharedPreferences spNoten = this.getSharedPreferences("MarkSettings",
				this.MODE_PRIVATE);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext(),
				spNoten.getInt("selected_semester", 1));
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
		SharedPreferences spNoten = this.getSharedPreferences("MarkSettings",
				this.MODE_PRIVATE);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext(),
				spNoten.getInt("selected_semester", 1));

		selected = db.getFach(faecher.get(position).getID());

		Bundle data = new Bundle();
		data.putInt("id", selected.getID());
		Intent i = new Intent(EditSelect.this, EditEntry.class);
		i.putExtras(data);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	@Override
	protected void onPause() {
		super.onPause();
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