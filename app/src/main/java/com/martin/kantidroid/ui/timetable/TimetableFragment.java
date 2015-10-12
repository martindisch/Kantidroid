package com.martin.kantidroid.ui.timetable;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import java.io.File;

public class TimetableFragment extends Fragment implements View.OnClickListener, TimetableAdapter.OnClickListener {

    private TextInputLayout mTilClass;
    private EditText mClass;
    private Button mDownload;
    private View mNothingImage, mDownloadsCard;
    private TimetableAdapter mAdapter;
    private ProgressBar mProgress;
    private boolean mHasError;
    private RecyclerView rvDownloads;

    public static TimetableFragment newInstance() {
        return new TimetableFragment();
    }

    public TimetableFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.timetable_fragment, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.timetable);

        mTilClass = (TextInputLayout) rootView.findViewById(R.id.tilClass);
        mTilClass.setErrorEnabled(true);
        mClass = (EditText) rootView.findViewById(R.id.etClass);
        mDownload = (Button) rootView.findViewById(R.id.bDownload);
        mNothingImage = rootView.findViewById(R.id.ivNothing);
        mDownloadsCard = rootView.findViewById(R.id.cvDownloads);
        rvDownloads = (RecyclerView) rootView.findViewById(R.id.rvDownloads);
        mProgress = (ProgressBar) rootView.findViewById(R.id.pbDownload);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvDownloads.hasFixedSize();
        rvDownloads.setLayoutManager(layoutManager);
        rvDownloads.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));

        if (savedInstanceState != null) {
            mHasError = savedInstanceState.getBoolean("mHasError");
        } else {
            mHasError = false;
        }
        if (mHasError) {
            mTilClass.setError(getString(R.string.timetable_error_noclass));
        }

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            mDownloadsCard.setVisibility(View.GONE);
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            showTimetables();
        }

        Glide.with(this).load(R.drawable.timetable_nodownloads).into((ImageView) rootView.findViewById(R.id.ivNothing));

        mDownload.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            downloadTimetable();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mHasError", mHasError);
    }

    @Override
    public void onItemClick(View v, int position) {
        File f = mAdapter.getData().get(position);
        MimeTypeMap myMime = MimeTypeMap.getSingleton();
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = myMime.getMimeTypeFromExtension(Util.fileExt(f).substring(1));
        newIntent.setDataAndType(Uri.fromFile(f), mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        SharedPreferences prefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_timetable", f.getAbsolutePath());
        editor.commit();

        try {
            getActivity().startActivity(newIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), R.string.timetable_noreader, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.timetable_deletequestion);
        builder.setNegativeButton(R.string.no, null);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                File f = mAdapter.getData().get(position).getAbsoluteFile();
                f.delete();
                mAdapter.remove(position);
                if (mAdapter.getData().size() == 0) {
                    mDownloadsCard.setVisibility(View.GONE);
                    mNothingImage.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case 0:
                    showTimetables();
                    break;
                case 1:
                    showTimetables();
                    downloadTimetable();
                    break;
            }
        }
    }

    private void showTimetables() {
        File[] downloads = Util.getDownloadedFiles();
        if (downloads != null) {
            if (downloads.length == 0) {
                mDownloadsCard.setVisibility(View.GONE);
                mNothingImage.setVisibility(View.VISIBLE);
            } else {
                mNothingImage.setVisibility(View.GONE);
                mDownloadsCard.setVisibility(View.VISIBLE);
            }
            mAdapter = new TimetableAdapter(getActivity(), downloads, this);
            rvDownloads.setAdapter(mAdapter);
        } else {
            mDownloadsCard.setVisibility(View.GONE);
            Toast.makeText(getActivity(), R.string.timetable_error_filesystem, Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadTimetable() {
        final String classUrl = Util.getClassUrl(mClass.getText().toString());
        if (!classUrl.contentEquals("error")) {
            mTilClass.setError(null);
            mHasError = false;
            final File downloadFile = Util.getTimetableFile(classUrl);
            if (downloadFile != null) {
                mProgress.setVisibility(View.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Util.isNetworkAvailable(getActivity())) {
                            String downloadUrl = getString(R.string.url_timetable) + classUrl + ".pdf";
                            if (!Util.urlExists(downloadUrl)) {
                                downloadUrl = getString(R.string.url_timetable) + classUrl.replace("%20", "") + ".pdf";
                            }
                            if (Util.urlExists(downloadUrl)) {
                                Ion.with(getActivity())
                                        .load(downloadUrl)
                                        .write(downloadFile)
                                        .setCallback(new FutureCallback<File>() {
                                            @Override
                                            public void onCompleted(final Exception e, final File file) {
                                                if (e != null) {
                                                    mDownload.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                            mProgress.setVisibility(View.INVISIBLE);
                                                        }
                                                    });
                                                } else {
                                                    mDownload.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getActivity(), R.string.timetable_success, Toast.LENGTH_SHORT).show();
                                                            mProgress.setVisibility(View.INVISIBLE);
                                                            mNothingImage.setVisibility(View.GONE);
                                                            mDownloadsCard.setVisibility(View.VISIBLE);
                                                            if (!mAdapter.getData().contains(file)) {

                                                                mAdapter.add(file);

                                                                SharedPreferences prefs = getActivity().getSharedPreferences("Kantidroid", Context.MODE_PRIVATE);
                                                                File f = new File(prefs.getString("last_timetable", "nope"));
                                                                if (!prefs.contains("last_timetable") || !f.exists()) {
                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                    editor.putString("last_timetable", file.getAbsolutePath());
                                                                    editor.commit();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            } else {
                                mDownload.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), R.string.timetable_nosuchclass, Toast.LENGTH_SHORT).show();
                                        mProgress.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        } else {
                            mDownload.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.timetable_noconnection, Toast.LENGTH_SHORT).show();
                                    mProgress.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                }).start();
            } else {
                Toast.makeText(getActivity(), R.string.timetable_error_filesystem, Toast.LENGTH_LONG).show();
            }
        } else {
            mTilClass.setError(getString(R.string.timetable_error_noclass));
            mHasError = true;
        }
    }
}
