package com.martin.kantidroid.ui.feedback;

import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.martin.kantidroid.R;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

public class FeedbackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        int measuredWidth = 0;
        int measuredHeight = 0;
        WindowManager w = getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
            measuredHeight = size.y;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
            measuredHeight = d.getHeight();
        }

        int calcHeight = (int) Math.round(measuredWidth * 0.7072916666);
        ImageView ivFeedback = (ImageView) findViewById(R.id.ivFeedback);
        ivFeedback.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, calcHeight));

        Glide.with(this).load(R.drawable.feedback).into(ivFeedback);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.feedback_statusbar));
        }

        RecyclerView mSelection = (RecyclerView) findViewById(R.id.rvFeedback);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mSelection.setLayoutManager(layoutManager);
        mSelection.setHasFixedSize(true);
        mSelection.addItemDecoration(new DividerItemDecoration(this, null, true));
        FeedbackAdapter adapter = new FeedbackAdapter(this, new FeedbackAdapter.OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                String subject;
                if (position == 0) {
                    subject = getString(R.string.feedback_subject_bug);
                } else {
                    subject = getString(R.string.feedback_subject_feature);
                }

                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("martindisch@gmail.com") + "?subject=" + Uri.encode(subject);
                Uri uri = Uri.parse(uriText);
                send.setData(uri);
                startActivity(Intent.createChooser(send, "E-Mail senden"));
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
