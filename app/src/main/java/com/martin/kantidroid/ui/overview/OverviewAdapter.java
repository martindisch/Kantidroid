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
    private OnClickListener mCallback;
    private Fach mTempFach;
    private String mTempGrades, mTempKontUs, mTempKontAv;
    private int mTempSemester;
    private Context mContext;

    public OverviewAdapter(Context context, List<Fach> entries, OnClickListener callback) {
        mContext = context;
        mEntries = entries;
        mCallback = callback;
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
        mTempFach = mEntries.get(position);

        // TODO: Ask user for semester, save it in SharedPreferences and load accordingly for kont and grades
        mTempSemester = 1;
        if (mTempSemester == 1) {
            mTempGrades = mTempFach.getMathAverage1();
            mTempKontUs = mTempFach.getKont1();
        }
        else {
            mTempGrades = mTempFach.getMathAverage2();
            mTempKontUs = mTempFach.getKont2();
        }
        mTempKontAv = mTempFach.getKont();

        if (mTempGrades.contentEquals("")) {
            mTempGrades = "-";
        }
        if (mTempKontUs.contentEquals("")) {
            mTempKontUs = "0";
        }
        if (mTempKontAv.contentEquals("")) {
            mTempKontAv = "0";
        }

        holder.tvName.setText(mTempFach.getName());
        holder.tvKont.setText(mTempKontUs + "/" + mTempKontAv);
        holder.tvGrades.setText(mTempGrades);
        holder.tvPic.setText(mTempFach.getShort());
        holder.tvGrades.setBackgroundColor(Util.getNormal(mContext, mTempFach.getColor()));
        holder.tvName.setBackgroundColor(Util.getDark(mContext, mTempFach.getColor()));
        holder.tvKont.setBackgroundColor(Util.getDark(mContext, mTempFach.getColor()));
        holder.rlPic.setBackgroundColor(Util.getLight(mContext, mTempFach.getColor()));

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
        public TextView tvName, tvPic, tvGrades, tvKont;
        public View rlPic, rlItem;

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

    public void update(Fach subject, int oldPos) {
        sort();
        int newPos = mEntries.indexOf(subject);
        notifyItemChanged(oldPos);
        if (oldPos != newPos) {
            notifyItemMoved(oldPos, newPos);
        }
    }

    private void sort() {
        Collections.sort(mEntries, new Comparator<Fach>() {

            @Override
            public int compare(Fach fach, Fach fach2) {
                return fach.getName().compareTo(fach2.getName());
            }
        });
    }

    public List<Fach> getData() {
        return mEntries;
    }
}
