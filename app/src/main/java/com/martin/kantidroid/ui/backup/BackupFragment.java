package com.martin.kantidroid.ui.backup;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.util.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BackupFragment extends Fragment {

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private String APP_KEY, APP_SECRET;
    private boolean linked;
    private int mAction;
    private ProgressBar mProgress;
    private ImageView mCheck;
    private TextView mStatus;

    public static BackupFragment newInstance() {
        return new BackupFragment();
    }

    public BackupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APP_KEY = getString(R.string.app_key);
        APP_SECRET = getString(R.string.app_secret);
        linked = false;
        mAction = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.backup_fragment, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.backup);

        int measuredWidth = 0;
        WindowManager w = getActivity().getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            w.getDefaultDisplay().getSize(size);
            measuredWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            measuredWidth = d.getWidth();
        }

        int calcHeight = (int) Math.round(measuredWidth * 0.381);
        ImageView ivDropboxLogo = (ImageView) rootView.findViewById(R.id.ivDropboxLogo);
        ivDropboxLogo.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, calcHeight));

        RecyclerView mSelection = (RecyclerView) rootView.findViewById(R.id.rvLocal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mSelection.setLayoutManager(layoutManager);
        mSelection.setHasFixedSize(true);
        mSelection.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));
        BackupAdapter adapter = new BackupAdapter(getActivity(), new BackupAdapter.OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
                    checkPermissionAndDo(Manifest.permission.WRITE_EXTERNAL_STORAGE, 0);
                } else {
                    checkPermissionAndDo(Manifest.permission.READ_EXTERNAL_STORAGE, 1);
                }
            }
        });
        mSelection.setAdapter(adapter);

        mProgress = (ProgressBar) rootView.findViewById(R.id.pbDropbox);
        mCheck = (ImageView) rootView.findViewById(R.id.ivCheck);
        mStatus = (TextView) rootView.findViewById(R.id.tvDropbox);

        SharedPreferences sp = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        if (sp.contains("dropbox_status")) {
            mCheck.setImageResource(R.drawable.ic_cloud_success);
        }
        mStatus.setText(sp.getString("dropbox_status", getString(R.string.backup_db_nobackup)));

        rootView.findViewById(R.id.bBackupDropbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndDo(Manifest.permission.WRITE_EXTERNAL_STORAGE, 2);
            }
        });
        rootView.findViewById(R.id.bImportDropbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndDo(Manifest.permission.READ_EXTERNAL_STORAGE, 3);
            }
        });

        Glide.with(this).load(R.drawable.dropbox).into(ivDropboxLogo);

        return rootView;
    }

    private boolean authenticated() {
        if (!linked) {
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys);
            SharedPreferences prefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
            if (prefs.contains("accessToken")) {
                session.setOAuth2AccessToken(prefs.getString("accessToken", "thisisneverhappening"));
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                if (session.isLinked()) {
                    linked = true;
                }
            } else {
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                mDBApi.getSession().startOAuth2Authentication(getActivity());
                return false;
            }
        }
        return true;
    }

    private void dropboxBackup() {
        mCheck.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        mStatus.setText(R.string.backup_db_backingup);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String msg = "";
                    final int result = Util.backupDropbox(getActivity(), mDBApi);
                    switch (result) {
                        case 0:
                            msg = getString(R.string.backup_db_success);
                            break;
                        case 1:
                            msg = getString(R.string.backup_ext_unavailable);
                            break;
                        case 2:
                            msg = getString(R.string.backup_db_notfile);
                            break;
                        case 3:
                            msg = getString(R.string.backup_db_copyfailed);
                            break;
                        case 4:
                            msg = getString(R.string.backup_prefs_notcopied);
                            break;
                        case 5:
                            msg = getString(R.string.backup_db_notfound);
                            break;
                        case 6:
                            msg = getString(R.string.backup_db_error);
                            break;
                        case 7:
                            msg = getString(R.string.backup_db_inputstream);
                            break;
                        case 8:
                            msg = getString(R.string.backup_db_future);
                            break;
                    }
                    final String endMsg = msg;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setVisibility(View.INVISIBLE);
                            if (result == 0) {
                                Toast.makeText(getActivity(), endMsg, Toast.LENGTH_SHORT).show();
                                mCheck.setImageResource(R.drawable.ic_cloud_success);
                                mCheck.setVisibility(View.VISIBLE);
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                                String date = df.format(c.getTime());
                                String message = getString(R.string.backup_db_msg) + " " + date;
                                mStatus.setText(message);
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).edit();
                                editor.putString("dropbox_status", message);
                                editor.commit();
                            } else {
                                mCheck.setImageResource(R.drawable.ic_cloud_failed);
                                mCheck.setVisibility(View.VISIBLE);
                                mStatus.setText(endMsg);
                            }
                        }
                    });
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        mAction = 0;
    }

    private void dropboxImport() {
        mCheck.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        mStatus.setText(R.string.backup_db_importing);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String msg = "";
                    final int result = Util.importDropbox(getActivity(), mDBApi);
                    switch (result) {
                        case 0:
                            msg = getString(R.string.import_success);
                            break;
                        case 1:
                            msg = getString(R.string.backup_ext_unavailable);
                            break;
                        case 2:
                            msg = getString(R.string.backup_db_notfile);
                            break;
                        case 3:
                            msg = getString(R.string.import_nodblocal);
                            break;
                        case 4:
                            msg = getString(R.string.import_db_copyfailed);
                            break;
                        case 5:
                            msg = getString(R.string.import_prefs_nolocal);
                            break;
                        case 6:
                            msg = getString(R.string.import_prefs_notcopied);
                            break;
                        case 7:
                            msg = getString(R.string.import_db_fail);
                            break;
                        case 8:
                            msg = getString(R.string.import_db_notfound);
                            break;
                        case 9:
                            msg = getString(R.string.backup_db_inputstream);
                            break;
                        case 10:
                            msg = getString(R.string.backup_db_future);
                            break;
                    }
                    final String endMsg = msg;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setVisibility(View.INVISIBLE);
                            mStatus.setText(endMsg);
                            if (result == 0) {
                                Toast.makeText(getActivity(), endMsg, Toast.LENGTH_SHORT).show();
                                mCheck.setImageResource(R.drawable.ic_cloud_success);
                                mCheck.setVisibility(View.VISIBLE);
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
                                String date = df.format(c.getTime());
                                String message = getString(R.string.backup_db_msg) + " " + date;
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).edit();
                                editor.putString("dropbox_status", message);
                                editor.commit();
                            } else {
                                mCheck.setImageResource(R.drawable.ic_cloud_failed);
                                mCheck.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        mAction = 0;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mDBApi != null) {
            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                    // Required to complete auth, sets the access token on the session
                    mDBApi.getSession().finishAuthentication();
                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE).edit();
                    editor.putString("accessToken", accessToken);
                    editor.commit();
                    if (mDBApi.getSession().isLinked()) {
                        linked = true;
                        if (mAction == 1) {
                            dropboxBackup();
                        } else if (mAction == 2) {
                            dropboxImport();
                        }
                    }
                } catch (IllegalStateException e) {
                    Log.i("DbAuthLog", "Error authenticating", e);
                    mAction = 0;
                    Toast.makeText(getActivity(), R.string.backup_nologin, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkPermissionAndDo(String permission, int action) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, action);
        } else {
            switch (action) {
                case 0:
                    doLocalBackup();
                    break;
                case 1:
                    doLocalImport();
                    break;
                case 2:
                    doDropboxBackup();
                    break;
                case 3:
                    doDropboxImport();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 0:
                    doLocalBackup();
                    break;
                case 1:
                    doLocalImport();
                    break;
                case 2:
                    doDropboxBackup();
                    break;
                case 3:
                    doDropboxImport();
                    break;
            }
        } else {
            Toast.makeText(getActivity(), R.string.backup_nopermission, Toast.LENGTH_SHORT).show();
        }
    }

    private void doLocalBackup() {
        String msg = "";
        switch (Util.backupLocal(getActivity())) {
            case 0:
                msg = getString(R.string.backup_success);
                break;
            case 1:
                msg = getString(R.string.backup_ext_unavailable);
                break;
            case 2:
                msg = getString(R.string.backup_db_notfile);
                break;
            case 3:
                msg = getString(R.string.backup_db_copyfailed);
                break;
            case 4:
                msg = getString(R.string.backup_prefs_notcopied);
                break;
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void doLocalImport() {
        String msg = "";
        switch (Util.importLocal(getActivity())) {
            case 0:
                msg = getString(R.string.import_success);
                break;
            case 1:
                msg = getString(R.string.backup_ext_unavailable);
                break;
            case 2:
                msg = getString(R.string.backup_db_notfile);
                break;
            case 3:
                msg = getString(R.string.import_nodblocal);
                break;
            case 4:
                msg = getString(R.string.import_db_copyfailed);
                break;
            case 5:
                msg = getString(R.string.import_prefs_nolocal);
                break;
            case 6:
                msg = getString(R.string.import_prefs_notcopied);
                break;
        }
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void doDropboxBackup() {
        mAction = 1;
        if (authenticated()) {
            dropboxBackup();
        }
    }

    private void doDropboxImport() {
        mAction = 2;
        if (authenticated()) {
            dropboxImport();
        }
    }
}
