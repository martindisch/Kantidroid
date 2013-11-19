package com.martin.kantidroid;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.Toast;

import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class Backup extends Activity implements OnClickListener {

	Button bBackup, bImport, bDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bBackup = (Button) findViewById(R.id.bBackup);
		bImport = (Button) findViewById(R.id.bImport);
		bDelete = (Button) findViewById(R.id.bDelete);
		bBackup.setOnClickListener(this);
		bImport.setOnClickListener(this);
		bDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bBackup:

			// Init

			File appdir = getFilesDir().getParentFile();
			File databases = new File(appdir + "/databases");
			File preferences = new File(appdir + "/shared_prefs");
			File kdroiddir = new File(
					Environment.getExternalStorageDirectory(), "/Kantidroid");
			File backupdatabases = new File(kdroiddir + "/backup/databases");
			File backuppreferences = new File(kdroiddir
					+ "/backup/shared_prefs");
			File[] files;

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
			if (preferences.isDirectory()) {
				files = preferences.listFiles();
				backuppreferences.mkdirs();
				for (int i = 0; i < files.length; i++) {
					try {
						copy(files[i], new File(backuppreferences + "/"
								+ files[i].getName()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Toast.makeText(this, "Daten gesichert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bImport:
			appdir = getFilesDir().getParentFile();
			databases = new File(appdir + "/databases");
			preferences = new File(appdir + "/shared_prefs");
			kdroiddir = new File(Environment.getExternalStorageDirectory(),
					"/Kantidroid");
			backupdatabases = new File(kdroiddir + "/backup/databases");
			backuppreferences = new File(kdroiddir + "/backup/shared_prefs");

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
						copy(files[i],
								new File(databases + "/" + files[i].getName()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (backuppreferences.isDirectory()) {
				files = backuppreferences.listFiles();
				preferences.mkdirs();
				for (int i = 0; i < files.length; i++) {
					try {
						copy(files[i],
								new File(preferences + "/" + files[i].getName()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			Toast.makeText(this, "Daten importiert", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bDelete:
			appdir = getFilesDir().getParentFile();
			databases = new File(appdir + "/databases");
			preferences = new File(appdir + "/shared_prefs");

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
			Toast.makeText(this, "Daten gelöscht", Toast.LENGTH_SHORT).show();
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
