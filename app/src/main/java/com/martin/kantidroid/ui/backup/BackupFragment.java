package com.martin.kantidroid.ui.backup;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.util.LinearLayoutManager;

public class BackupFragment extends Fragment {

    private DropboxAPI<AndroidAuthSession> mDBApi;
    private String APP_KEY, APP_SECRET;
    private boolean linked;
    private int mAction;

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

        RecyclerView mSelection = (RecyclerView) rootView.findViewById(R.id.rvLocal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mSelection.setLayoutManager(layoutManager);
        mSelection.setHasFixedSize(true);
        mSelection.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));
        BackupAdapter adapter = new BackupAdapter(getActivity(), new BackupAdapter.OnClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (position == 0) {
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
                } else {
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
            }
        });
        mSelection.setAdapter(adapter);

        Glide.with(this).load(R.drawable.dropbox).fitCenter().into((ImageView) rootView.findViewById(R.id.ivDropboxLogo));

        rootView.findViewById(R.id.bBackupDropbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAction = 1;
                if (authenticated()) {
                    dropboxBackup();
                }
            }
        });
        rootView.findViewById(R.id.bImportDropbox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAction = 2;
                if (authenticated()) {
                    dropboxImport();
                }
            }
        });

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
            }
            else {
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                mDBApi.getSession().startOAuth2Authentication(getActivity());
                return false;
            }
        }
        return true;
    }

    private void dropboxBackup() {
        Log.e("FFF", "Now I would be backing up");
    }

    private void dropboxImport() {
        Log.e("FFF", "Now I would be importing");
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
                        }
                        else if (mAction == 2) {
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
}
