package com.martin.kantidroid.ui.overview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
    private Fach mTempfach;
    private int mTempColor;

    public OverviewAdapter(List<Fach> entries, OnClickListener callback) {
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
        mTempfach = mEntries.get(position);
        holder.tvName.setText(mTempfach.getName());
        // TODO: Ask user for semester, save it in SharedPreferences and load accordingly for kont and grades
        holder.tvKont.setText(mTempfach.getKont1());
        holder.tvGrades.setText(mTempfach.getMathAverage1());
        holder.tvPic.setText(mTempfach.getShort());
        mTempColor = Color.parseColor(mTempfach.getColor());
        holder.tvGrades.setBackgroundColor(mTempColor);
        holder.tvName.setBackgroundColor(Util.getDark(mTempColor));
        holder.tvKont.setBackgroundColor(Util.getDark(mTempColor));
        holder.rlPic.setBackgroundColor(Util.getLight(mTempColor));

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
        public RelativeLayout rlPic, rlItem;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            tvGrades = (TextView) v.findViewById(R.id.tvGrades);
            tvKont = (TextView) v.findViewById(R.id.tvKont);
            rlPic = (RelativeLayout) v.findViewById(R.id.rlPic);
            rlItem = (RelativeLayout) v.findViewById(R.id.rlItem);
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
