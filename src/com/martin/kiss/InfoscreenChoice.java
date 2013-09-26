package com.martin.kiss;

import org.holoeverywhere.ArrayAdapter;
import org.holoeverywhere.app.ListActivity;
import org.holoeverywhere.widget.ListView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.martin.kantidroid.R;

public class InfoscreenChoice extends ListActivity {

	@Override
	protected void onCreate(Bundle sSavedInstanceState) {
		super.onCreate(sSavedInstanceState);
		String[] entries = { "Im Browser öffnen", "Aus dem Cache anzeigen" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.simple_list_item_1, entries);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (position == 0) {
			String url = "https://kpf.bks-campus.ch/infoscreen";
			Intent iKISS = new Intent(Intent.ACTION_VIEW);
			iKISS.setData(Uri.parse(url));
			startActivity(iKISS);
		}
		else {
			Intent iCache = new Intent(InfoscreenChoice.this, KissView.class);
			iCache.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(iCache);
		}
		finish();
	}

}
