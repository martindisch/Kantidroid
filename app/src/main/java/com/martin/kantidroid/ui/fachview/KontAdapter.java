package com.martin.kantidroid.ui.fachview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class KontAdapter extends RecyclerView.Adapter<KontAdapter.ViewHolder> {

    private ArrayList<String> mEntries;
    private OnClickListener mCallback;
    private Context mContext;

    public KontAdapter(Context context, ArrayList<String> entries, OnClickListener callback) {
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fachview_kont_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvDate.setText(mEntries.get(position).split(" - ")[0]);
        holder.tvPic.setText(mEntries.get(position).split(" - ")[1]);
        holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.green_dark), PorterDuff.Mode.SRC_ATOP);
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
        public TextView tvDate, tvPic;
        public View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvDate = (TextView) v.findViewById(R.id.tvDate);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
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
        // TODO: This needs to properly sort dates
        Collections.sort(mEntries, new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                return first.split(" - ")[0].compareTo(second.split(" - ")[0]);
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
