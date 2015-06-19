package com.martin.kantidroid.ui.fachview;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.martin.kantidroid.R;

public class EditMarkDialog extends AppCompatActivity {

    private boolean newItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fachview_edit_mark_dialog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_close);
        ab.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO: Also load from InstanceState
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_save:

                break;
            case R.id.action_delete:
                if (!newItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.delete_question);
                    builder.setNegativeButton(R.string.no, null);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setResult(3);
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
        getMenuInflater().inflate(R.menu.fachview_edit_mark_menu, menu);
        return true;
    }
}