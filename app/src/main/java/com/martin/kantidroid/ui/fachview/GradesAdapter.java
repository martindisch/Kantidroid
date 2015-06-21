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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GradesAdapter extends RecyclerView.Adapter<GradesAdapter.ViewHolder> {

    private ArrayList<String> mEntries;
    private OnClickListener mCallback;
    private Context mContext;
    private String[] mTempSplit;
    private String mDiff1, mDiff2;

    public GradesAdapter(Context context, ArrayList<String> entries, OnClickListener callback) {
        mContext = context;
        mEntries = entries;
        mCallback = callback;
        sort();
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fachview_grades_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.tvGrade.setText(mEntries.get(position).split(" - ")[0]);
            holder.tvWeight.setText(mEntries.get(position).split(" - ")[1]);
            holder.tvDate.setText(mEntries.get(position).split(" - ")[2]);
            holder.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.onItemClick(view, holder.getAdapterPosition());
                }
            });
            holder.rlRoot.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mCallback.onItemLongClick(view, holder.getAdapterPosition());
                    return false;
                }
            });
            holder.rlRoot.setBackgroundResource(R.drawable.btn_flat_selector);
    }

    public interface OnClickListener {
        void onItemClick(View v, final int position);

        void onItemLongClick(View v, final int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvGrade, tvWeight, tvDate;
        public View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvGrade = (TextView) v.findViewById(R.id.tvGrade);
            tvWeight = (TextView) v.findViewById(R.id.tvWeight);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public void add(String object) {
        mEntries.add(object);
        sort();
        int newPos = mEntries.indexOf(object);
        notifyItemInserted(newPos);
    }

    public void update(String object, int oldPos) {
        sort();
        int newPos = mEntries.indexOf(object);
        notifyItemChanged(oldPos);
        if (oldPos != newPos) {
            notifyItemMoved(oldPos, newPos);
        }
    }

    public void remove(int pos) {
        mEntries.remove(pos);
        notifyItemRemoved(pos);
    }

    private void sort() {
        Collections.sort(mEntries, new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                mTempSplit = first.split(" - ")[2].split("\\.");
                mDiff1 = mTempSplit[2] + mTempSplit[1] + mTempSplit[0];
                mTempSplit = second.split(" - ")[2].split("\\.");
                mDiff2 = mTempSplit[2] + mTempSplit[1] + mTempSplit[0];
                return mDiff1.compareTo(mDiff2);
            }
        });
    }

    public ArrayList<String> getData() {
        return mEntries;
    }

    public void setData(ArrayList<String> newData) {
        mEntries = newData;
    }
}
