package com.martin.kantidroid.ui.food;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.DividerItemDecoration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FoodFragment extends Fragment implements FoodAdapter.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mMensa, mKonvikt;
    private SwipeRefreshLayout mSwipeContainer;
    private ScrollView mFoodContainer;
    private TextView mErrorText;

    private ArrayList<String[]> mMensaItems, mKonviktItems;
    private String mPendingURL;

    public FoodFragment() {
        // Required empty public constructor
    }

    public static FoodFragment newInstance() {
        return new FoodFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.food_fragment, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.food);

        mMensa = rootView.findViewById(R.id.rvMensa);
        mKonvikt = rootView.findViewById(R.id.rvKonvikt);
        mSwipeContainer = rootView.findViewById(R.id.swipeContainer);
        mFoodContainer = rootView.findViewById(R.id.svFoodContainer);
        mErrorText = rootView.findViewById(R.id.tvError);

        mMensa.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMensa.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));
        mKonvikt.setLayoutManager(new LinearLayoutManager(getActivity()));
        mKonvikt.addItemDecoration(new DividerItemDecoration(getActivity(), null, false));
        mSwipeContainer.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.accent));
        mSwipeContainer.setOnRefreshListener(this);

        if (savedInstanceState != null) {
            mMensaItems = (ArrayList<String[]>) savedInstanceState.getSerializable("mensa");
            mKonviktItems = (ArrayList<String[]>) savedInstanceState.getSerializable("konvikt");
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMensaItems != null && mKonviktItems != null) {
            updateMenus(false);
        } else {
            updateMenus(true);
        }
    }

    private void updateMenus(final boolean fetch) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fetch) {
                        mSwipeContainer.post(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeContainer.setRefreshing(true);
                            }
                        });
                        Document doc = Jsoup.connect(getString(R.string.url_food)).get();

                        mMensaItems = new ArrayList<>();
                        mKonviktItems = new ArrayList<>();

                        Elements mensEl = doc.select(".WebPart1 a");
                        for (Element el : mensEl) {
                            mMensaItems.add(new String[]{el.text().replace(".pdf", ""), el.attr("href")});
                        }

                        Elements konvEl = doc.select(".WebPart2 a");
                        for (Element el : konvEl) {
                            mKonviktItems.add(new String[]{el.text().replace(".pdf", ""), el.attr("href")});
                        }
                    }

                    final FoodAdapter adMensa = new FoodAdapter(getActivity(), mMensaItems, FoodFragment.this, FoodAdapter.TYPE_MENSA);
                    final FoodAdapter adKonvikt = new FoodAdapter(getActivity(), mKonviktItems, FoodFragment.this, FoodAdapter.TYPE_KONVIKT);

                    mMensa.post(new Runnable() {
                        @Override
                        public void run() {
                            mMensa.setAdapter(adMensa);
                            mKonvikt.setAdapter(adKonvikt);
                            mErrorText.setVisibility(View.GONE);
                            mFoodContainer.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (IOException e) {
                    mErrorText.post(new Runnable() {
                        @Override
                        public void run() {
                            mFoodContainer.setVisibility(View.GONE);
                            mErrorText.setVisibility(View.VISIBLE);
                        }
                    });
                } finally {
                    mSwipeContainer.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeContainer.setRefreshing(false);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("mensa", mMensaItems);
        outState.putSerializable("konvikt", mKonviktItems);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(View v, final String url) {
        mPendingURL = url;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            downloadMenu(url);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadMenu(mPendingURL);
        }
    }

    private void downloadMenu(String rawUrl) {
        // Matcher to split filename from rest of URL
        Pattern pattern = Pattern.compile("(.*/)(.*)");
        Matcher matcher = pattern.matcher(rawUrl);
        if (matcher.find()) {
            String path = matcher.group(1);
            String filename = matcher.group(2);
            // Encode the filename to convert illegal characters (e.g. ä, ü, etc.)
            final String url = path + URLEncoder.encode(filename).replace("+", "%20");
            if (Environment.getExternalStorageState().contentEquals(Environment.MEDIA_MOUNTED)) {
                File localDest = new File(Environment.getExternalStorageDirectory(), "/Kantidroid/menus");
                localDest.mkdirs();
                final File downloadFile = new File(localDest + "/" + "Menu.pdf");
                mSwipeContainer.setRefreshing(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Util.isNetworkAvailable(getActivity())) {
                            if (Util.urlExists(url)) {
                                Ion.with(getActivity())
                                        .load(url)
                                        .write(downloadFile)
                                        .setCallback(new FutureCallback<File>() {
                                            @Override
                                            public void onCompleted(final Exception e, final File file) {
                                                if (e != null) {
                                                    mSwipeContainer.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                            mSwipeContainer.setRefreshing(false);
                                                        }
                                                    });
                                                } else {
                                                    mSwipeContainer.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mSwipeContainer.setRefreshing(false);

                                                            MimeTypeMap myMime = MimeTypeMap.getSingleton();
                                                            Intent newIntent = new Intent(Intent.ACTION_VIEW);
                                                            String mimeType = myMime.getMimeTypeFromExtension(Util.fileExt(downloadFile).substring(1));
                                                            newIntent.setDataAndType(FileProvider.getUriForFile(getContext(), "com.martin.fileprovider", downloadFile), mimeType);
                                                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                            try {
                                                                getActivity().startActivity(newIntent);
                                                            } catch (ActivityNotFoundException e) {
                                                                Toast.makeText(getActivity(), R.string.timetable_noreader, Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                            } else {
                                mSwipeContainer.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), R.string.food_no_such_file, Toast.LENGTH_SHORT).show();
                                        mSwipeContainer.setRefreshing(false);
                                    }
                                });
                            }
                        } else {
                            mSwipeContainer.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), R.string.timetable_noconnection, Toast.LENGTH_SHORT).show();
                                    mSwipeContainer.setRefreshing(false);
                                }
                            });
                        }
                    }
                }).start();
            } else {
                Toast.makeText(getActivity(), R.string.timetable_error_filesystem, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity(), R.string.food_no_such_file, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefresh() {
        updateMenus(true);
    }
}
