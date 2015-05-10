package com.martin.noten;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.martin.kantidroid.R;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Guessing extends Activity implements OnClickListener {

    private Button bCalc;
    private Button bCancel;
    private Button bOwnRelevance;
    private Spinner sRelevance;
    private EditText etMark;
    private TextView tvResult;
    private TextView tvMessage;
    private int id;
    private boolean OwnRelevance = false;
    private double dOwnRelevance;
    private int iSemester;
    private Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guessing);
        getData();
        initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        id = received.getInt("id");
        iSemester = received.getInt("semester");
    }

    private void initialize() {
        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        bCalc = (Button) findViewById(R.id.bGuessingCalculate);
        bCancel = (Button) findViewById(R.id.bGuessingCancel);
        bOwnRelevance = (Button) findViewById(R.id.bGuessingOwnRelevance);
        sRelevance = (Spinner) findViewById(R.id.sGuessingRelevance);
        etMark = (EditText) findViewById(R.id.etGuessingMark);
        tvResult = (TextView) findViewById(R.id.tvGuessingResult);
        tvResult.setTypeface(tf);
        tvMessage = (TextView) findViewById(R.id.tvGuessingNecessary);
        bCalc.setOnClickListener(this);
        bCancel.setOnClickListener(this);
        bOwnRelevance.setOnClickListener(this);

        DatabaseHandler db = new DatabaseHandler(this);
        Fach fach = db.getFach(id);
        setTitle("Notenvorhersage für " + fach.getName());
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.bGuessingCalculate:
                if (!etMark.getText().toString().contentEquals("")) {
                    double sMark = Double.parseDouble(etMark.getText().toString());

                    if (sMark >= 1 && sMark <= 6.3) {
                        DatabaseHandler db = new DatabaseHandler(this);
                        Fach fach = db.getFach(id);

                        String sSelectedRelevance = "";
                        if (OwnRelevance) {
                            sSelectedRelevance = String.valueOf(dOwnRelevance);
                        } else {
                            switch (sRelevance.getSelectedItemPosition()) {
                                case 0:
                                    sSelectedRelevance = "1.0";
                                    break;
                                case 1:
                                    sSelectedRelevance = "0.5";
                                    break;
                                case 2:
                                    sSelectedRelevance = "2.0";
                                    break;
                            }
                        }

                        double upper_term;
                        double dRelevance = Double.parseDouble(sSelectedRelevance);
                        double dGoal = Double.parseDouble(etMark.getText().toString());

                        if (iSemester == 1) {
                            String sMarks = fach.getNoten1();

                            String[] entries = sMarks.split("\n");
                            int count = entries.length;
                            double subtraktion = 0;
                            double multiplikatoren = 0;

                            for (int i = 0; i < count; i++) {
                                String[] item = entries[i].split(" - ");
                                subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
                                multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
                            }
                            upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;
                        } else {
                            String sMarks = fach.getNoten2();

                            String[] entries = sMarks.split("\n");
                            int count = entries.length;
                            double subtraktion = 0;
                            double multiplikatoren = 0;

                            for (int i = 0; i < count; i++) {
                                String[] item = entries[i].split(" - ");
                                subtraktion = subtraktion + (Double.parseDouble(item[0].replace(",", ".")) * Double.parseDouble(item[1].replace(",", ".")));
                                multiplikatoren = multiplikatoren + Double.parseDouble(item[1].replace(",", "."));
                            }
                            upper_term = dGoal * (multiplikatoren + dRelevance) - subtraktion;
                        }

                        double needed = upper_term / dRelevance;

                        BigDecimal bd = new BigDecimal(needed);

                        tvResult.setText(bd.setScale(2, RoundingMode.HALF_UP).toString());
                        tvMessage.setVisibility(View.VISIBLE);
                        tvResult.setVisibility(View.VISIBLE);

                        // Hide the keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View view = getCurrentFocus();
                        if (view == null) {
                            view = new View(this);
                        }
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    } else {
                        Toast t = Toast.makeText(Guessing.this, "Ungültige Note", Toast.LENGTH_SHORT);
                        t.show();
                    }
                } else {
                    Toast t = Toast.makeText(this, "Leeres Feld", Toast.LENGTH_SHORT);
                    t.show();
                }

                break;
            case R.id.bGuessingCancel:
                finish();
                break;
            case R.id.bGuessingOwnRelevance:
                AlertDialog.Builder inp = new AlertDialog.Builder(this);
                inp.setTitle("Relevanz");
                inp.setMessage("Gib an, wie viel diese Note zählen soll (1, 0.5, 0.2 etc.)");
                final EditText rel = new EditText(this);
                rel.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                inp.setView(rel);
                inp.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!rel.getText().toString().contentEquals("")) {
                            dOwnRelevance = Double.parseDouble(rel.getText().toString());
                            sRelevance.setEnabled(false);
                            OwnRelevance = true;
                        } else {
                            Toast t = Toast.makeText(Guessing.this, "Gib einen Wert ein", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }
                });
                inp.setNegativeButton("Abbrechen", null);
                inp.show();
                break;
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
