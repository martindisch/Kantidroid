package com.martin.noten;

import java.util.List;

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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

	// PAGER

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpagerlayout_noten);
		// PAGER

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int arg0) {
				getApplicationContext();
				SharedPreferences spNoten = getApplicationContext().getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = spNoten.edit();
				switch (arg0) {
				case 0:
					editor.putInt("selected_semester", 1);
					break;
				case 1:
					editor.putInt("selected_semester", 2);
					break;
				case 2:
					editor.putInt("selected_semester", 3);
					break;
				}
				editor.commit();
			}

		});
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
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.menu_noten, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
			SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
			selected = settings.getInt("sorting", 0);

			AlertDialog.Builder dee = new AlertDialog.Builder(this);
			dee.setTitle("Sortieren");
			dee.setNeutralButton("Abbrechen", null);
			dee.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();

					editor.putInt("sorting", selected);
					editor.commit();
					mViewPager.setAdapter(mSectionsPagerAdapter);
				}
			});
			dee.setSingleChoiceItems(R.array.sorting_entries, selected, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					selected = which;
				}
			});
			dee.show();
			break;
		case R.id.iAbteilung:
			selected = 0;
			SharedPreferences spNoten = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
			String sAbteilung = spNoten.getString("Abteilung", "Gym");
			if (sAbteilung.contentEquals("HMS")) {
				selected = 1;
			} else if (sAbteilung.contentEquals("FMS")) {
				selected = 2;
			}

			AlertDialog.Builder dr = new AlertDialog.Builder(this);
			dr.setTitle("Abteilung wählen");
			dr.setNeutralButton("Abbrechen", null);
			dr.setPositiveButton("OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					String Abteilung = "Gym";

					if (selected == 1) {
						Abteilung = "HMS";
					} else if (selected == 2) {
						Abteilung = "FMS";
					}

					editor.putString("Abteilung", Abteilung);
					editor.commit();
					mViewPager.setAdapter(mSectionsPagerAdapter);
				}
			});
			dr.setSingleChoiceItems(R.array.abteilungen, selected, new DialogInterface.OnClickListener() {

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
			com.martin.noten.DatabaseHandler dbN = new com.martin.noten.DatabaseHandler(this);
			List<com.martin.noten.Fach> nFaecher = dbN.getAllFaecher(this, 1);

			com.martin.kontingent.DatabaseHandler dbK = new com.martin.kontingent.DatabaseHandler(this);
			List<com.martin.kontingent.Fach> kFaecher = dbK.getAllFaecher(this);

			com.martin.noten.Fach entryN = null;
			com.martin.kontingent.Fach entryK = null;

			boolean exists;
			boolean imported = false;

			for (int i = 0; i < kFaecher.size(); i++) {
				entryK = kFaecher.get(i);
				exists = false;

				for (int z = 0; z < nFaecher.size(); z++) {
					entryN = nFaecher.get(z);
					if (entryK.getName().contentEquals(entryN.getName().toString())) {
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

				}
			}
			if (!imported) {
				Toast t = Toast.makeText(this, "Keine neuen Fächer zu importieren", Toast.LENGTH_SHORT);
				t.show();
			} else {
				AlertDialog.Builder impinfo = new AlertDialog.Builder(this);
				impinfo.setTitle("Info");
				impinfo.setMessage("Alle Fächer wurden als Promotionsfächer übernommen. Sollte dies nicht korrekt sein, kannst du das über den Menüpunkt 'Fach bearbeiten' korrigieren.");
				impinfo.setNeutralButton("Schliessen", null);
				impinfo.show();
				mViewPager.setAdapter(mSectionsPagerAdapter);
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
		SharedPreferences spNoten = this.getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
		List<Fach> faecher = db.getAllFaecher(getApplicationContext(), spNoten.getInt("selected_semester", 1));
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
		mViewPager.setAdapter(mSectionsPagerAdapter);

		Toast t = Toast.makeText(Main.this, "Noten zurückgesetzt", Toast.LENGTH_SHORT);
		t.show();

	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
		if (settings.getInt("selected_semester", 1) == 1) {
			mViewPager.setCurrentItem(0);
		} else if (settings.getInt("selected_semester", 1) == 2) {
			mViewPager.setCurrentItem(1);
		} else {
			mViewPager.setCurrentItem(2);
		}
	}
}
