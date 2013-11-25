package com.martin.noten;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.ContextMenu.ContextMenuInfo;
=======
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
>>>>>>> beta
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
<<<<<<< HEAD
import android.widget.SimpleAdapter;

import com.actionbarsherlock.view.ContextMenu;
import com.actionbarsherlock.view.MenuItem;
=======
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

>>>>>>> beta
import com.martin.kantidroid.R;

public class Semester2Fragment extends Fragment implements OnItemClickListener {

	ListView lv;
	TextView promoviert, pluspunkte;
	int selected, semester;
	String result;
	Fach entry;
<<<<<<< HEAD
	View separator;
	double schn = 0;
	Resources res;
	Fach fSelected = null;
=======
	double schn = 0;
	Resources res;
	Fach fSelected = null;
	RelativeLayout indicator;
>>>>>>> beta

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
<<<<<<< HEAD
		SharedPreferences spNoten = getActivity().getSharedPreferences(
				"MarkSettings", getActivity().MODE_PRIVATE);
		SharedPreferences.Editor editor = spNoten.edit();
		editor.putInt("selected_semester", 2);
		editor.commit();
=======
>>>>>>> beta
		return inflater.inflate(R.layout.activity_main_noten, container, false);
	}

	@Override
	public void onResume() {
		super.onResume();
		createList();
	}
<<<<<<< HEAD
	
=======

>>>>>>> beta
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
<<<<<<< HEAD
		com.actionbarsherlock.view.MenuInflater inflater = getSupportActivity().getSupportMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.cmEdit:
			DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
			List<Fach> faecher = db.getAllFaecher(getActivity().getApplicationContext(), 2);
=======
		MenuInflater inflater = getSupportActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.cmEdit:
			DatabaseHandler db = new DatabaseHandler(getActivity()
					.getApplicationContext());
			List<Fach> faecher = db.getAllFaecher(getActivity()
					.getApplicationContext(), 2);
>>>>>>> beta

			fSelected = db.getFach(faecher.get(info.position).getID());

			Bundle data = new Bundle();
			data.putInt("id", fSelected.getID());
			Intent i = new Intent(getActivity(), EditEntry.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtras(data);
			startActivity(i);
			break;
		case R.id.cmDelete:
<<<<<<< HEAD
			DatabaseHandler db1 = new DatabaseHandler(getActivity().getApplicationContext());
			List<Fach> faecher1 = db1.getAllFaecher(getActivity().getApplicationContext(), 2);
=======
			DatabaseHandler db1 = new DatabaseHandler(getActivity()
					.getApplicationContext());
			List<Fach> faecher1 = db1.getAllFaecher(getActivity()
					.getApplicationContext(), 2);
>>>>>>> beta

			fSelected = db1.getFach(faecher1.get(info.position).getID());

			AlertDialog.Builder dg = new AlertDialog.Builder(getActivity());
			dg.setTitle("Fach l�schen");
			dg.setMessage("Willst du das Fach " + fSelected.getName()
					+ " wirklich l�schen?");
			dg.setPositiveButton("Ja", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
<<<<<<< HEAD
					DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
					db.deleteFach(fSelected);
					createList();
				}
				
=======
					DatabaseHandler db = new DatabaseHandler(getActivity()
							.getApplicationContext());
					db.deleteFach(fSelected);
					createList();
				}

>>>>>>> beta
			});
			dg.setNegativeButton("Nein", null);
			dg.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void createList() {
<<<<<<< HEAD
		promoviert = (TextView) getActivity().findViewById(R.id.tvPromoviert);
		pluspunkte = (TextView) getActivity().findViewById(R.id.tvPlusP);
		lv = (ListView) getActivity().findViewById(R.id.lvMain_noten);
		separator = getActivity().findViewById(R.id.vSeparator);
=======
		promoviert = (TextView) getView().findViewById(R.id.tvPromoviert);
		pluspunkte = (TextView) getView().findViewById(R.id.tvPlusP);
		lv = (ListView) getView().findViewById(R.id.lvMain_noten);
		indicator = (RelativeLayout) getView().findViewById(R.id.rlNoten);
>>>>>>> beta
		ArrayList<Map<String, String>> list = buildData();
		String[] from = { "fach", "anzahl" };
		int[] to = { R.id.tvLeft, R.id.tvRight };

		SimpleAdapter adapter = new MyAdapter(getActivity(), list,
				R.layout.overview_list_item, from, to);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		registerForContextMenu(lv);
		getSem();
		res = getResources();
		checkPromo();
	}

	private void getSem() {
		semester = 2;
	}

	private void checkPromo() {
<<<<<<< HEAD
		SharedPreferences spNoten = getActivity().getSharedPreferences("MarkSettings", getActivity().MODE_PRIVATE);
=======
		SharedPreferences spNoten = getActivity().getSharedPreferences(
				"MarkSettings", getActivity().MODE_PRIVATE);
>>>>>>> beta
		String sAbteilung = spNoten.getString("Abteilung", "Gym");
		PromoCheck prCheck = new PromoCheck(getActivity());
		PromoRes prResult = null;
		if (sAbteilung.contentEquals("Gym")) {
			prResult = prCheck.getGym(semester);
<<<<<<< HEAD
		}
		else if (sAbteilung.contentEquals("HMS")) {
			prResult = prCheck.getHMS(semester);
		}
		else {
			prResult = prCheck.getFMS(semester);
		}
		promoviert.setText(prResult.sMessage);
		promoviert.setTextColor(res.getColor(prResult.iColor));
		pluspunkte.setText(prResult.sPP);
		pluspunkte.setTextColor(res.getColor(prResult.iColor));
		separator.setBackgroundColor(res.getColor(prResult.iColor));
=======
		} else if (sAbteilung.contentEquals("HMS")) {
			prResult = prCheck.getHMS(semester);
		} else {
			prResult = prCheck.getFMS(semester);
		}
		promoviert.setText(prResult.sMessage);
		indicator.setBackgroundColor(res.getColor(prResult.iColor));
		pluspunkte.setText(prResult.sPP);
>>>>>>> beta
	}

	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getActivity());
		int count = db.getFachCount();
		List<Fach> faecher = db.getAllFaecher(getActivity(), 2);
		for (int i = 0; i < count; i++) {
			entry = faecher.get(i);
			if (entry.getMathAverage2().contentEquals("-")) {
				result = "-";
			} else {
				result = String.format("%.2f",
						Double.parseDouble(entry.getMathAverage2()));
			}
			list.add(putData(entry.getName(), result));
		}
		return list;
	}

	private HashMap<String, String> putData(String fach, String anzahl) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("fach", fach);
		item.put("anzahl", anzahl);
		return item;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		DatabaseHandler db = new DatabaseHandler(getActivity());
		List<Fach> faecher = db.getAllFaecher(getActivity(), 2);

		Fach selected = db.getFach(faecher.get(arg2).getID());

		Bundle data = new Bundle();
		data.putInt("id", selected.getID());
		data.putInt("semester", 2);
		Intent i = new Intent(getActivity(), ViewFach.class);
		i.putExtras(data);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

}