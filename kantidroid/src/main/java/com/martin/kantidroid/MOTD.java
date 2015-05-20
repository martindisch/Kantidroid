package com.martin.kantidroid;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MOTD extends AppCompatActivity {

    private String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getData();
        Typeface tfT = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface tfL = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        setContentView(R.layout.motd);

        TextView title = (TextView) findViewById(R.id.tvMOTD_title);
        TextView message = (TextView) findViewById(R.id.tvMOTD_message);
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

    private String unescape(String s) {
        return s.replace("*", "\n");
    }

}
