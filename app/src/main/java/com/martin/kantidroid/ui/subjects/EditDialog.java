package com.martin.kantidroid.ui.subjects;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.internal.widget.AppCompatPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;

public class EditDialog extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText mName, mShort;
    private CheckBox mCounts;
    private Button mAdd;
    private View mColor;
    private SwitchCompat mSwitch;
    private RadioGroup mKontSelection;
    private boolean newSubject = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subjects_edit_dialog);

        mName = (EditText) findViewById(R.id.et_subj_name);
        mShort = (EditText) findViewById(R.id.et_subj_short);
        mCounts = (CheckBox) findViewById(R.id.cbCounts);
        mSwitch = (SwitchCompat) findViewById(R.id.scCounts);
        mSwitch.setOnCheckedChangeListener(this);
        mKontSelection = (RadioGroup) findViewById(R.id.rgKont);
        mAdd = (Button) findViewById(R.id.bAdd);
        mAdd.setOnClickListener(this);
        mColor = findViewById(R.id.vColor);
        mColor.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra("name")) {
            mName.setText(data.getStringExtra("name"));
            mShort.setText(data.getStringExtra("short"));
            boolean counts = false;
            if (data.getStringExtra("counts").contentEquals("true")) {
                counts = true;
            }
            mCounts.setChecked(counts);
            mColor.setBackgroundColor(Color.parseColor(data.getStringExtra("color")));
            mColor.setTag(data.getStringExtra("color"));
            selectKont(data.getStringExtra("kontAv"));
            mAdd.setText(R.string.save);
        }
        else {
            newSubject = true;
        }

        mName.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bAdd:
                String sName = mName.getText().toString();
                String sShort = mShort.getText().toString();
                if (sName.length() > 0 && sShort.length() > 0) {
                    if (sShort.length() <= 5) {
                        String counts = "false";
                        if (mCounts.isChecked()) {
                            counts = "true";
                        }
                        Intent data = new Intent();
                        data.putExtra("name", sName);
                        data.putExtra("short", sShort);
                        data.putExtra("color", mColor.getTag().toString());
                        data.putExtra("counts", counts);
                        data.putExtra("kontAv", getKontAv());
                        if (newSubject) {
                            setResult(1, data);
                        }
                        else {
                            setResult(2, data);
                        }
                        finish();
                    }
                    else {
                        Toast.makeText(this, R.string.too_long, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.vColor:
                Intent i = new Intent(this, ColorPickerDialog.class);
                startActivityForResult(i, 2);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 1) {
            mColor.setTag(data.getStringExtra("color"));
            mColor.setBackgroundColor(Color.parseColor(data.getStringExtra("color")));
        }
    }

    private String getKontAv() {
        String kontAv = "";
        if (mKontSelection.getChildAt(0).isEnabled()) {
            switch (mKontSelection.getCheckedRadioButtonId()) {
                case R.id.rb2:
                    kontAv = "2";
                    break;
                case R.id.rb4:
                    kontAv = "4";
                    break;
                case R.id.rb6:
                    kontAv = "6";
                    break;
                case R.id.rb8:
                    kontAv = "8";
                    break;
                case R.id.rb10:
                    kontAv = "10";
                    break;
                case R.id.rb12:
                    kontAv = "12";
                    break;
            }
        }
        return kontAv;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        boolean newStatus = !mKontSelection.getChildAt(0).isEnabled();
        for (int i = 0; i < mKontSelection.getChildCount(); i++) {
            mKontSelection.getChildAt(i).setEnabled(newStatus);
        }
    }

    private void selectKont(String kontAv) {
        if (kontAv.contentEquals("")) {
            kontAv = "0";
        }
        int kont = Integer.parseInt(kontAv);
        if (kont == 0) {
            mSwitch.setChecked(false);
        }
        else {
            int[] kontList = new int[] {2, 4, 6, 8, 10, 12};
            int[] ids = new int[] {
                    R.id.rb2,
                    R.id.rb4,
                    R.id.rb6,
                    R.id.rb8,
                    R.id.rb10,
                    R.id.rb12
            };
            int id = R.id.rb4;
            for (int i = 0; i < kontList.length; i++) {
                if (kont == kontList[i]) {
                    id = ids[i];
                }
            }
            mKontSelection.check(id);
        }
    }
}