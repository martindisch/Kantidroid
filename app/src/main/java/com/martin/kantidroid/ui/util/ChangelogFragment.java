package com.martin.kantidroid.ui.util;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.martin.kantidroid.R;

public class ChangelogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView tv = new TextView(getActivity());
        tv.setText(R.string.changelog);
        tv.setTextSize(20);
        tv.setTextColor(Color.BLACK);
        tv.setPadding(30, 30, 30, 30);
        builder.setCustomTitle(tv);
        builder.setNeutralButton(R.string.close, null);

        WebView wv = new WebView(getActivity());
        wv.setWebViewClient(new WebViewClient());
        wv.loadUrl("file:///android_asset/changelog.html");

        builder.setView(wv);
        return builder.create();
    }

}
