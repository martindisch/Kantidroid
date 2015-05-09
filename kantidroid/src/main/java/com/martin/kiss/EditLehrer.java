package com.martin.kiss;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

public class EditLehrer extends Activity implements OnClickListener {

    private String sName;
    private EditText etName;
    private Button bSave;
    private Button bCancel;
    private TextView tvCount;

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
    protected void onCreate(Bundle sSavedInstanceState) {
        super.onCreate(sSavedInstanceState);
        setContentView(R.layout.edit_lehrer);
        getData();
        initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initialize() {
        getApplicationContext();
        SharedPreferences spRem = getApplicationContext().getSharedPreferences("Rem", Context.MODE_PRIVATE);
        etName = (EditText) findViewById(R.id.etEditName);
        tvCount = (TextView) findViewById(R.id.tvEditCount);
        bSave = (Button) findViewById(R.id.bEditSave);
        bCancel = (Button) findViewById(R.id.bEditCancel);
        bSave.setOnClickListener(this);
        bCancel.setOnClickListener(this);
        etName.setText(sName);
        tvCount.setText(String.valueOf(spRem.getInt(sName, 0)));
    }

    private void getData() {
        Bundle bReceived = getIntent().getExtras();
        sName = bReceived.getString("name");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bEditSave:
                if (!etName.getText().toString().contentEquals("")) {
                    getApplicationContext();
                    SharedPreferences spKISS = getApplicationContext().getSharedPreferences("KISS", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = spKISS.edit();
                    if (!sName.contentEquals(etName.getText().toString())) {
                        getApplicationContext();
                        SharedPreferences spRem = getApplicationContext().getSharedPreferences("Rem", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = spRem.edit();
                        ed.putInt(etName.getText().toString(), spRem.getInt(sName, 0));
                        ed.remove(sName);
                        ed.commit();
                        String sOld_List = spKISS.getString("lehrer", "");
                        String sNew_List = sOld_List.replace(sName, etName.getText().toString());
                        editor.putString("lehrer", sNew_List);
                        editor.commit();
                        Toast t = Toast.makeText(this, "Person gespeichert", Toast.LENGTH_SHORT);
                        t.show();
                    }

                    finish();
                } else {
                    Toast t = Toast.makeText(this, "Gib einen Namen ein", Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
            case R.id.bEditCancel:
                finish();
                break;
        }
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
