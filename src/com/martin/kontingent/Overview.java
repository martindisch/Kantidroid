package com.martin.kontingent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.NumberPicker;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;

public class Overview extends Activity implements OnClickListener,
		OnItemClickListener {

	int selected = 0;
	ListView lv;
	// ProgressBar pb;
	TextView tvUsage;
	Resources res;
	Fach fSelected = null;
	LinearLayout KOverview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.overview);
		lv = (ListView) findViewById(R.id.lvMain);
		// pb = (ProgressBar) findViewById(R.id.pbUsage);
		KOverview = (LinearLayout) findViewById(R.id.llKontingentOverview);
		tvUsage = (TextView) findViewById(R.id.tvUsage);
		lv.setOnItemClickListener(this);
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.kont_main);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		if (getIntent().hasExtra("Internal_call")) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		createList();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.cmEdit:
			DatabaseHandler db = new DatabaseHandler(this);
			List<Fach> faecher = db.getAllFaecher(getApplicationContext());

			fSelected = db.getFach(faecher.get(info.position).getID());

			Bundle data = new Bundle();
			data.putInt("id", fSelected.getID());
			Intent i = new Intent(Overview.this, EditEntry.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtras(data);
			startActivity(i);
			break;
		case R.id.cmDelete:
			DatabaseHandler db1 = new DatabaseHandler(this);
			List<Fach> faecher1 = db1.getAllFaecher(getApplicationContext());

			fSelected = db1.getFach(faecher1.get(info.position).getID());

			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Fach löschen");
			dg.setMessage("Willst du das Fach " + fSelected.getName()
					+ " wirklich löschen?");
			dg.setPositiveButton("Ja", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					DatabaseHandler db = new DatabaseHandler(
							getApplicationContext());
					db.deleteFach(fSelected);
					createList();
				}

			});
			dg.setNegativeButton("Nein", null);
			dg.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	private void createList() {
		ArrayList<Map<String, String>> list = buildData();
		String[] from = { "fach", "anzahl" };
		int[] to = { R.id.tvLeft, R.id.tvRight };

		SimpleAdapter adapter = new MyAdapter(this, list,
				R.layout.overview_list_item, from, to);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		DatabaseHandler db = new DatabaseHandler(this);
		int count = db.getFachCount();

		int total = 0;
		int used = 0;
		int iPercentage = 0;
		double dPercentage = 0;

		List<Fach> faecher = db.getAllFaecher(getApplicationContext());
		Fach entry = null;

		int überzogen = 0;

		for (int i = 0; i < count; i++) {
			entry = faecher.get(i);
			if (!entry.getKont_av().contentEquals("-")) {
				total = total + Integer.parseInt(entry.getKont_av());
			}
			if (!entry.getKont_us().contentEquals("-")) {
				used = used + Integer.parseInt(entry.getKont_us());

				// Check für überzogen
				if (Integer.parseInt(entry.getKont_us()) > Integer
						.parseInt(entry.getKont_av())) {
					überzogen++;
				}
			}
		}

		if (!(total == 0)) {
			iPercentage = used * 100 / total;
			dPercentage = (double) Math
					.round((double) used * 100 / total * 100) / 100;
		}

		res = getResources();

		tvUsage.setText(dPercentage + "% des Kontingents benutzt (" + used
				+ "/" + total + ")");
		if (überzogen == 0) {
			KOverview
					.setBackgroundColor(res.getColor(R.color.holo_green_light));
			// tvUsage.setTextColor(res.getColor(R.color.holo_green_light));
		} else {
			// tvUsage.setTextColor(res.getColor(R.color.holo_red_light));
			KOverview.setBackgroundColor(res.getColor(R.color.holo_red_light));
			String before = tvUsage.getText().toString();
			String einzmehrz = " Fach überzogen";
			if (überzogen > 1) {
				einzmehrz = " Fächern überzogen";
			}
			tvUsage.setText(before + "\nKontingent in " + überzogen + einzmehrz);
		}
		// pb.setMax(100);
		// pb.setProgress(iPercentage);
	}

	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DatabaseHandler db = new DatabaseHandler(this);
		int count = db.getFachCount();
		List<Fach> faecher = db.getAllFaecher(getApplicationContext());
		for (int i = 0; i < count; i++) {
			Fach entry = faecher.get(i);
			list.add(putData(entry.getName(),
					entry.getKont_us() + "/" + entry.getKont_av()));
		}

		return list;
	}

	private HashMap<String, String> putData(String fach, String anzahl) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("fach", fach);
		item.put("anzahl", anzahl);
		return item;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iAdd:
			Intent iadd = new Intent(Overview.this, CreateEntry.class);
			iadd.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iadd);
			break;
		case R.id.iRemove:
			DatabaseHandler db = new DatabaseHandler(this);
			if (db.getFachCount() == 0) {
				Toast t = Toast.makeText(this, "Kein Fach zu entfernen",
						Toast.LENGTH_SHORT);
				t.show();
			} else {
				Intent iremove = new Intent(Overview.this, RemoveEntry.class);
				iremove.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(iremove);
			}

			break;
		case R.id.iAbout:
			AlertDialog.Builder infodg = new AlertDialog.Builder(this);
			LayoutInflater inflater = this.getLayoutInflater();
			infodg.setTitle("Über");
			infodg.setView(inflater.inflate(R.layout.about_dialog));
			infodg.setNeutralButton("Schliessen", null);
			infodg.show();
			break;
		case R.id.iReset:
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Kontingent zurücksetzen");
			dg.setMessage("Willst du deinen Kontingentverbrauch zurücksetzen?");
			dg.setPositiveButton("Ja", this);
			dg.setNegativeButton("Nein", null);
			dg.show();
			break;
		case R.id.iSort:
			SharedPreferences settings = getSharedPreferences("mysettings",
					Context.MODE_PRIVATE);
			selected = settings.getInt("sorting", 0);

			AlertDialog.Builder dee = new AlertDialog.Builder(this);
			dee.setTitle("Sortieren");
			dee.setNeutralButton("Abbrechen", null);
			dee.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences settings = getSharedPreferences(
							"mysettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();

					editor.putInt("sorting", selected);
					editor.commit();
					createList();
				}
			});
			dee.setSingleChoiceItems(R.array.sorting_entries, selected,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							selected = which;
						}
					});
			dee.show();
			break;
		case R.id.iEdit:
			Intent iedit = new Intent(Overview.this, SelectEdit.class);
			iedit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iedit);
			break;
		case R.id.kImport:
			com.martin.noten.DatabaseHandler dbN = new com.martin.noten.DatabaseHandler(
					this);
			List<com.martin.noten.Fach> nFaecher = dbN.getAllFaecher(this, 1);

			com.martin.kontingent.DatabaseHandler dbK = new com.martin.kontingent.DatabaseHandler(
					this);
			List<com.martin.kontingent.Fach> kFaecher = dbK.getAllFaecher(this);

			com.martin.noten.Fach entryN = null;
			com.martin.kontingent.Fach entryK = null;

			boolean exists;
			boolean imported = false;

			for (int i = 0; i < nFaecher.size(); i++) {
				entryN = nFaecher.get(i);
				exists = false;

				for (int z = 0; z < kFaecher.size(); z++) {
					entryK = kFaecher.get(z);
					if (entryN.getName().contentEquals(
							entryK.getName().toString())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					imported = true;
					final String fname = entryN.getName();
					final com.martin.kontingent.DatabaseHandler fdb = dbK;

					final NumberPicker np = new NumberPicker(this);
					np.setMinValue(1);
					np.setMaxValue(8);
					np.setValue(4);
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle("Kontingent für " + fname);
					builder.setView(np);
					builder.setNegativeButton("Nicht importieren", null);
					builder.setPositiveButton("OK", new OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							com.martin.kontingent.Fach newFach = new com.martin.kontingent.Fach();
							newFach.setName(fname);
							newFach.setKont_av(np.getValue() + "");
							newFach.setKont_us("0");
							fdb.addFach(newFach);
							createList();
						}

					});
					builder.show();
				}
			}
			if (!imported) {
				Toast t = Toast
						.makeText(this, "Keine neuen Fächer zu importieren",
								Toast.LENGTH_SHORT);
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
	public void onClick(DialogInterface arg0, int arg1) {
		DatabaseHandler db = new DatabaseHandler(this);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext());
		Fach selected;
		for (int i = 0; i < db.getFachCount(); i++) {
			selected = db.getFach(faecher.get(i).getID());
			selected.setDates("empty");
			selected.setKont_us("0");
			db.updateFach(selected);
		}
		createList();
		Toast t = Toast.makeText(Overview.this, "Kontingent zurückgesetzt",
				Toast.LENGTH_SHORT);
		t.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		DatabaseHandler db = new DatabaseHandler(this);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext());

		Fach selected = db.getFach(faecher.get(arg2).getID());

		Bundle data = new Bundle();
		data.putInt("id", selected.getID());
		Intent i = new Intent(Overview.this, ViewFach.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.putExtras(data);
		startActivity(i);
	}

}
