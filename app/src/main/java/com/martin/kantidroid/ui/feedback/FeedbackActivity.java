package com.martin.kantidroid.ui.feedback;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.backup.BackupAdapter;
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.util.LinearLayoutManager;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Glide.with(this).load(R.drawable.feedback).into((ImageView) findViewById(R.id.ivFeedback));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.feedback_statusbar));
        }

        RecyclerView mSelection = (RecyclerView) findViewById(R.id.rvFeedback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSelection.setLayoutManager(layoutManager);
        mSelection.setHasFixedSize(true);
        mSelection.addItemDecoration(new DividerItemDecoration(this, null, false));
        FeedbackAdapter adapter = new FeedbackAdapter(this, new FeedbackAdapter.OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {

                } else {

                }
            }
        });
        mSelection.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}
