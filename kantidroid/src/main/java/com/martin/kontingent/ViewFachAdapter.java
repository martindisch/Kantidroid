package com.martin.kontingent;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.martin.kantidroid.R;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

class ViewFachAdapter extends BaseAdapter {

    private static LayoutInflater inflater;
    private final String[] sDates;
    private final String[] sUsages;
    private TextView tvLeft, tvRight;

    public ViewFachAdapter(Context context, String[] sDates, String[] sUsages) {
        super();
        this.sDates = sDates;
        this.sUsages = sUsages;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return sDates.length;
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
        tvRight.setText(sUsages[arg0]);
        return vi;
    }

}
