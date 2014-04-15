package com.martin.kantidroid;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.Window;

public class Food extends Activity implements RestsLoaded {

	private String[] weekdays = { "Montag", "Dienstag", "Mittwoch",
			"Donnerstag", "Freitag", "Samstag", "Sonntag" };
	private String[] sRests = { "mensa", "bodmer", "konvikt", "cafemartin" };
	private String[][] sMenu = new String[7][4];
	private String[] sDates = new String[7];
	private int iDay;
	private boolean bLoaded = false;

	private FoodPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pd = new ProgressDialog(this);
		pd.setMessage("Menuplan wird geladen...");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		}

		setContentView(R.layout.viewpagerlayout_food);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// PAGER

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new FoodPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);

		getCurrentWeek();

		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.menuplan);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
	}

	private void getCurrentWeek() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			pd.show();
		}

		bLoaded = false;

		new Thread(new Runnable() {

			@Override
			public void run() {
				// Get JSON response
				ServiceHandler sh = new ServiceHandler();
				final String jsonStr = sh.makeServiceCall(
						getString(R.string.getcurrent), ServiceHandler.GET);

				if (jsonStr != null) {
					try {
						// Get the top object, representing all restaurants for a week
						JSONObject rests = new JSONObject(jsonStr);

						// Get the extras
						JSONObject extras = (JSONObject) rests.get("extras");
						makeDay(extras.getString("day"));
						makeDates((String) extras.get("kw"));

						// Iterate through all restaurants
						for (int i = 0; i < 4; i++) {

							// Get the current restaurant object
							JSONObject rest = (JSONObject) rests.get(sRests[i]);

							// Iterate through all the days
							for (int z = 0; z < 7; z++) {
								sMenu[z][i] = rest.getString(weekdays[z]);
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						// Now that we're loaded, start displaying the fragments
						mViewPager.setAdapter(mSectionsPagerAdapter);
						if (iDay < 6) {
							mViewPager.setCurrentItem(iDay - 1);
						}
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							setSupportProgressBarIndeterminateVisibility(false);
						} else {
							pd.hide();
						}
					}
				});
			}
		}).start();
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

	public Fragment findFragmentByPosition(int position) {
		FoodPagerAdapter fragmentPagerAdapter = mSectionsPagerAdapter;
		return getSupportFragmentManager().findFragmentByTag(
				"android:switcher:" + mViewPager.getId() + ":"
						+ fragmentPagerAdapter.getItemId(position));
	}

	private void makeDates(String kw) {
		Calendar c = Calendar.getInstance();
		if (iDay < 6) {
			c.add(Calendar.DAY_OF_WEEK, -(iDay - 1));
		} else {
			c.add(Calendar.DAY_OF_WEEK, +(7 - iDay) + 1);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

		for (int i = 0; i < sDates.length; i++) {
			sDates[i] = sdf.format(c.getTime());
			c.add(Calendar.DAY_OF_WEEK, 1);
		}
	}

	private void makeDay(String day) {
		iDay = Integer.valueOf(day);
	}

	@Override
	public boolean loaded() {
		return bLoaded;
	}

	@Override
	public String[][] getMenus() {
		return sMenu;
	}

	@Override
	public String[] getDates() {
		return sDates;
	}

}
