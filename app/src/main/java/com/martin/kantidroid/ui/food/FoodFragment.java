package com.martin.kantidroid.ui.food;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martin.kantidroid.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class FoodFragment extends Fragment {

    public FoodFragment() {
        // Required empty public constructor
    }

    public static FoodFragment newInstance() {
        FoodFragment fragment = new FoodFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(getString(R.string.url_food)).get();
                    Elements myHrefs = doc.select(".WebPart a");
                    for (Element el : myHrefs) {
                        Log.e("FFF", String.format("Text: %s  -  URL: %s", el.text(), el.attr("href")));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.food_fragment, container, false);
    }

}
