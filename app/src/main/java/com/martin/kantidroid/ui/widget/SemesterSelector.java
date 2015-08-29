package com.martin.kantidroid.ui.widget;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;

public class SemesterSelector extends AppCompatActivity implements View.OnClickListener {

    private RadioGroup mRadios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_semesterselector);

        mRadios = (RadioGroup) findViewById(R.id.widget_radios);
        Button bSave = (Button) findViewById(R.id.widget_save);
        bSave.setOnClickListener(this);

        SharedPreferences prefs = getSharedPreferences("Kantidroid", MODE_PRIVATE);
        if (prefs.getInt("widget_semester", 1) == 1) {
            mRadios.check(R.id.widget_radio1);
        } else {
            mRadios.check(R.id.widget_radio2);
        }
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
        Util.updateWidget(this);
        finish();
    }
}
