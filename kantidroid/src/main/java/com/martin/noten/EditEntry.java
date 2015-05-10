package com.martin.noten;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Toast;

public class EditEntry extends Activity implements OnClickListener {

    private EditText etName;
    private String fname;
    private String fpromotionsrelevant;
    private Fach fach;
    private CheckBox cbPromotionsrelevant;

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
        setContentView(R.layout.edit_entry_noten);
        getData();
        initialize();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        DatabaseHandler db = new DatabaseHandler(this);

        int id = received.getInt("id");
        fach = db.getFach(id);

        fname = fach.getName();
        fpromotionsrelevant = fach.getPromotionsrelevant();
    }

    private void initialize() {
        etName = (EditText) findViewById(R.id.etEditFach);
        Button bSave = (Button) findViewById(R.id.bEditSave);
        Button bCancel = (Button) findViewById(R.id.bEditCancel);
        bSave.setOnClickListener(this);
        bCancel.setOnClickListener(this);
        cbPromotionsrelevant = (CheckBox) findViewById(R.id.cbEditPromotionsfach);

        updateText();
    }

    private void updateText() {
        etName.setText(fname);
        if (fpromotionsrelevant.contentEquals("false")) {
            cbPromotionsrelevant.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bEditSave:
                String sFach = etName.getText().toString();

                if (!sFach.contentEquals("")) {

                    DatabaseHandler db = new DatabaseHandler(this);

                    fach.setName(sFach);
                    if (cbPromotionsrelevant.isChecked()) {
                        fach.setPromotionsrelevant("true");
                    } else {
                        fach.setPromotionsrelevant("false");
                    }

                    db.updateFach(fach);

                    finish();
                } else {
                    Toast t = Toast.makeText(EditEntry.this, "Fülle das Feld aus", Toast.LENGTH_SHORT);
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