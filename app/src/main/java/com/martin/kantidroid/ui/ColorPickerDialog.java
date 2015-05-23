package com.martin.kantidroid.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.martin.kantidroid.R;

// This is the worst thing I've done in a long time. Please don't tell anyone.
public class ColorPickerDialog extends Activity implements View.OnClickListener {

    private View c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.color_picker);
        c1 = findViewById(R.id.c1);
        c2 = findViewById(R.id.c2);
        c3 = findViewById(R.id.c3);
        c4 = findViewById(R.id.c4);
        c5 = findViewById(R.id.c5);
        c6 = findViewById(R.id.c6);
        c7 = findViewById(R.id.c7);
        c8 = findViewById(R.id.c8);
        c9 = findViewById(R.id.c9);
        c10 = findViewById(R.id.c10);
        c11 = findViewById(R.id.c11);
        c12 = findViewById(R.id.c12);
        c13 = findViewById(R.id.c13);
        c14 = findViewById(R.id.c14);
        c15 = findViewById(R.id.c15);
        c16 = findViewById(R.id.c16);

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
        String[] colors = getResources().getStringArray(R.array.colors);
        String color = colors[0];
        switch (view.getId()) {
            case R.id.c1:
                color = colors[0];
                break;
            case R.id.c2:
                color = colors[1];
                break;
            case R.id.c3:
                color = colors[2];
                break;
            case R.id.c4:
                color = colors[3];
                break;
            case R.id.c5:
                color = colors[4];
                break;
            case R.id.c6:
                color = colors[5];
                break;
            case R.id.c7:
                color = colors[6];
                break;
            case R.id.c8:
                color = colors[7];
                break;
            case R.id.c9:
                color = colors[8];
                break;
            case R.id.c10:
                color = colors[9];
                break;
            case R.id.c11:
                color = colors[00];
                break;
            case R.id.c12:
                color = colors[11];
                break;
            case R.id.c13:
                color = colors[12];
                break;
            case R.id.c14:
                color = colors[13];
                break;
            case R.id.c15:
                color = colors[14];
                break;
            case R.id.c16:
                color = colors[15];
                break;
        }

        Intent i = new Intent();
        i.putExtra("color", color);
        setResult(1, i);
        finish();
    }
}
