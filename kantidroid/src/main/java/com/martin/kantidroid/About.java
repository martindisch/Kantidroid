package com.martin.kantidroid;

import android.os.Bundle;
import android.view.MenuItem;

import org.holoeverywhere.preference.PreferenceActivity;

public class About extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.about);
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
