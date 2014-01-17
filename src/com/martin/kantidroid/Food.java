package com.martin.kantidroid;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.holoeverywhere.app.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
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
				getResponse(getString(R.string.testpage), etRequest.getText().toString());
			}

		});
	}

	private void getResponse(final String url, final String request) {

		final String sReturn = "nil";

		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpClient httpclient = new DefaultHttpClient();

				try {
					final HttpResponse response = httpclient
							.execute(new HttpGet(url + "?request=" + request));

					final String sTemp = EntityUtils.toString(
							response.getEntity()).replace("<br>", "\n");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							tvOut.setText(sTemp);
						}
					});

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
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
