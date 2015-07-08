package com.martin.kantidroid.ui.backup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.DividerItemDecoration;
import com.martin.kantidroid.ui.util.LinearLayoutManager;

public class BackupFragment extends Fragment {

    private RecyclerView mSelection;

    public static BackupFragment newInstance() {
        return new BackupFragment();
    }

    public BackupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mSelection = (RecyclerView) rootView.findViewById(R.id.rvLocal);
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
                }
                else {

                }
            }
        });
        mSelection.setAdapter(adapter);

        Glide.with(this).load(R.drawable.dropbox).fitCenter().into((ImageView) rootView.findViewById(R.id.ivDropboxLogo));

        return rootView;
    }
}
