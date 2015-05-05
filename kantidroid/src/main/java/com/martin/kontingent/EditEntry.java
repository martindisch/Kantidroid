package com.martin.kontingent;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

public class EditEntry extends Activity implements OnClickListener {

    EditText etName, etKontingent;
    Button bSave, bCancel;
    int id;
    String fname, fkont;
    Fach fach;

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
        setContentView(R.layout.edit_entry);
        getData();
        initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        DatabaseHandler db = new DatabaseHandler(this);

        id = received.getInt("id");
        fach = db.getFach(id);

        fname = fach.getName();
        fkont = fach.getKont_av();
    }

    private void initialize() {
        etName = (EditText) findViewById(R.id.etEditFach);
        etKontingent = (EditText) findViewById(R.id.etEditKontingent);
        bSave = (Button) findViewById(R.id.bEditSave);
        bCancel = (Button) findViewById(R.id.bEditCancel);
        bSave.setOnClickListener(this);
        bCancel.setOnClickListener(this);
        updateText();
    }

    private void updateText() {
        etName.setText(fname);
        etKontingent.setText(fkont);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bEditSave:
                String sFach = etName.getText().toString();
                String sKont_av = etKontingent.getText().toString();

                if (!sFach.contentEquals("") && !sKont_av.contentEquals("")) {

                    DatabaseHandler db = new DatabaseHandler(this);

                    fach.setName(sFach);
                    fach.setKont_av(sKont_av);

                    db.updateFach(fach);

                    finish();
                } else {
                    Toast t = Toast.makeText(EditEntry.this, "Fülle beide Felder aus", Toast.LENGTH_SHORT);
                    t.show();
                }
                break;
            case R.id.bEditCancel:
                finish();
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
