package com.martin.kantidroid.ui.timetable;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
        RecyclerView rvDownloads = (RecyclerView) rootView.findViewById(R.id.rvDownloads);
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

        File[] downloads = Util.getDownloadedFiles();
        if (downloads != null) {
            if (downloads.length == 0) {
                mDownloadsCard.setVisibility(View.GONE);
            } else {
                mNothingImage.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(getActivity(), R.string.timetable_error_filesystem, Toast.LENGTH_SHORT).show();
        }
        Glide.with(this).load(R.drawable.timetable_nodownloads).into((ImageView) rootView.findViewById(R.id.ivNothing));
        mAdapter = new TimetableAdapter(getActivity(), downloads, this);
        rvDownloads.setAdapter(mAdapter);

        mDownload.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
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
                            if (Util.urlExists(getString(R.string.url_timetable) + classUrl + ".pdf")) {
                                Ion.with(getActivity())
                                        .load(getString(R.string.url_timetable) + classUrl + ".pdf")
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
                                                                if (!prefs.contains("last_timetable")) {
                                                                    SharedPreferences.Editor editor = prefs.edit();
                                                                    editor.putString("last_timetable", Uri.fromFile(file).toString());
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
        editor.putString("last_timetable", Uri.fromFile(f).toString());
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
}
