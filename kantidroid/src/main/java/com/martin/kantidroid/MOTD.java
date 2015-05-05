package com.martin.kantidroid;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import org.holoeverywhere.app.Activity;

public class MOTD extends Activity {

    private String[] list;
    private TextView title, message;
    private Typeface tfT, tfL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        tfT = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        tfL = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        setContentView(R.layout.motd);

        title = (TextView) findViewById(R.id.tvMOTD_title);
        message = (TextView) findViewById(R.id.tvMOTD_message);
        message.setSingleLine(false);

        title.setText(list[0]);
        title.setTypeface(tfT);
        message.setText(unescape(list[1]));
        message.setTypeface(tfL);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        list = received.getStringArray("data");
    }

    String unescape(String s) {
        return s.replace("*", "\n");
    }

}
