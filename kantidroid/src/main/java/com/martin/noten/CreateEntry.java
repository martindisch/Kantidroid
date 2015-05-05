package com.martin.noten;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.AutoCompleteTextView;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.Toast;

public class CreateEntry extends Activity implements OnClickListener, OnCheckedChangeListener {

    AutoCompleteTextView fach;
    Button save, cancel;
    CheckBox another, promotionsrelevant;

    @Override
    protected void onStop() {
        super.onStop();
        Intent rIntent = new Intent(this, WidgetProvider.class);
        rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(rIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entry_noten);
        initialize();
        getCheckbox();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
        fach = (AutoCompleteTextView) findViewById(R.id.etAddFach);
        save = (Button) findViewById(R.id.bAddSave);
        cancel = (Button) findViewById(R.id.bAddCancel);
        another = (CheckBox) findViewById(R.id.cbAnother);
        promotionsrelevant = (CheckBox) findViewById(R.id.cbPromotionsrelevant);

        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        another.setOnCheckedChangeListener(this);

        String[] names = getResources().getStringArray(R.array.faecher_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_1, names);
        fach.setAdapter(adapter);
    }

    private void getCheckbox() {
        getApplicationContext();
        SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
        boolean bAnother = settings.getBoolean("anotherFach", true);

        if (bAnother == true) {
            another.setChecked(true);
        } else {
            another.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bAddSave:
                String sFach = fach.getText().toString();
                String sPromotionsrelevant = "false";
                if (promotionsrelevant.isChecked()) {
                    sPromotionsrelevant = "true";
                }

                if (!sFach.contentEquals("")) {

                    Fach ffach = new Fach(sFach, sPromotionsrelevant);
                    DatabaseHandler db = new DatabaseHandler(this);

                    db.addFach(ffach);

                    if (another.isChecked()) {
                        fach.setText("");
                        Toast t = Toast.makeText(CreateEntry.this, "Fach gespeichert", Toast.LENGTH_SHORT);
                        t.show();
                        fach.requestFocus();
                    } else {
                        finish();
                    }
                } else {
                    Toast t = Toast.makeText(CreateEntry.this, "Leeres Feld", Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
            case R.id.bAddCancel:
                finish();
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        getApplicationContext();
        SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        if (another.isChecked()) {
            editor.putBoolean("anotherFach", true);
        } else {
            editor.putBoolean("anotherFach", false);
        }

        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
