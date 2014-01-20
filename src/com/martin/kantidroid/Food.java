package com.martin.kantidroid;

import java.util.Calendar;
import java.util.Locale;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.ProgressDialog;
import org.holoeverywhere.preference.SharedPreferences;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

public class Food extends Activity implements RestsLoaded {

	private String[] weekdays = { "Montag", "Dienstag", "Mittwoch",
			"Donnerstag", "Freitag", "Samstag", "Sonntag" };
	private String[] sRests = { "mensa", "bodmer", "konvikt", "cafemartin", "migros" };
	private String[][] sMenu = new String[7][5];
	private String[] sDates = new String[7];
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
						makeDates((String) extras.get("kw"));

						// Iterate through all restaurants
						for (int i = 0; i < 5; i++) {

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

	/*public void getResponse(final String url, final String request) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				// Get JSON response
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("request", request));
				ServiceHandler sh = new ServiceHandler();
				final String jsonStr = sh.makeServiceCall(url,
						ServiceHandler.GET, params);

				if (jsonStr != null
						&& !jsonStr.contentEquals("invalid request")) {
					try {
						// Get top object, respresenting a single restaurant and containing all kalenderwochen
						JSONObject rest = new JSONObject(jsonStr);

						// Iterate through all kalenderwochen
						Iterator<String> iter = rest.keys();

						while (iter.hasNext()) {

							String key = iter.next();
							sOut += "Kalenderwoche " + key + "\n";
							sOut += "==============\n\n";

							// Get the current kalenderwochen object
							final JSONObject kw = (JSONObject) rest.get(key);

							// Iterate through all the days
							for (String day : weekdays) {
								sOut += day + "\n";
								sOut += "************\n\n";
								sOut += kw.getString(day) + "\n\n";
							}
							sOut += "\n";

						}

						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								tvOut.setText(sOut);
								sOut = "";
							}
						});
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}*/

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
	
	/*private void makeDates(String kw, String sDay, String sD, String sM, String sY) {
		int day = Integer.valueOf(sDay);
		int d = Integer.valueOf(sD);
		int m = Integer.valueOf(sM);
		int y = Integer.valueOf(sY);
		
		day--;
		
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		
		
		if (day < 5) {
			for (int i = 0; i < 7; i++) {
				sDates[i] = d + "." + m + "." + y;
			}
			sDates[day] = d + "." + m + "." + y;
		}
		
	}*/
	
	private void makeDates(String kw) {
		for (int i = 0; i < sDates.length; i++) {
			sDates[i] = Integer.valueOf(kw) + "";
		}
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
