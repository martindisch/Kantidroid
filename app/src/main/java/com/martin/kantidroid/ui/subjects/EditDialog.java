package com.martin.kantidroid.ui.subjects;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;

public class EditDialog extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private EditText mName, mShort;
    private CheckBox mCounts;
    private View mColor;
    private SwitchCompat mSwitch;
    private RadioGroup mKontSelection;
    private boolean newSubject = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subjects_edit_dialog);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        mName = (EditText) findViewById(R.id.et_subj_name);
        mShort = (EditText) findViewById(R.id.et_subj_short);
        mCounts = (CheckBox) findViewById(R.id.cbCounts);
        mSwitch = (SwitchCompat) findViewById(R.id.scCounts);
        mSwitch.setOnCheckedChangeListener(this);
        mKontSelection = (RadioGroup) findViewById(R.id.rgKont);
        mColor = findViewById(R.id.vColor);
        mColor.setOnClickListener(this);

        Intent data = getIntent();
        if (data.hasExtra("name")) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            mName.setText(data.getStringExtra("name"));
            mShort.setText(data.getStringExtra("short"));
            boolean counts = false;
            if (data.getStringExtra("counts").contentEquals("true")) {
                counts = true;
            }
            mCounts.setChecked(counts);
            mColor.setBackgroundColor(Util.getNormal(this, data.getStringExtra("color")));
            mColor.setTag(data.getStringExtra("color"));
            selectKont(data.getStringExtra("kontAv"));
        } else {
            newSubject = true;
            setTitle(R.string.subject_add);
        }

        if (savedInstanceState != null) {
            mColor.setBackgroundColor(Util.getNormal(this, savedInstanceState.getString("color")));
            mColor.setTag(savedInstanceState.getString("color"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
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
            mColor.setBackgroundColor(Util.getNormal(this, data.getStringExtra("color")));
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
        } else {
            int[] kontList = new int[]{2, 4, 6, 8, 10};
            int[] ids = new int[]{
                    R.id.rb2,
                    R.id.rb4,
                    R.id.rb6,
                    R.id.rb8,
                    R.id.rb10
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("color", mColor.getTag().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
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
                        } else {
                            setResult(2, data);
                        }
                        finish();
                    } else {
                        Toast.makeText(this, R.string.too_long, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.enter_name, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_delete:
                if (!newSubject) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.delete_question);
                    builder.setNegativeButton(R.string.no, null);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setResult(3);
                            finish();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(this, R.string.no_delete, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subjects_edit_menu, menu);
        return true;
    }
}
