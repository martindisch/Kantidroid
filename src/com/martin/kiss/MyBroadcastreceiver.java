package com.martin.kiss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastreceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, Background.class);
		context.startService(service);
	}
}
