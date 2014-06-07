package com.martin.kantidroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ChangelogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Changelog");
		builder.setNeutralButton("Schliessen", null);
		
		WebView wv = new WebView(getActivity());
		wv.setWebViewClient(new WebViewClient());
		wv.loadUrl("file:///android_asset/changelog.html");
        
		builder.setView(wv);
		return builder.create();
	}


}
