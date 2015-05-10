package com.martin.noten;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;

import com.martin.kantidroid.R;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog;
import org.holoeverywhere.widget.datetimepicker.date.DatePickerDialog.OnDateSetListener;

public class EditMark extends Activity {

    private int mId, mSemester, mPosition;
    private EditText mMark, mRelevance;
    private ArrayAdapter<String> mAdapter;
    private String[] mEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_mark);
        getData();
        initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        mId = received.getInt("id");
        mSemester = received.getInt("semester");
        mPosition = received.getInt("position");
    }

    private void initialize() {
        Button mSave = (Button) findViewById(R.id.bAddSave);
        Button mCancel = (Button) findViewById(R.id.bAddCancel);
        mMark = (EditText) findViewById(R.id.etMark);
        mRelevance = (EditText) findViewById(R.id.etAndere);
        Spinner mDate = (Spinner) findViewById(R.id.sDatumMark);

        mSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                save();
            }
        });
        mCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DatabaseHandler db = new DatabaseHandler(this);
        Fach fach = db.getFach(mId);
        String entries_raw = null;
        if (mSemester == 1) {
            entries_raw = fach.getNoten1();
        } else {
            entries_raw = fach.getNoten2();
        }
        mEntries = entries_raw.split("\n");
        String mark = mEntries[mPosition].split(" - ")[0];
        String relevance = mEntries[mPosition].split(" - ")[1];
        String date = mEntries[mPosition].split(" - ")[2];
        final String fDate = date;

        mMark.setText(mark);
        mRelevance.setText(relevance);
        mAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        mAdapter.add(date);
        mDate.setAdapter(mAdapter);
        mDate.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setDate(fDate);
                }
                return true;
            }
        });
    }

    private void setDate(String date) {
        DatePickerDialog dg = new DatePickerDialog();
        int year = Integer.parseInt(date.split("\\.")[2]);
        int month = Integer.parseInt(date.split("\\.")[1]) - 1;
        int day = Integer.parseInt(date.split("\\.")[0]);
        dg.setDate(year, month, day);
        dg.setOnDateSetListener(new OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                String selectedDate = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                mAdapter.clear();
                mAdapter.add(selectedDate);
            }
        });
        dg.show(this);
    }

    private void save() {
        String mark = mMark.getText().toString();
        String relevance = mRelevance.getText().toString();
        String date = mAdapter.getItem(0);

        if (!(mark.contentEquals("") || mark.contentEquals("0") || relevance.contentEquals("0"))) {
            double dMark = Double.parseDouble(mark);

            if (dMark >= 1 && dMark <= 6.3) {
                DatabaseHandler db = new DatabaseHandler(this);
                Fach fach = db.getFach(mId);

                mEntries[mPosition] = mark + " - " + relevance + " - " + date;

                String entries_raw = "";
                for (int i = 0; i < mEntries.length - 1; i++) {
                    entries_raw += mEntries[i] + "\n";
                }
                entries_raw += mEntries[mEntries.length - 1];

                if (mSemester == 1) {
                    fach.setNoten1(entries_raw);
                    db.updateFach(fach);
                } else {
                    fach.setNoten2(entries_raw);
                    db.updateFach(fach);
                }

                finish();
            } else {
                Toast t = Toast.makeText(this, "Ungültige Note", Toast.LENGTH_SHORT);
                t.show();
            }
        } else {
            Toast t = Toast.makeText(this, "Leeres Feld", Toast.LENGTH_SHORT);
            t.show();
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
