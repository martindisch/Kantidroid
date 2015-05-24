package com.martin.kantidroid.ui.subjects;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.martin.kantidroid.R;

public class EditDialog extends Activity implements View.OnClickListener {

    public RecyclerView mSubjects;
    private SubjectsAdapter mAdapter;
    private EditText mName, mShort;
    private CheckBox mCounts;
    private Button mAdd;
    private View mColor;
    private SwitchCompat mSwitch;
    private RadioGroup mKontSelection;
    private int mEditingIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subjects_edit_dialog);

        mName = (EditText) findViewById(R.id.et_subj_name);
        mShort = (EditText) findViewById(R.id.et_subj_short);
        mCounts = (CheckBox) findViewById(R.id.cbCounts);
        mSwitch = (SwitchCompat) findViewById(R.id.scCounts);
        mSwitch.setOnClickListener(this);
        mKontSelection = (RadioGroup) findViewById(R.id.rgKont);
        mAdd = (Button) findViewById(R.id.bAdd);
        mAdd.setOnClickListener(this);
        mColor = findViewById(R.id.vColor);
        mColor.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bAdd:

                break;
            case R.id.vColor:

                break;
            case R.id.scCounts:
                mKontSelection.setEnabled(!mKontSelection.isEnabled());
                break;
        }
    }
}
