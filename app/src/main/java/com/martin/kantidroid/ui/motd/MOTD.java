package com.martin.kantidroid.ui.motd;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.martin.kantidroid.R;

public class MOTD extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motd_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        String[] list = getIntent().getStringArrayExtra("data");

        TextView tvTitle = (TextView) findViewById(R.id.tvMOTD_title);
        TextView tvMessage = (TextView) findViewById(R.id.tvMOTD_message);
        tvMessage.setSingleLine(false);

        tvTitle.setText(list[0]);
        tvMessage.setText(unescape(list[1]));
    }

    String unescape(String s) {
        return s.replace("*", "\n");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
