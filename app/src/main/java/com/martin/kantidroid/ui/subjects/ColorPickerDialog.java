package com.martin.kantidroid.ui.subjects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.martin.kantidroid.R;

// This is the worst thing I've done in a long time. Please don't tell anyone.
public class ColorPickerDialog extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subjects_color_picker);
        View c1 = findViewById(R.id.c1);
        View c2 = findViewById(R.id.c2);
        View c3 = findViewById(R.id.c3);
        View c4 = findViewById(R.id.c4);
        View c5 = findViewById(R.id.c5);
        View c6 = findViewById(R.id.c6);
        View c7 = findViewById(R.id.c7);
        View c8 = findViewById(R.id.c8);
        View c9 = findViewById(R.id.c9);
        View c10 = findViewById(R.id.c10);
        View c11 = findViewById(R.id.c11);
        View c12 = findViewById(R.id.c12);
        View c13 = findViewById(R.id.c13);
        View c14 = findViewById(R.id.c14);
        View c15 = findViewById(R.id.c15);
        View c16 = findViewById(R.id.c16);

        c1.setOnClickListener(this);
        c2.setOnClickListener(this);
        c3.setOnClickListener(this);
        c4.setOnClickListener(this);
        c5.setOnClickListener(this);
        c6.setOnClickListener(this);
        c7.setOnClickListener(this);
        c8.setOnClickListener(this);
        c9.setOnClickListener(this);
        c10.setOnClickListener(this);
        c11.setOnClickListener(this);
        c12.setOnClickListener(this);
        c13.setOnClickListener(this);
        c14.setOnClickListener(this);
        c15.setOnClickListener(this);
        c16.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent();
        i.putExtra("color", view.getTag().toString());
        setResult(1, i);
        finish();
    }
}
