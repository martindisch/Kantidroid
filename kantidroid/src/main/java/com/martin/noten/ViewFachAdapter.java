package com.martin.noten;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.martin.kantidroid.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

public class ViewFachAdapter extends BaseAdapter {

    private static LayoutInflater inflater;
    private String[] sDates;
    private String[] sRelevances;
    private String[] sMarks;
    private TextView tvLeft, tvRight;

    public ViewFachAdapter(Context context, String[] sDates, String[] sRelevances, String[] sMarks) {
        super();
        this.sDates = sDates;
        this.sRelevances = sRelevances;
        this.sMarks = sMarks;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sMarks.length;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        View vi = arg1;
        if (vi == null)
            vi = inflater.inflate(R.layout.overview_list_item, null);
        tvLeft = (TextView) vi.findViewById(R.id.tvLeft);
        tvRight = (TextView) vi.findViewById(R.id.tvRight);
        tvLeft.setTextColor(Color.parseColor("#8e8e8e"));
        tvLeft.setText(sDates[arg0]);
        if (!sRelevances[arg0].contentEquals("1") && !(sRelevances[arg0].contentEquals("1.0"))) {
            tvRight.setText("(" + sRelevances[arg0] + ") " + sMarks[arg0]);
        } else {
            tvRight.setText(sMarks[arg0]);
        }
        if (Double.parseDouble(sMarks[arg0]) < 3.75) {
            tvRight.setTextColor(Color.parseColor("#cc0000"));
        }
        return vi;
    }

}
