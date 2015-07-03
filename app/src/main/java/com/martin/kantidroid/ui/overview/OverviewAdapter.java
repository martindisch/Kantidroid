package com.martin.kantidroid.ui.overview;

import android.content.Context;
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

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private List<Fach> mEntries;
    private final OnClickListener mCallback;
    private String mTempKontString;
    private final int mTempSemester;
    private final Context mContext;

    public OverviewAdapter(Context context, List<Fach> entries, OnClickListener callback, int semester) {
        mContext = context;
        mEntries = entries;
        mCallback = callback;
        mTempSemester = semester;
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.overview_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Fach mTempFach = mEntries.get(position);
        boolean mBad = false;

        String mTempKontUs;
        String mTempGrades;
        if (mTempSemester == 1) {
            mTempGrades = mTempFach.getMathAverage1();
            mTempKontUs = mTempFach.getKont1();
        } else {
            mTempGrades = mTempFach.getMathAverage2();
            mTempKontUs = mTempFach.getKont2();
        }
        String mTempKontAv = mTempFach.getKontAvailable();

        if (mTempGrades.contentEquals("")) {
            mTempGrades = "-";
        } else if (Double.parseDouble(mTempGrades) < 3.75) {
            mBad = true;
        }

        holder.tvName.setText(mTempFach.getName());
        holder.tvKont.setText(Util.formatKont(mTempKontUs, mTempKontAv));
        holder.tvGrades.setText(mTempGrades);
        holder.tvPic.setText(mTempFach.getShort());
        if (mBad) {
            holder.tvGrades.setBackgroundColor(mContext.getResources().getColor(R.color.red_dark));
            holder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.promo_black));
            holder.tvKont.setBackgroundColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvKont.setTextColor(mContext.getResources().getColor(R.color.promo_black));
            holder.rlPic.setBackgroundColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvPic.setTextColor(mContext.getResources().getColor(R.color.promo_black));
        } else {
            holder.tvGrades.setBackgroundColor(Util.getNormal(mContext, mTempFach.getColor()));
            holder.tvName.setBackgroundColor(Util.getDark(mContext, mTempFach.getColor()));
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvKont.setBackgroundColor(Util.getDark(mContext, mTempFach.getColor()));
            holder.tvKont.setTextColor(mContext.getResources().getColor(R.color.promo_white));
            holder.rlPic.setBackgroundColor(Util.getLight(mContext, mTempFach.getColor()));
            holder.tvPic.setTextColor(mContext.getResources().getColor(R.color.promo_white));
        }

        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemClick(view, holder.getAdapterPosition());
            }
        });
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);

        void onItemLongClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvPic;
        public final TextView tvGrades;
        public final TextView tvKont;
        public final View rlPic;
        public final View rlItem;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            tvGrades = (TextView) v.findViewById(R.id.tvGrades);
            tvKont = (TextView) v.findViewById(R.id.tvKont);
            rlPic = v.findViewById(R.id.rlPic);
            rlItem = v.findViewById(R.id.rlItem);
        }
    }

    public List<Fach> getData() {
        return mEntries;
    }

    public void setData(List<Fach> faecher) {
        mEntries = faecher;
    }
}
