package com.martin.kantidroid.ui.fachview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.Util;

import java.util.ArrayList;

public class CalcAdapter extends RecyclerView.Adapter<CalcAdapter.ViewHolder> {

    private final Fach mFach;
    private final int mSemester;
    private final double mZeugnis;
    private double[] mDPicGrades;
    private String mWeight = "1";
    private final Resources mRes;
    private double mRequired;
    String[] mEntries;

    public CalcAdapter(Context context, Fach fach, int semester) {
        mFach = fach;
        mSemester = semester;
        if (mSemester == 1) {
            mZeugnis = Double.parseDouble(fach.getRealAverage1());
        } else {
            mZeugnis = Double.parseDouble(fach.getRealAverage2());
        }
        mRes = context.getResources();
        mEntries = fach.getNotenEntries(semester);
        mDPicGrades = getPicGrades();
    }

    private double[] getPicGrades() {
        ArrayList<Double> lPicGrades = new ArrayList<>();
        final double[] allGrades = {1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5, 5.5, 6};
        double[] requiredGrades = new double[11];
        double goal = 0.75;
        for (int i = 0; i < 11; i++) {
            requiredGrades[i] = Util.getRequiredPerf(mEntries, mWeight, goal + "");
            goal += .5;
        }
        for (int i = 10; i >= 0; i--) {
            if (requiredGrades[i] >= 1 && requiredGrades[i] <= 6) {
                lPicGrades.add(allGrades[i]);
            }
        }
        return Util.convertDoubles(lPicGrades);
    }

    @Override
    public int getItemCount() {
        return mDPicGrades.length;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fachview_calc_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mZeugnis < mDPicGrades[position]) {
            holder.tvPic.getBackground().setColorFilter(mRes.getColor(R.color.red_dark), PorterDuff.Mode.SRC_ATOP);
            holder.tvName.setText(R.string.needed);
            holder.tvName.setTextColor(mRes.getColor(R.color.secondary_text_default_material_light));
        } else if (mZeugnis > mDPicGrades[position]) {
            holder.tvPic.getBackground().setColorFilter(mRes.getColor(R.color.promo_black), PorterDuff.Mode.SRC_ATOP);
            holder.tvName.setText(R.string.needed);
            holder.tvName.setTextColor(mRes.getColor(R.color.secondary_text_default_material_light));
        } else {
            holder.tvPic.getBackground().setColorFilter(mRes.getColor(R.color.highlight_light), PorterDuff.Mode.SRC_ATOP);
            holder.tvName.setTextColor(mRes.getColor(R.color.highlight_light));
            holder.tvName.setText(R.string.actual);
        }
        holder.tvPic.setText(mDPicGrades[position] + "");
        mRequired = Util.getRequiredPerf(mEntries, mWeight, mDPicGrades[position] - 0.25 + "");
        holder.tvMark.setText(mRequired + "");
        if (mRequired > 6 || mRequired < 1) {
            holder.tvMark.setTextColor(mRes.getColor(R.color.red_dark));
        } else {
            holder.tvMark.setTextColor(mRes.getColor(R.color.primary_text_default_material_light));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvPic;
        public final TextView tvMark;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            tvMark = (TextView) v.findViewById(R.id.tvMark);
        }
    }

    public void changeWeight(String weight) {
        mWeight = weight;
        mDPicGrades = getPicGrades();
    }
}
