package com.martin.noten;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Fragment;
import org.holoeverywhere.widget.ListView;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;

import com.martin.kantidroid.R;

public class ZeugnisFragment extends Fragment {

	ListView lv;
	TextView promoviert, pluspunkte;
	int selected, semester;
	String result;
	Fach entry;
	RelativeLayout indicator;
	double schn = 0;
	Resources res;
	Fach fSelected = null;
	private Fach[] toSort;
	private static boolean ASC;
	private int index, top;
	private boolean savedState = false;
	private ExecutorService mExecutor;
	Future<?> mZeugnisLoader;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setRetainInstance(true);
		mExecutor = Executors.newSingleThreadExecutor();
		return inflater.inflate(R.layout.activity_main_noten, container, false);
	}

	@Override
	public void onPause() {
		super.onPause();
		index = lv.getFirstVisiblePosition();
		View v = lv.getChildAt(0);
		top = (v == null) ? 0 : v.getTop();
		savedState = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		mZeugnisLoader = mExecutor.submit(new Runnable() {

			@Override
			public void run() {
				createList();
			}

		});
	}

	@Override
	public void onStop() {
		super.onStop();
		try {
			if (mZeugnisLoader.get() != null) {
				mZeugnisLoader.cancel(true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, view, menuInfo);
		MenuInflater inflater = getSupportActivity().getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.cmEdit:
			fSelected = toSort[info.position];

			Bundle data = new Bundle();
			data.putInt("id", fSelected.getID());
			Intent i = new Intent(getActivity(), EditEntry.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtras(data);
			startActivity(i);
			break;
		case R.id.cmDelete:
			fSelected = toSort[info.position];

			AlertDialog.Builder dg = new AlertDialog.Builder(getActivity());
			dg.setTitle("Fach löschen");
			dg.setMessage("Willst du das Fach " + fSelected.getName() + " wirklich löschen?");
			dg.setPositiveButton("Ja", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());
					db.deleteFach(fSelected);
					((FragmentHolder) getActivity()).reloadFragments();
				}

			});
			dg.setNegativeButton("Nein", null);
			dg.show();
			break;
		}
		return super.onContextItemSelected(item);
	}

	public void createList() {
		promoviert = (TextView) getView().findViewById(R.id.tvPromoviert);
		pluspunkte = (TextView) getView().findViewById(R.id.tvPlusP);
		lv = (ListView) getView().findViewById(R.id.lvMain_noten);
		indicator = (RelativeLayout) getView().findViewById(R.id.rlNoten);
		ArrayList<Map<String, String>> list = buildData();
		String[] from = { "fach", "anzahl" };
		int[] to = { R.id.tvLeft, R.id.tvRight };

		final SimpleAdapter adapter = new MyAdapter(getActivity(), list, R.layout.overview_list_item, from, to);
		lv.post(new Runnable() {

			@Override
			public void run() {
				lv.setAdapter(adapter);
				if (savedState) {
					lv.setSelectionFromTop(index, top);
				}
			}
		});
		registerForContextMenu(lv);
		getSem();
		res = getResources();
		checkPromo();
	}

	private void getSem() {
		semester = 3;
	}

	private void checkPromo() {
		SharedPreferences spNoten = getActivity().getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
		String sAbteilung = spNoten.getString("Abteilung", "Gym");
		PromoCheck prCheck = new PromoCheck(getActivity());
		PromoRes prResult = null;
		if (sAbteilung.contentEquals("Gym")) {
			prResult = prCheck.getGym(semester);
		} else if (sAbteilung.contentEquals("HMS")) {
			prResult = prCheck.getHMS(semester);
		} else {
			prResult = prCheck.getFMS(semester);
		}

		final PromoRes resources = prResult;

		lv.post(new Runnable() {

			@Override
			public void run() {
				promoviert.setText(resources.sMessage);
				indicator.setBackgroundColor(res.getColor(resources.iColor));
				pluspunkte.setText(resources.sPP);
			}
		});
	}

	private static void shellsort(Fach[] a) {
		int j;
		for (int gap = a.length / 2; gap > 0; gap /= 2) {
			for (int i = gap; i < a.length; i++) {
				Fach tmp = a[i];
				for (j = i; j >= gap && check(getNumber(tmp.getZeugnis()), getNumber(a[j - gap].getZeugnis())); j -= gap) {
					a[j] = a[j - gap];
				}
				a[j] = tmp;
			}
		}
	}

	private static double getNumber(String text) {
		if (text.contentEquals("-")) {
			return 0;
		}
		return Double.parseDouble(text);
	}

	private static boolean check(double a, double b) {
		if (!ASC) {
			if (a > b) {
				return true;
			} else {
				return false;
			}
		} else {
			if (a < b) {
				return true;
			}
		}
		return false;
	}

	private ArrayList<Map<String, String>> buildData() {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DatabaseHandler db = new DatabaseHandler(getActivity());
		List<Fach> faecher = db.getAllFaecher(getActivity(), 3);
		toSort = new Fach[faecher.size()];
		for (int i = 0; i < faecher.size(); i++) {
			toSort[i] = faecher.get(i);
		}

		SharedPreferences settings = getActivity().getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
		ASC = false;
		if (settings.getInt("sorting", 1) == 2) {
			ASC = true;
		}

		shellsort(toSort);

		for (Fach entry : toSort) {
			list.add(putData(entry.getName(), entry.getZeugnis()));
		}

		return list;
	}

	private HashMap<String, String> putData(String fach, String anzahl) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("fach", fach);
		item.put("anzahl", anzahl);
		return item;
	}

}