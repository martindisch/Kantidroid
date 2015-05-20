package com.martin.kontingent;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.martin.kantidroid.WidgetProvider;

public class RemoveUsage extends ListActivity implements android.content.DialogInterface.OnClickListener {

    private String dates;
    private String removable;
    private String[] entries;
    private String[] current;
    private int id;
    private int kont_us;

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
        getData();
        prepareList();
        createList();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getData() {
        Bundle received = getIntent().getExtras();
        DatabaseHandler db = new DatabaseHandler(this);

        id = received.getInt("id");
        Fach fach = db.getFach(id);

        String name = fach.getName();
        dates = fach.getDates();
        String sKont_us = fach.getKont_us();
        kont_us = Integer.parseInt(sKont_us);
    }

    private void prepareList() {
        entries = dates.split("\n");
    }

    private void createList() {
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, entries));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        removable = entries[position] + "\n";
        current = entries[position].split(" - ");

        AlertDialog.Builder dg = new AlertDialog.Builder(this);
        dg.setTitle("Nutzung rückgängig");
        dg.setMessage("Willst du die " + current[1] + " vom " + current[0] + " wirklich rückgängig machen?");
        dg.setPositiveButton("Ja", this);
        dg.setNegativeButton("Nein", null);
        dg.show();
    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {
        int newKont = getNewKont();
        String newDates = dates.replace(removable, "");

        DatabaseHandler db = new DatabaseHandler(this);
        Fach updated = db.getFach(id);
        if (newKont == 0) {
            updated.setDates("empty");
        } else {
            updated.setDates(newDates);
        }
        updated.setKont_us(Integer.toString(newKont));
        db.updateFach(updated);

        Toast t = Toast.makeText(RemoveUsage.this, current[1] + " abgezogen", Toast.LENGTH_SHORT);
        t.show();

        finish();
    }

    private int getNewKont() {
        String[] details = current[1].split(" ");
        int kont_rev = Integer.parseInt(details[0]);
        return kont_us - kont_rev;
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
