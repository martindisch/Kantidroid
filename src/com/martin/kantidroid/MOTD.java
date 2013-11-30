package com.martin.kantidroid;

import org.holoeverywhere.app.Activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class MOTD extends Activity {

	private String[] list;
	private TextView title, message;
	private Typeface tf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getData();
		tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
		setContentView(R.layout.motd);

		title = (TextView) findViewById(R.id.tvMOTD_title);
		message = (TextView) findViewById(R.id.tvMOTD_message);
		message.setSingleLine(false);

		title.setText(list[0]);
		title.setTypeface(tf);
		message.setText(unescape(list[1]));
	}

	private void getData() {
		Bundle received = getIntent().getExtras();
		list = received.getStringArray("data");
	}

	String unescape(String s) {
		return s.replace("*", "\n");
	}

}
