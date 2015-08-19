package com.martin.kantidroid.ui.motd;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.martin.kantidroid.R;

public class MOTD extends AppCompatActivity {

    private String[] list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motd_activity);

        getData();

        TextView tvTitle = (TextView) findViewById(R.id.tvMOTD_title);
        TextView tvMessage = (TextView) findViewById(R.id.tvMOTD_message);
        tvMessage.setSingleLine(false);

        tvTitle.setText(list[0]);
        tvMessage.setText(unescape(list[1]));
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        list = received.getStringArray("data");
    }

    String unescape(String s) {
        return s.replace("*", "\n");
    }
}
