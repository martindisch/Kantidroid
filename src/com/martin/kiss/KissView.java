package com.martin.kiss;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.martin.kantidroid.R;

public class KissView extends Activity {

	TextView tvDate;
	WebView wvKISS;

	@Override
	protected void onCreate(Bundle sSavedInstanceState) {
		super.onCreate(sSavedInstanceState);
		setContentView(R.layout.kiss_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		tvDate = (TextView) findViewById(R.id.kvDate);
		wvKISS = (WebView) findViewById(R.id.kvWebView);
		SharedPreferences spKISS = getSharedPreferences("KISS", MODE_PRIVATE);
		if (!spKISS.getString("last_refresh", "-").contentEquals("-")) {
			tvDate.setText("KISS vom " + spKISS.getString("last_refresh", "KISS nie geladen"));
		} else {
			tvDate.setText("Kein aktuelles KISS verfügbar");
		}
		wvKISS.loadDataWithBaseURL(null, spKISS.getString("KISS", "Blook"), "text/html", "utf-8", null);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
