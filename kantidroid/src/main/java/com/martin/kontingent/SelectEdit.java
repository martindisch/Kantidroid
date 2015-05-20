package com.martin.kontingent;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class SelectEdit extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createList();
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void createList() {
        DatabaseHandler db = new DatabaseHandler(this);
        int count = db.getFachCount();
        List<Fach> faecher = db.getAllFaecher(getApplicationContext());
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
        List<Fach> faecher = db.getAllFaecher(getApplicationContext());

        Fach selected = db.getFach(faecher.get(position).getID());

        Bundle data = new Bundle();
        data.putInt("id", selected.getID());
        Intent i = new Intent(SelectEdit.this, EditEntry.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtras(data);
        startActivity(i);
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
