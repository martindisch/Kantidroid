package com.martin.noten;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.martin.kantidroid.R;

import java.util.List;

public class AddSelect extends ListActivity {

    private Fach selected = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createList();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void createList() {
        DatabaseHandler db = new DatabaseHandler(this);
        int count = db.getFachCount();
        SharedPreferences spNoten = this.getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
        List<Fach> faecher = db.getAllFaecher(getApplicationContext(), spNoten.getInt("selected_semester", 1));
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            Fach entry = faecher.get(i);
            names[i] = entry.getName();
        }
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        DatabaseHandler db = new DatabaseHandler(this);
        SharedPreferences spNoten = this.getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);

        List<Fach> faecher = db.getAllFaecher(getApplicationContext(), spNoten.getInt("selected_semester", 1));

        selected = db.getFach(faecher.get(position).getID());
        int predictedsem = spNoten.getInt("selected_semester", 1);
        if (predictedsem == 3) {
            predictedsem = 1;
        }

        AlertDialog.Builder inp = new AlertDialog.Builder(this);
        inp.setTitle("Semester");
        LayoutInflater inflator = this.getLayoutInflater();
        LinearLayout ll = (LinearLayout) inflator.inflate(R.layout.semester_choice, null);
        final Spinner sem = (Spinner) ll.findViewById(R.id.spSemester);
        sem.setSelection(predictedsem - 1);
        inp.setView(ll);
        inp.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle data = new Bundle();
                data.putInt("id", selected.getID());
                data.putInt("semester", sem.getSelectedItemPosition() + 1);
                Intent i = new Intent(AddSelect.this, AddMark.class);
                i.putExtras(data);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        inp.setNegativeButton("Abbrechen", null);
        inp.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
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