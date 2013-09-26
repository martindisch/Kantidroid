package com.martin.noten;

import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;

public class Main extends Activity implements OnClickListener {

	ListView lv;
	TextView promoviert, pluspunkte;
	int selected, semester;
	String result;
	Fach entry;
	View separator;
	double schn = 0;
	Resources res;
	int iSemester = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ActionBar.Tab tabA = bar.newTab().setText("1. Semester");
		ActionBar.Tab tabB = bar.newTab().setText("2. Semester");
		ActionBar.Tab tabC = bar.newTab().setText("Zeugnis");

		tabA.setTabListener(new TabListener<Semester1Fragment>(this,
				"semester1", Semester1Fragment.class));
		tabB.setTabListener(new TabListener<Semester2Fragment>(this,
				"semester2", Semester2Fragment.class));
		tabC.setTabListener(new TabListener<ZeugnisFragment>(this, "zeugnis",
				ZeugnisFragment.class));

		SharedPreferences spNoten = this.getSharedPreferences("MarkSettings",
				this.MODE_PRIVATE);
		if (spNoten.getInt("selected_semester", 1) == 1) {
			bar.addTab(tabA, 0, true);
			bar.addTab(tabB, 1, false);
			bar.addTab(tabC, 2, false);
		} else if (spNoten.getInt("selected_semester", 1) == 2) {
			bar.addTab(tabA, 0, false);
			bar.addTab(tabB, 1, true);
			bar.addTab(tabC, 2, false);
		} else {
			bar.addTab(tabA, 0, false);
			bar.addTab(tabB, 1, false);
			bar.addTab(tabC, 2, true);
		}
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.noten_main);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		if (getIntent().hasExtra("Internal_call")) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater inflator = getSupportMenuInflater();
		inflator.inflate(R.menu.menu_noten, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId,
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.iAdd:
			Intent iadd = new Intent(Main.this, CreateEntry.class);
			iadd.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iadd);
			break;
		case R.id.iRemove:
			Intent iremove = new Intent(Main.this, RemoveEntry.class);
			iremove.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iremove);
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
			dg.setTitle("Jahr abschliessen");
			dg.setMessage("Willst du das Jahr abschliessen und ein neues beginnen?");
			dg.setPositiveButton("Ja", this);
			dg.setNegativeButton("Nein", null);
			dg.show();
			break;
		case R.id.iSort:
			selected = 0;
			SharedPreferences settings = getSharedPreferences("MarkSettings",
					Context.MODE_PRIVATE);
			selected = settings.getInt("sorting", 0);

			AlertDialog.Builder dee = new AlertDialog.Builder(this);
			dee.setTitle("Sortieren");
			dee.setNeutralButton("Abbrechen", null);
			dee.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences settings = getSharedPreferences(
							"MarkSettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();

					editor.putInt("sorting", selected);
					editor.commit();
					if (settings.getInt("selected_semester", 1) == 1) {
						getSupportFragmentManager().findFragmentByTag(
								"semester1").onResume();
					} else if (settings.getInt("selected_semester", 1) == 2) {
						getSupportFragmentManager().findFragmentByTag(
								"semester2").onResume();
					} else {
						getSupportFragmentManager()
								.findFragmentByTag("zeugnis").onResume();
					}

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
		case R.id.iAbteilung:
			selected = 0;
			SharedPreferences spNoten = getSharedPreferences("MarkSettings",
					Context.MODE_PRIVATE);
			String sAbteilung = spNoten.getString("Abteilung", "Gym");
			if (sAbteilung.contentEquals("HMS")) {
				selected = 1;
			}
			else if (sAbteilung.contentEquals("FMS")) {
				selected = 2;
			}

			AlertDialog.Builder dr = new AlertDialog.Builder(this);
			dr.setTitle("Abteilung wählen");
			dr.setNeutralButton("Abbrechen", null);
			dr.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences settings = getSharedPreferences(
							"MarkSettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					String Abteilung = "Gym";
					
					if (selected == 1) {
						Abteilung = "HMS";
					}
					else if (selected == 2) {
						Abteilung = "FMS";
					}
					
					editor.putString("Abteilung", Abteilung);
					editor.commit();
					if (settings.getInt("selected_semester", 1) == 1) {
						getSupportFragmentManager().findFragmentByTag(
								"semester1").onResume();
					} else if (settings.getInt("selected_semester", 1) == 2) {
						getSupportFragmentManager().findFragmentByTag(
								"semester2").onResume();
					} else {
						getSupportFragmentManager()
								.findFragmentByTag("zeugnis").onResume();
					}

				}
			});
			dr.setSingleChoiceItems(R.array.abteilungen, selected,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							selected = which;
						}
					});
			dr.show();
			break;
		case R.id.iEdit:
			Intent iedit = new Intent(Main.this, EditSelect.class);
			iedit.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iedit);
			break;
		case R.id.nImport:
			com.martin.noten.DatabaseHandler dbN = new com.martin.noten.DatabaseHandler(
					this);
			List<com.martin.noten.Fach> nFaecher = dbN.getAllFaecher(this, 1);

			com.example.kontingentprototype.DatabaseHandler dbK = new com.example.kontingentprototype.DatabaseHandler(
					this);
			List<com.example.kontingentprototype.Fach> kFaecher = dbK
					.getAllFaecher(this);

			com.martin.noten.Fach entryN = null;
			com.example.kontingentprototype.Fach entryK = null;

			boolean exists;
			boolean imported = false;

			for (int i = 0; i < kFaecher.size(); i++) {
				entryK = kFaecher.get(i);
				exists = false;

				for (int z = 0; z < nFaecher.size(); z++) {
					entryN = nFaecher.get(z);
					if (entryK.getName().contentEquals(
							entryN.getName().toString())) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					imported = true;

					com.martin.noten.Fach newFach = new com.martin.noten.Fach();
					newFach.setName(entryK.getName());
					newFach.setPromotionsrelevant("true");
					dbN.addFach(newFach);
					onResume();

				}
			}
			if (!imported) {
				Toast t = Toast
						.makeText(this, "Keine neuen Fächer zu importieren",
								Toast.LENGTH_SHORT);
				t.show();
			} else {
				AlertDialog.Builder impinfo = new AlertDialog.Builder(this);
				impinfo.setTitle("Info");
				impinfo.setMessage("Alle Fächer wurden als Promotionsfächer übernommen. Sollte dies nicht korrekt sein, kannst du das über den Menüpunkt 'Fach bearbeiten' korrigieren.");
				impinfo.setNeutralButton("Schliessen", null);
				impinfo.show();
			}
			break;
		case android.R.id.home:
			finish();
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		DatabaseHandler db = new DatabaseHandler(this);
		SharedPreferences spNoten = this.getSharedPreferences("MarkSettings",
				this.MODE_PRIVATE);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext(),
				spNoten.getInt("selected_semester", 1));
		Fach selected;
		for (int i = 0; i < db.getFachCount(); i++) {
			selected = db.getFach(faecher.get(i).getID());
			selected.setMathAverage1("-");
			selected.setNoten1("-");
			selected.setRealAverage1("-");
			selected.setMathAverage2("-");
			selected.setNoten2("-");
			selected.setRealAverage2("-");
			selected.setZeugnis("-");
			db.updateFach(selected);
		}
		SharedPreferences settings = getSharedPreferences("MarkSettings",
				Context.MODE_PRIVATE);
		if (settings.getInt("selected_semester", 1) == 1) {
			getSupportFragmentManager().findFragmentByTag("semester1")
					.onResume();
		} else if (settings.getInt("selected_semester", 1) == 2) {
			getSupportFragmentManager().findFragmentByTag("semester2")
					.onResume();
		} else {
			getSupportFragmentManager().findFragmentByTag("zeugnis").onResume();
		}

		Toast t = Toast.makeText(Main.this, "Noten zurückgesetzt",
				Toast.LENGTH_SHORT);
		t.show();

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences("MarkSettings",
				Context.MODE_PRIVATE);
		if (settings.getInt("selected_semester", 1) == 1) {
			getSupportFragmentManager().findFragmentByTag("semester1")
					.onResume();
			getSupportFragmentManager().findFragmentByTag("semester1")
					.setRetainInstance(true);
		} else if (settings.getInt("selected_semester", 1) == 2) {
			getSupportFragmentManager().findFragmentByTag("semester2")
					.onResume();
			getSupportFragmentManager().findFragmentByTag("semester2")
					.setRetainInstance(true);
		} else {
			getSupportFragmentManager().findFragmentByTag("zeugnis").onResume();
			getSupportFragmentManager().findFragmentByTag("zeugnis")
					.setRetainInstance(true);
		}
	}
}
