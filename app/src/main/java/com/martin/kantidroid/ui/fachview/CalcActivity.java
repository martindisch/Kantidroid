package com.martin.kantidroid.ui.fachview;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.util.LinearLayoutManager;

public class CalcActivity extends AppCompatActivity {

    private Spinner mWeight;
    private RecyclerView mMarks;
    private int mSemester, mId;
    private CalcAdapter mAdapter;
    private String[] mWeights = { "1", "0.5", "2" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_calc_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mWeight = (Spinner) findViewById(R.id.sRelevance);
        mMarks = (RecyclerView) findViewById(R.id.rvMarks);
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
