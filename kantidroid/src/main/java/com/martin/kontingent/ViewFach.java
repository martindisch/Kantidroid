package com.martin.kontingent;

import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

import java.util.Calendar;

public class ViewFach extends AppCompatActivity implements OnClickListener, android.content.DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener {

    private TextView tvTitle;
    private TextView tvUsage;
    private ListView lvUsage;
    private NumberPicker picker;
    private int id;
    private int kont_us;
    private int picked_number;
    private boolean invoke;
    private String fname;
    private String fkont;
    private String date_new;
    private String date_old;
    private String addition;

    @Override
    protected void onStop() {
        super.onStop();
        Intent rIntent = new Intent(this, WidgetProvider.class);
        rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(rIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewfach);
        getData();
        initialize();
        Check check = new Check();
        if (!check.getSeen(getClass().getName(), this)) {
            AlertDialog.Builder dg = new AlertDialog.Builder(this);
            dg.setTitle("Info");
            dg.setNeutralButton("Schliessen", null);
            dg.setMessage(R.string.kont_viewfach);
            dg.show();
            check.setSeen(getClass().getName(), this);
        }
        // TODO: Check if support is necessary and replace everywhere
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        DatabaseHandler db = new DatabaseHandler(this);

        id = received.getInt("id");
        invoke = received.getBoolean("invoke");
        Fach fach = db.getFach(id);

        fname = fach.getName();
        fkont = fach.getKont_us() + "/" + fach.getKont_av();
        date_old = fach.getDates();

        kont_us = Integer.parseInt(fach.getKont_us());
    }

    private void initialize() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        tvTitle = (TextView) findViewById(R.id.tvFachTitle);
        tvTitle.setTypeface(tf);
        ImageButton ibAddKont = (ImageButton) findViewById(R.id.ibAddKont);
        ibAddKont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                useKont();
            }
        });
        tvUsage = (TextView) findViewById(R.id.tvViewUsage);
        lvUsage = (ListView) findViewById(R.id.lvViewUsage);

        updateText();
        if (invoke) {
            invokedUse();
        }
    }

    private void updateText() {
        tvTitle.setText(fname);
        tvUsage.setText(fkont);
        if (!date_old.contentEquals("empty")) {
            String[] entries = date_old.split("\n");
            String[] dates = new String[entries.length];
            String[] usages = new String[entries.length];
            for (int i = 0; i < entries.length; i++) {
                dates[i] = entries[i].split(" - ")[0];
                usages[i] = entries[i].split(" - ")[1];
            }

            ViewFachAdapter adapter = new ViewFachAdapter(this, dates, usages);
            lvUsage.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        picked_number = picker.getValue();
        Calendar c = Calendar.getInstance();
        DatePickerDialog dg = new DatePickerDialog(this, this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dg.show();
    }

    private void setKont() {
        if (date_old.contains(addition)) {
            Toast t = Toast.makeText(ViewFach.this, "Eintrag bereits vorhanden", Toast.LENGTH_SHORT);
            t.show();
        } else {
            DatabaseHandler db = new DatabaseHandler(this);
            Fach updated = db.getFach(id);
            int new_value = kont_us + picked_number;
            updated.setKont_us(Integer.toString(new_value));
            updated.setDates(date_new);
            db.updateFach(updated);
            getData();
            updateText();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getData();
        updateText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.menu_viewfach, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void invokedUse() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anzahl genommenes Kontingent");
        View view = LayoutInflater.from(this).inflate(R.layout.numberpicker, null);
        picker = (NumberPicker) view.findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(4);
        picker.setValue(1);
        builder.setView(view);
        builder.setNegativeButton("Abbrechen", null);
        builder.setPositiveButton("OK", this);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.iViewRemove:
                if (!(kont_us == 0)) {
                    Bundle data = new Bundle();
                    data.putString("name", fname);
                    data.putString("dates", date_old);
                    data.putInt("id", id);
                    Intent i = new Intent(ViewFach.this, RemoveUsage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtras(data);
                    startActivity(i);
                } else {
                    Toast t = Toast.makeText(this, "Kein Kontingent zu entfernen", Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View arg0) {
        // Rien

    }

    private void useKont() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anzahl genommenes Kontingent");
        View view = LayoutInflater.from(this).inflate(R.layout.numberpicker, null);
        picker = (NumberPicker) view.findViewById(R.id.numberPicker);
        picker.setMinValue(1);
        picker.setMaxValue(4);
        picker.setValue(1);
        builder.setView(view);
        builder.setNegativeButton("Abbrechen", null);
        builder.setPositiveButton("OK", this);
        builder.show();
    }

    // TODO: Check DatePicker correctness here and in Noten
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        String singularPlural = " Lektion";

        if (picked_number > 1) {
            singularPlural = " Lektionen";
        }
        addition = dayOfMonth + "." + (monthOfYear + 1) + "." + year + " - " + picked_number + singularPlural + "\n";

        if (date_old.contentEquals("empty")) {
            date_new = addition;
        } else {
            date_new = date_old + addition;
        }
        setKont();
    }
}
