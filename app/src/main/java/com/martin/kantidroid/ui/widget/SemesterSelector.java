package com.martin.kantidroid.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RemoteViews;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;

public class SemesterSelector extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup mRadios;
    private int mAppWidgetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_semesterselector);

        mRadios = (RadioGroup) findViewById(R.id.widget_radios);
        Button bSave = (Button) findViewById(R.id.widget_save);
        bSave.setOnClickListener(this);

        setResult(RESULT_CANCELED);
    }

    @Override
    public void onClick(View view) {
        SharedPreferences prefs = getSharedPreferences("Kantidroid", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int radioButtonID = mRadios.getCheckedRadioButtonId();
        View radioButton = mRadios.findViewById(radioButtonID);
        int idx = mRadios.indexOfChild(radioButton);
        editor.putInt("widget_semester", idx + 1);
        editor.commit();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Util.updateWidget(this);
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
        else {
            Util.updateWidget(this);
            finish();
        }
    }
}
