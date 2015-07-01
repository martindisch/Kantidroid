package com.martin.kantidroid.ui.fachview;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.util.LinearLayoutManager;

public class CalcActivity extends AppCompatActivity {

    private Spinner mWeight, mWeight2;
    private RecyclerView mMarks;
    private int mSemester, mId;
    private CalcAdapter mAdapter;
    private EditText mGoal;
    private TextView mRequired;
    private Button mCalculate;
    private String[] mWeights = {"1", "0.5", "2"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_calc_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mGoal = (EditText) findViewById(R.id.etGoal);
        mWeight2 = (Spinner) findViewById(R.id.sRelevance2);
        mRequired = (TextView) findViewById(R.id.tvGoalMark);
        mWeight = (Spinner) findViewById(R.id.sRelevance);
        mMarks = (RecyclerView) findViewById(R.id.rvMarks);
        mCalculate = (Button) findViewById(R.id.bCalculate);
        mCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mGoal.getText().toString().contentEquals("")) {
                    double goal = Double.parseDouble(mGoal.getText().toString());
                    if (goal <= 6 && goal >= 1) {
                        double required = Util.getRequired(CalcActivity.this, mId, mSemester, mWeights[mWeight2.getSelectedItemPosition()], mGoal.getText().toString());
                        mRequired.setText(required + "");
                        if (required > 6) {
                            mRequired.setTextColor(getResources().getColor(R.color.red_dark));
                        } else {
                            mRequired.setTextColor(getResources().getColor(R.color.primary_text_default_material_light));
                        }
                    } else {
                        Toast.makeText(CalcActivity.this, R.string.impossible, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CalcActivity.this, R.string.input_sucks, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mWeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mAdapter.changeWeight(mWeights[i]);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMarks.setLayoutManager(layoutManager);
        mMarks.setHasFixedSize(true);
        mMarks.addItemDecoration(new DividerItemDecoration(this, null, false));

        mSemester = getIntent().getIntExtra("semester", -1);
        mId = getIntent().getIntExtra("id", -1);

        mAdapter = new CalcAdapter(this, new DatabaseHandler(this).getFach(mId), mSemester);

        mMarks.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
