package com.martin.kantidroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxFileSystem.SyncStatusListener;
import com.dropbox.sync.android.DbxPath;
import com.dropbox.sync.android.DbxPath.InvalidPathException;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.preference.SharedPreferences;
import org.holoeverywhere.preference.SharedPreferences.Editor;
import org.holoeverywhere.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Backup extends Activity implements OnClickListener {

    private static final int REQUEST_LINK_TO_DBX = 0;
    private ImageButton bBackup;
    private ImageButton bImport;
    private TextView tvSync;
    private TextView tvBackup;
    private TextView tvSyncprogress;
    private Button bDbxBackup;
    private Button bDbxImport;
    private ProgressBar pbDbx;
    private ImageView ivSuccess;
    private Context context = this;
    private File appdir;
    private File databases;
    private File preferences;
    private File kdroiddir;
    private File backupdatabases;
    private File backuppreferences;
    private File[] files;
    private String[] prefnames;
    private Typeface tf;
    private DbxPath databasePath;
    private DbxPath prefPath;
    private DbxFile dbxFile;
    private String appKey = "03ktxe8m7s1i0b6";
    // Is initialized later on, loaded from credential storage
    private String appSecret;
    private DbxAccountManager mDbxAcctMgr;
    private DbxFileSystem dbxFs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appSecret = getApplicationContext().getString(R.string.appSecret);

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
        kdroiddir = new File(Environment.getExternalStorageDirectory(), "/Kantidroid");
        backupdatabases = new File(kdroiddir + "/backup/databases");
        backuppreferences = new File(kdroiddir + "/backup/shared_prefs");
        prefnames = getResources().getStringArray(R.array.prefnames);

        tf = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
        tvSync.setTypeface(tf);
        tvBackup.setTypeface(tf);
        tvSyncprogress.setTypeface(tf);

        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), appKey, appSecret);

        if (!mDbxAcctMgr.hasLinkedAccount()) {
            tvSyncprogress.setVisibility(View.INVISIBLE);
            pbDbx.setVisibility(View.INVISIBLE);
            ivSuccess.setVisibility(View.INVISIBLE);
        }
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
                    mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
                } else {
                    AlertDialog.Builder delDg = new AlertDialog.Builder(this);
                    delDg.setTitle("Dropbox");
                    delDg.setMessage("Damit werden bereits vorhandene lokale Backups �berschrieben.\n\nWillst du fortfahren?\n");
                    delDg.setNegativeButton("Nein", null);
                    delDg.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Make local backup

                            localBackup_noDg();

                            // Sync to Dropbox

                            // databases

                            if (databases.isDirectory()) {
                                files = databases.listFiles();
                                for (int i = 0; i < files.length; i++) {
                                    try {
                                        if (dbxFs.exists(new DbxPath(databasePath, files[i].getName()))) {
                                            dbxFile = dbxFs.open(new DbxPath(databasePath, files[i].getName()));
                                        } else {
                                            dbxFile = dbxFs.create(new DbxPath(databasePath, files[i].getName()));
                                        }
                                        dbxFile.writeFromExistingFile(files[i], false);
                                        dbxFile.close();
                                    } catch (InvalidPathException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (DbxException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // shared_prefs

                            if (preferences.isDirectory()) {
                                for (int i1 = 0; i1 < prefnames.length; i1++) {
                                    try {
                                        if (dbxFs.exists(new DbxPath(prefPath, prefnames[i1]))) {
                                            dbxFile = dbxFs.open(new DbxPath(prefPath, prefnames[i1]));
                                        } else {
                                            dbxFile = dbxFs.create(new DbxPath(prefPath, prefnames[i1]));
                                        }
                                        dbxFile.writeFromExistingFile(new File(backuppreferences + "/" + prefnames[i1]), false);
                                        dbxFile.close();
                                    } catch (InvalidPathException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (DbxException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Toast.makeText(getApplicationContext(), "Daten werden hochgeladen", Toast.LENGTH_SHORT).show();
                        }

                    });
                    delDg.show();
                }
                break;
            case R.id.bDbxImport:
                if (!mDbxAcctMgr.hasLinkedAccount()) {
                    mDbxAcctMgr.startLink(this, REQUEST_LINK_TO_DBX);
                } else {
                    final Handler handler = new Handler();
                    AlertDialog.Builder delDg = new AlertDialog.Builder(this);
                    delDg.setTitle("Dropbox");
                    delDg.setMessage("Damit werden bereits vorhandene lokale Backups �berschrieben.\n\nWillst du fortfahren?\n");
                    delDg.setNegativeButton("Nein", null);
                    delDg.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final ProgressDialog pd = new ProgressDialog(context);
                            pd.setMessage("Daten werden importiert...");
                            pd.setCancelable(false);
                            pd.setIndeterminate(true);
                            pd.show();

                            new Thread() {
                                @Override
                                public void run() {
                                    try {
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

                                        // Import to local backups

                                        backupdatabases.mkdirs();
                                        if (dbxFs.exists(databasePath)) {
                                            List<DbxFileInfo> databaseList = dbxFs.listFolder(databasePath);
                                            for (int i = 0; i < databaseList.size(); i++) {
                                                dbxFile = dbxFs.open(databaseList.get(i).path);
                                                copyDbx(dbxFile.getReadStream(), new File(backupdatabases + "/" + dbxFile.getPath().getName()));
                                                dbxFile.close();
                                            }
                                        }

                                        backuppreferences.mkdirs();
                                        if (dbxFs.exists(prefPath)) {
                                            List<DbxFileInfo> prefList = dbxFs.listFolder(prefPath);
                                            for (int i = 0; i < prefList.size(); i++) {
                                                dbxFile = dbxFs.open(prefList.get(i).path);
                                                copyDbx(dbxFile.getReadStream(), new File(backuppreferences + "/" + dbxFile.getPath().getName()));
                                                dbxFile.close();
                                            }
                                        }

                                        // Do local import

                                        handler.post(new Runnable() {

                                            @Override
                                            public void run() {
                                                localImport_nodg();
                                                pd.dismiss();
                                            }
                                        });

                                    } catch (DbxException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        }

                    });
                    delDg.show();
                }
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
                        copy(files[i], new File(backupdatabases + "/" + files[i].getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (int i = 0; i < prefnames.length; i++) {
                saveSharedPreferencesToFile(prefnames[i], new File(backuppreferences + "/" + prefnames[i]));
            }
            Toast.makeText(this, "Daten gesichert", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dropbox synchronisiert gerade", Toast.LENGTH_SHORT).show();
        }
    }

    private void localBackup_noDg() {
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
                        copy(files[i], new File(backupdatabases + "/" + files[i].getName()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (int i = 0; i < prefnames.length; i++) {
                saveSharedPreferencesToFile(prefnames[i], new File(backuppreferences + "/" + prefnames[i]));
            }
            Toast.makeText(this, "Daten gesichert", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Dropbox synchronisiert gerade", Toast.LENGTH_SHORT).show();
        }
    }

    private void localImport() {
        AlertDialog.Builder delDg = new AlertDialog.Builder(this);
        delDg.setTitle("Import");
        delDg.setMessage("Stelle sicher, dass du wirklich ein �lteres Backup im Ordner Kantidroid/Backup auf dem externen Speicher hast, da alle Daten in der App vor dem Import gel�scht werden.\n\nWillst du fortfahren?\n");
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
                        if (!files[i].getName().contentEquals("dropbox-credentials.xml")) {
                            files[i].delete();
                        }
                    }
                }

                // Copy new data

                if (backupdatabases.isDirectory()) {
                    files = backupdatabases.listFiles();
                    databases.mkdirs();
                    for (int i = 0; i < files.length; i++) {
                        try {
                            copy(files[i], new File(databases + "/" + files[i].getName()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (int i = 0; i < prefnames.length; i++) {
                    loadSharedPreferencesFromFile(prefnames[i], new File(backuppreferences + "/" + prefnames[i]));
                }
                Toast.makeText(getApplicationContext(), "Daten importiert", Toast.LENGTH_SHORT).show();
            }

        });
        delDg.show();
    }

    private void localImport_nodg() {
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
                if (!files[i].getName().contentEquals("dropbox-credentials.xml")) {
                    files[i].delete();
                }
            }
        }

        // Copy new data

        if (backupdatabases.isDirectory()) {
            files = backupdatabases.listFiles();
            databases.mkdirs();
            for (int i = 0; i < files.length; i++) {
                try {
                    copy(files[i], new File(databases + "/" + files[i].getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < prefnames.length; i++) {
            loadSharedPreferencesFromFile(prefnames[i], new File(backuppreferences + "/" + prefnames[i]));
        }
        Toast.makeText(getApplicationContext(), "Daten importiert", Toast.LENGTH_SHORT).show();
    }

    private boolean dbxSynchronizing() {
        boolean synchronizing = false;
        if (mDbxAcctMgr.hasLinkedAccount()) {
            try {
                if (dbxFs.getSyncStatus().upload.inProgress || dbxFs.getSyncStatus().download.inProgress) {
                    synchronizing = true;
                }
            } catch (DbxException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        return synchronizing;
    }

    private void initFs() {
        if (mDbxAcctMgr.hasLinkedAccount()) {
            try {
                databasePath = new DbxPath("/databases");
                prefPath = new DbxPath("/shared_prefs");
                dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
                dbxFs.addSyncStatusListener(new SyncStatusListener() {

                    @Override
                    public void onSyncStatusChange(DbxFileSystem arg0) {
                        if (dbxSynchronizing()) {
                            pbDbx.setVisibility(View.VISIBLE);
                            ivSuccess.setVisibility(View.INVISIBLE);
                            tvSyncprogress.setText("Synchronisieren");
                        } else {
                            pbDbx.setVisibility(View.INVISIBLE);
                            ivSuccess.setVisibility(View.VISIBLE);
                            tvSyncprogress.setText("Synchronisiert");
                        }
                    }

                });
                if (dbxSynchronizing()) {
                    pbDbx.setVisibility(View.VISIBLE);
                    ivSuccess.setVisibility(View.INVISIBLE);
                    tvSyncprogress.setText("Synchronisieren");
                } else {
                    pbDbx.setVisibility(View.INVISIBLE);
                    ivSuccess.setVisibility(View.VISIBLE);
                    tvSyncprogress.setText("Synchronisiert");
                }
            } catch (Unauthorized e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void copy(File src, File dst) throws IOException {
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

    private void copyDbx(InputStream in, File dst) throws IOException {
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

    private void saveSharedPreferencesToFile(String prefName, File dst) {
        backuppreferences.mkdirs();
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(dst));
            SharedPreferences pref = getSharedPreferences(prefName, MODE_PRIVATE);
            output.writeObject(pref.getAll());
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
    }

    @SuppressWarnings({"unchecked"})
    private void loadSharedPreferencesFromFile(String prefName, File src) {
        ObjectInputStream input = null;
        try {
            input = new ObjectInputStream(new FileInputStream(src));
            Editor prefEdit = getSharedPreferences(prefName, MODE_PRIVATE).edit();
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
            if (resultCode == android.app.Activity.RESULT_OK) {
                initFs();
                Toast.makeText(this, "Bei Dropbox eingeloggt. Nochmals dr�cken f�r Backup oder Import.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Login fehlgeschlagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
