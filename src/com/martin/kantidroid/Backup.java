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
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxException.Unauthorized;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.SyncStatusListener;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

public class Backup extends Activity implements OnClickListener {

	ImageButton bBackup, bImport;
	TextView tvSync, tvBackup, tvSyncprogress;
	Button bDbxBackup, bDbxImport;
	ProgressBar pbDbx;
	ImageView ivSuccess;

	File appdir;
	File databases;
	File preferences;
	File kdroiddir;
	File backupdatabases;
	File backuppreferences;
	File[] files;
	String[] prefnames;

	Typeface tf;

	private static final String appKey = "03ktxe8m7s1i0b6";
	private static final String appSecret = "tvsp7mo5rtiogb1";

	private DbxAccountManager mDbxAcctMgr;
	private DbxFileSystem dbxFs;

	private static final int REQUEST_LINK_TO_DBX = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup_combined);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		bBackup = (ImageButton) findViewById(R.id.bBackup);
		bImport = (ImageButton) findViewById(R.id.bImport);
		tvSync = (TextView) findViewById(R.id.tvSyncRoboto);
		tvBackup = (TextView) findViewById(R.id.tvBackupRoboto);
		tvSyncprogress = (TextView) findViewById(R.id.tvSyncprogressRoboto);
		bDbxBackup = (Button) findViewById(R.id.bDbxBackup);
		bDbxImport = (Button) findViewById(R.id.bDbxImport);
		pbDbx = (ProgressBar) findViewById(R.id.pbDbx);
		ivSuccess = (ImageView) findViewById(R.id.ivSuccess);
		bBackup.setOnClickListener(this);
		bImport.setOnClickListener(this);
		bDbxBackup.setOnClickListener(this);
		bDbxImport.setOnClickListener(this);

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
		tvSyncprogress.setTypeface(tf);

		mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(),
				appKey, appSecret);
		initFs();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bBackup:
			localBackup();
			break;
		case R.id.bImport:
			localImport();
			break;
		case R.id.bDbxBackup:
			if (!mDbxAcctMgr.hasLinkedAccount()) {
				mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
			} else {
				// Make local backup

				localBackup();

				// Sync to Dropbox

				DbxPath databasePath = new DbxPath("/databases");
				DbxPath prefPath = new DbxPath("/shared_prefs");
				DbxFile dbxFile;

				// databases

				if (databases.isDirectory()) {
					files = databases.listFiles();
					for (int i = 0; i < files.length; i++) {
						try {
							if (dbxFs.exists(new DbxPath(databasePath, files[i]
									.getName()))) {
								dbxFile = dbxFs.open(new DbxPath(databasePath,
										files[i].getName()));
							} else {
								dbxFile = dbxFs.create(new DbxPath(
										databasePath, files[i].getName()));
							}
							dbxFile.writeFromExistingFile(files[i], false);
							dbxFile.close();
						} catch (InvalidPathException e) {
							Toast.makeText(this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						} catch (DbxException e) {
							Toast.makeText(this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						} catch (IOException e) {
							Toast.makeText(this, e.getMessage(),
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}

				// shared_prefs

				if (preferences.isDirectory()) {
					for (int i = 0; i < prefnames.length; i++) {
						saveSharedPreferencesToFile(prefnames[i], new File(
								backuppreferences + "/" + prefnames[i]));
						files = preferences.listFiles();
						for (int i1 = 0; i1 < files.length; i1++) {
							try {
								if (dbxFs.exists(new DbxPath(prefPath,
										files[i1].getName()))) {
									dbxFile = dbxFs.open(new DbxPath(prefPath,
											files[i1].getName()));
								} else {
									dbxFile = dbxFs.create(new DbxPath(
											prefPath, files[i1].getName()));
								}
								dbxFile.writeFromExistingFile(files[i1], false);
								dbxFile.close();
							} catch (InvalidPathException e) {
								Toast.makeText(this, e.getMessage(),
										Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							} catch (DbxException e) {
								Toast.makeText(this, e.getMessage(),
										Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							} catch (IOException e) {
								Toast.makeText(this, e.getMessage(),
										Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							}
						}
					}
				}
				Toast.makeText(this, "Daten werden hochgeladen",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.bDbxImport:

			break;
		}
	}

	private void localBackup() {
		if (!dbxSynchronizing()) {
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
		} else {
			Toast.makeText(this, "Dropbox synchronisiert gerade",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void localImport() {
		AlertDialog.Builder delDg = new AlertDialog.Builder(this);
		delDg.setTitle("Import");
		delDg.setMessage("Stelle sicher, dass du wirklich ein älteres Backup im Ordner Kantidroid/Backup auf dem externen Speicher hast, da alle Daten in der App vor dem Import gelöscht werden.\n\nWillst du fortfahren?\n");
		delDg.setNegativeButton("Nein", null);
		delDg.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

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
							copy(files[i],
									new File(databases + "/"
											+ files[i].getName()));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				for (int i = 0; i < prefnames.length; i++) {
					loadSharedPreferencesFromFile(prefnames[i], new File(
							backuppreferences + "/" + prefnames[i]));
				}
				Toast.makeText(getApplicationContext(), "Daten importiert",
						Toast.LENGTH_SHORT).show();
			}

		});
		delDg.show();
	}

	private boolean dbxSynchronizing() {
		boolean synchronizing = false;
		if (mDbxAcctMgr.hasLinkedAccount()) {
			try {
				if (dbxFs.getSyncStatus().upload.inProgress
						|| dbxFs.getSyncStatus().download.inProgress) {
					synchronizing = true;
				}
			} catch (DbxException e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
		}
		return synchronizing;
	}

	private void initFs() {
		if (mDbxAcctMgr.hasLinkedAccount()) {
			try {
				dbxFs = DbxFileSystem
						.forAccount(mDbxAcctMgr.getLinkedAccount());
				dbxFs.addSyncStatusListener(new SyncStatusListener() {

					@Override
					public void onSyncStatusChange(DbxFileSystem arg0) {
						if (dbxSynchronizing()) {
							pbDbx.setVisibility(ProgressBar.VISIBLE);
							ivSuccess.setVisibility(ImageView.INVISIBLE);
							tvSyncprogress.setText("Synchronisieren");
						} else {
							pbDbx.setVisibility(ProgressBar.INVISIBLE);
							ivSuccess.setVisibility(ImageView.VISIBLE);
							tvSyncprogress.setText("Synchronisiert");
						}
					}

				});
				if (dbxSynchronizing()) {
					pbDbx.setVisibility(ProgressBar.VISIBLE);
					ivSuccess.setVisibility(ImageView.INVISIBLE);
					tvSyncprogress.setText("Synchronisieren");
				} else {
					pbDbx.setVisibility(ProgressBar.INVISIBLE);
					ivSuccess.setVisibility(ImageView.VISIBLE);
					tvSyncprogress.setText("Synchronisiert");
				}
			} catch (Unauthorized e) {
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			}
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
		backuppreferences.mkdirs();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_LINK_TO_DBX) {
			if (resultCode == Activity.RESULT_OK) {
				initFs();
				Toast.makeText(this,
						"Bei Dropbox eingeloggt. Nochmals drücken für Backup",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Login fehlgeschlagen", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}
