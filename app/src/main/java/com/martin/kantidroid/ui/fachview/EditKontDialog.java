package com.martin.kantidroid.ui.fachview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.DatabaseHandler;
import com.martin.kantidroid.logic.Fach;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditKontDialog extends AppCompatActivity {

    private boolean newItem = false;
    private int mId, mSemester;
    private String mEntry;
    private EditText mDate;
    private RadioGroup mAmount;
    private TextView mSemesterInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_edit_kont_dialog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_close);
        ab.setDisplayHomeAsUpEnabled(true);

        mAmount = (RadioGroup) findViewById(R.id.rgKont);
        mDate = (EditText) findViewById(R.id.etDate);
        mSemesterInfo = (TextView) findViewById(R.id.tvSem);
        mAmount = (RadioGroup) findViewById(R.id.rgKont);

        Intent i = getIntent();
        mId = i.getIntExtra("id", -1);
        mSemester = i.getIntExtra("semester", -1);

        if (i.hasExtra("entry")) {
            mEntry = i.getStringExtra("entry");
            ab.setTitle(getString(R.string.edit_kont));
            String[] single = mEntry.split(" - ");
            mDate.setText(single[0]);
            ((RadioButton) mAmount.getChildAt(Integer.parseInt(single[1]) - 1)).setChecked(true);
        } else {
            newItem = true;
            ab.setTitle(getString(R.string.new_kont));
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            mDate.setText(df.format(c.getTime()));
        }
        mSemesterInfo.setText(mSemester + getString(R.string.n_semester));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:
                if (saveToSave()) {
                    DatabaseHandler db = new DatabaseHandler(this);
                    Fach fach = db.getFach(mId);
                    if (!newItem) {
                        fach.removeKont(mSemester, mEntry);
                    }
                    fach.addKont(mSemester, mDate.getText().toString() + " - " + (mAmount.indexOfChild(findViewById(mAmount.getCheckedRadioButtonId())) + 1));
                    db.updateFach(fach);
                    setResult(1);
                    finish();
                } else {
                    Toast.makeText(this, R.string.input_sucks, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_delete:
                if (!newItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.delete_question_kont);
                    builder.setNegativeButton(R.string.no, null);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DatabaseHandler db = new DatabaseHandler(EditKontDialog.this);
                            Fach fach = db.getFach(mId);
                            fach.removeKont(mSemester, mEntry);
                            db.updateFach(fach);
                            setResult(1);
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
        getMenuInflater().inflate(R.menu.fachview_edit_kont_menu, menu);
        return true;
    }

    private boolean saveToSave() {
        String date = mDate.getText().toString();
        if (date.contentEquals("")) {
            return false;
        }
        if (date.contentEquals("0")) {
            return false;
        }
        return true;
    }
}