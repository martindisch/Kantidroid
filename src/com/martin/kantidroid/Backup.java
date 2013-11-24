package com.martin.kantidroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.Editor;
import org.holoeverywhere.widget.Toast;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class Backup extends Activity implements OnClickListener {

	ImageButton bBackup, bImport;
	TextView tvSync, tvBackup;

	File appdir;
	File databases;
	File preferences;
	File kdroiddir;
	File backupdatabases;
	File backuppreferences;
	File[] files;
	String[] prefnames;

	Typeface tf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_combined);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bBackup = (ImageButton) findViewById(R.id.bBackup);
		bImport = (ImageButton) findViewById(R.id.bImport);
		tvSync = (TextView) findViewById(R.id.tvSyncRoboto);
		tvBackup = (TextView) findViewById(R.id.tvBackupRoboto);
		bBackup.setOnClickListener(this);
		bImport.setOnClickListener(this);

		appdir = getFilesDir().getParentFile();
		databases = new File(appdir + "/databases");
		preferences = new File(appdir + "/shared_prefs");
		kdroiddir = new File(Environment.getExternalStorageDirectory(),
				"/Kantidroid");
		backupdatabases = new File(kdroiddir + "/backup/databases");
		backuppreferences = new File(kdroiddir + "/backup/shared_prefs");
		prefnames = getResources().getStringArray(R.array.prefnames);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
		tvSync.setTypeface(tf);
		tvBackup.setTypeface(tf);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bBackup:
			// Delete older backups

			if (backupdatabases.isDirectory()) {
				files = backupdatabases.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
			if (backuppreferences.isDirectory()) {
				files = backuppreferences.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}

			// Backup

			if (databases.isDirectory()) {
				files = databases.listFiles();
				backupdatabases.mkdirs();
				for (int i = 0; i < files.length; i++) {
					try {
						copy(files[i], new File(backupdatabases + "/"
								+ files[i].getName()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			for (int i = 0; i < prefnames.length; i++) {
				saveSharedPreferencesToFile(prefnames[i], new File(
						backuppreferences + "/" + prefnames[i]));
			}
			Toast.makeText(this, "Daten gesichert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bImport:
			AlertDialog.Builder delDg = new AlertDialog.Builder(this);
			delDg.setTitle("Import");
			delDg.setMessage("Stelle sicher, dass du wirklich ein älteres Backup im Ordner Kantidroid/Backup auf dem externen Speicher hast, da alle Daten in der App vor dem Import gelöscht werden.\n\nWillst du fortfahren?\n");
			delDg.setNegativeButton("Nein", null);
			delDg.setPositiveButton("Ja",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							// Delete existing data

							if (databases.isDirectory()) {
								files = databases.listFiles();
								for (int i = 0; i < files.length; i++) {
									files[i].delete();
								}
							}
							if (preferences.isDirectory()) {
								files = preferences.listFiles();
								for (int i = 0; i < files.length; i++) {
									files[i].delete();
								}
							}

							// Copy new data

							if (backupdatabases.isDirectory()) {
								files = backupdatabases.listFiles();
								databases.mkdirs();
								for (int i = 0; i < files.length; i++) {
									try {
										copy(files[i], new File(databases + "/"
												+ files[i].getName()));
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}

							for (int i = 0; i < prefnames.length; i++) {
								loadSharedPreferencesFromFile(prefnames[i],
										new File(backuppreferences + "/"
												+ prefnames[i]));
							}
							Toast.makeText(getApplicationContext(), "Daten importiert",
									Toast.LENGTH_SHORT).show();
						}

					});
			delDg.show();

			break;
		}
	}

	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	private boolean saveSharedPreferencesToFile(String prefName, File dst) {
		boolean res = false;
		ObjectOutputStream output = null;
		try {
			output = new ObjectOutputStream(new FileOutputStream(dst));
			SharedPreferences pref = getSharedPreferences(prefName,
					MODE_PRIVATE);
			output.writeObject(pref.getAll());

			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (output != null) {
					output.flush();
					output.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
	}

	@SuppressWarnings({ "unchecked" })
	private boolean loadSharedPreferencesFromFile(String prefName, File src) {
		boolean res = false;
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(new FileInputStream(src));
			Editor prefEdit = getSharedPreferences(prefName, MODE_PRIVATE)
					.edit();
			prefEdit.clear();
			Map<String, ?> entries = (Map<String, ?>) input.readObject();
			for (Entry<String, ?> entry : entries.entrySet()) {
				Object v = entry.getValue();
				String key = entry.getKey();

				if (v instanceof Boolean)
					prefEdit.putBoolean(key, ((Boolean) v).booleanValue());
				else if (v instanceof Float)
					prefEdit.putFloat(key, ((Float) v).floatValue());
				else if (v instanceof Integer)
					prefEdit.putInt(key, ((Integer) v).intValue());
				else if (v instanceof Long)
					prefEdit.putLong(key, ((Long) v).longValue());
				else if (v instanceof String)
					prefEdit.putString(key, ((String) v));
			}
			prefEdit.commit();
			res = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return res;
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
