package com.martin.kantidroid.ui.fachview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.Util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CalcAdapter extends RecyclerView.Adapter<CalcAdapter.ViewHolder> {

    private Fach mFach;
    private int mSemester;
    private String mZeugnis;
    private final String[] mPicGrades = { "6", "5.5", "5", "4.5", "4"};
    private Context mContext;

    public CalcAdapter(Context context, Fach fach, int semester) {
        mFach = fach;
        mSemester = semester;
        mContext = context;
        if (mSemester == 1) {
            mZeugnis = fach.getRealAverage1();
        }
        else {
            mZeugnis = fach.getRealAverage2();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fachview_calc_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (Double.parseDouble(mZeugnis) < Double.parseDouble(mPicGrades[position])) {
            holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.red_dark), PorterDuff.Mode.SRC_ATOP);
            holder.tvName.setText(R.string.needed);
        }
        else if (Double.parseDouble(mZeugnis) > Double.parseDouble(mPicGrades[position])) {
            holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.promo_black), PorterDuff.Mode.SRC_ATOP);
            holder.tvName.setText(R.string.needed);
        }
        else {
            holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.highlight_light), PorterDuff.Mode.SRC_ATOP);
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.highlight_light));
            holder.tvName.setText(R.string.actual);
        }
        holder.tvPic.setText(mPicGrades[position]);
        holder.rlRoot.setBackgroundResource(R.drawable.btn_flat_selector);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvPic, tvMark;
        public View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            tvMark = (TextView) v.findViewById(R.id.tvMark);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }
}
