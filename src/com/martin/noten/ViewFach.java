package com.martin.noten;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.Toast;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.martin.kantidroid.Check;
import com.martin.kantidroid.R;
import com.martin.kantidroid.WidgetProvider;

public class ViewFach extends Activity {

	TextView name, math_average, real_average, promotionsfach;
	int id, iSemester;
	String fname, fnoten, addition, fmath_average, freal_average,
			fpromotionsfach;
	String[] entries, mark;
	Typeface tf;
	ImageButton ibAddMark, ibMarkRequest;
	ListView lvViewfach;

	@Override
	protected void onStop() {
		super.onStop();
		Intent rIntent = new Intent(this, WidgetProvider.class);
		rIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
		int[] ids = AppWidgetManager.getInstance(getApplication())
				.getAppWidgetIds(
						new ComponentName(getApplication(),
								WidgetProvider.class));
		rIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
		sendBroadcast(rIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewfach_noten);
		getData();
		initialize();
		Check check = new Check();
		if (!check.getSeen(getClass().getName(), this)) {
			AlertDialog.Builder dg = new AlertDialog.Builder(this);
			dg.setTitle("Info");
			dg.setNeutralButton("Schliessen", null);
			dg.setMessage(R.string.noten_viewfach);
			dg.show();
			check.setSeen(getClass().getName(), this);
		}
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
		name.setTypeface(tf);
	}

	private void getData() {
		Bundle received = getIntent().getExtras();
		DatabaseHandler db = new DatabaseHandler(this);

		id = received.getInt("id");
		iSemester = received.getInt("semester");
		Fach fach = db.getFach(id);

		fname = fach.getName();
		if (iSemester == 1) {
			fnoten = fach.getNoten1();
			if (!fnoten.contentEquals("-")) {
				entries = fnoten.split("\n");
			}
			fmath_average = fach.getMathAverage1();
			freal_average = fach.getRealAverage1();
			fpromotionsfach = fach.getPromotionsrelevant();
		} else {
			fnoten = fach.getNoten2();
			if (!fnoten.contentEquals("-")) {
				entries = fnoten.split("\n");
			}
			fmath_average = fach.getMathAverage2();
			freal_average = fach.getRealAverage2();
			fpromotionsfach = fach.getPromotionsrelevant();
		}
	}

	private void initialize() {
		name = (TextView) findViewById(R.id.tvFachTitle);
		math_average = (TextView) findViewById(R.id.tvArthmeticMedium);
		real_average = (TextView) findViewById(R.id.tvGradeMedium);
		promotionsfach = (TextView) findViewById(R.id.tvPromotion);
		lvViewfach = (ListView) findViewById(R.id.lvViewfach);
		
		ibAddMark = (ImageButton) findViewById(R.id.ibAddMark);
		ibMarkRequest = (ImageButton) findViewById(R.id.ibMarkRequest);
		ibAddMark.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addMark();
			}
		});
		
		ibMarkRequest.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		updateText();
	}

	private void updateText() {
		String[] sDates = new String[entries.length];
		String[] sRelevances = new String[entries.length];
		String[] sMarks = new String[entries.length];
		
		name.setText(fname);
		if (!fnoten.contentEquals("-")) {
			for (int i = 0; i < entries.length; i++) {
				mark = entries[i].split(" - ");
				sMarks[i] = mark[0];
				sRelevances[i] = mark[1];
				sDates[i] = mark[2];
			}
		}

		if (fpromotionsfach.contentEquals("true")) {
			promotionsfach.setText("Ja");
		} else {
			promotionsfach.setText("Nein");
		}

		math_average.setText(fmath_average);
		real_average.setText(freal_average);
		
		ViewFachAdapter adapter = new ViewFachAdapter(this, sDates, sRelevances, sMarks);
		lvViewfach.setAdapter(adapter);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getData();
		updateText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflator = getMenuInflater();
		inflator.inflate(R.menu.viewmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.mViewDiscard:
			removeMark();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void removeMark() {
		DatabaseHandler db = new DatabaseHandler(this);
		Fach check = db.getFach(id);
		if (iSemester == 1) {
			if (!check.getNoten1().contentEquals("-")) {
				Bundle data2 = new Bundle();
				data2.putInt("id", id);
				data2.putInt("semester", iSemester);
				Intent i2 = new Intent(ViewFach.this, RemoveMark.class);
				i2.putExtras(data2);
				i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i2);
			} else {
				Toast t = Toast.makeText(ViewFach.this,
						"Keine Note zu entfernen", Toast.LENGTH_SHORT);
				t.show();
			}
		} else {
			if (!check.getNoten2().contentEquals("-")) {
				Bundle data2 = new Bundle();
				data2.putInt("id", id);
				data2.putInt("semester", iSemester);
				Intent i2 = new Intent(ViewFach.this, RemoveMark.class);
				i2.putExtras(data2);
				i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i2);
			} else {
				Toast t = Toast.makeText(ViewFach.this,
						"Keine Note zu entfernen", Toast.LENGTH_SHORT);
				t.show();
			}
		}
	}

	private void addMark() {
		Bundle data = new Bundle();
		data.putInt("id", id);
		data.putInt("semester", iSemester);
		Intent i = new Intent(ViewFach.this, AddMark.class);
		i.putExtras(data);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	private void startGuessing() {
		DatabaseHandler dba = new DatabaseHandler(this);
		Fach checka = dba.getFach(id);
		if (iSemester == 1) {
			if (!checka.getNoten1().contentEquals("-")) {
				Bundle data2 = new Bundle();
				data2.putInt("id", id);
				data2.putInt("semester", iSemester);
				Intent i2 = new Intent(ViewFach.this, Guessing.class);
				i2.putExtras(data2);
				i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i2);
			} else {
				Toast t = Toast.makeText(ViewFach.this,
						"Keine Noten vorhanden", Toast.LENGTH_SHORT);
				t.show();
			}
		} else {
			if (!checka.getNoten2().contentEquals("-")) {
				Bundle data2 = new Bundle();
				data2.putInt("id", id);
				data2.putInt("semester", iSemester);
				Intent i2 = new Intent(ViewFach.this, Guessing.class);
				i2.putExtras(data2);
				i2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i2);
			} else {
				Toast t = Toast.makeText(ViewFach.this,
						"Keine Noten vorhanden", Toast.LENGTH_SHORT);
				t.show();
			}
		}
	}

}
