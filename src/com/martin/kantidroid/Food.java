package com.martin.kantidroid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.holoeverywhere.app.Activity;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Food extends Activity {

	private TextView tvOut;
	private EditText etRequest;
	private Button bRequest;
	private Typeface tf;
	private String sOut = "";
	private String[] weekdays = { "Montag", "Dienstag", "Mittwoch",
			"Donnerstag", "Freitag", "Samstag", "Sonntag" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");

		tvOut = (TextView) findViewById(R.id.tvFoodText);
		tvOut.setTypeface(tf);
		etRequest = (EditText) findViewById(R.id.etFoodRequest);
		bRequest = (Button) findViewById(R.id.bFoodLoad);
		bRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getResponse(getString(R.string.testpage), etRequest.getText()
						.toString());
			}

		});
	}

	private void getResponse(final String url, final String request) {

		new Thread(new Runnable() {

			@Override
			public void run() {

				// Get JSON response
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("request", request));
				ServiceHandler sh = new ServiceHandler();
				final String jsonStr = sh.makeServiceCall(url,
						ServiceHandler.GET, params);

				if (jsonStr != null && !jsonStr.contentEquals("invalid request")) {
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
				else {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							tvOut.setText("Invalid request");
						}
					});
				}

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

}
