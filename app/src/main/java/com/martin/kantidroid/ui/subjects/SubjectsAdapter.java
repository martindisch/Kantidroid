package com.martin.kantidroid.ui.subjects;

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

public class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.ViewHolder> {

    private List<Fach> mEntries;
    private OnClickListener mCallback;
    private Context mContext;

    public SubjectsAdapter(Context context, List<Fach> entries, OnClickListener callback) {
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subjects_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(mEntries.get(position).getName());
        holder.tvPic.getBackground().setColorFilter(Util.getNormal(mContext, mEntries.get(position).getColor()), PorterDuff.Mode.SRC_ATOP);
        holder.tvPic.setText(mEntries.get(position).getShort().substring(0, 1));
        holder.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setTag(1);
                mCallback.onItemClick(view, holder.getAdapterPosition());
            }
        });
    }

    public interface OnClickListener {
        void onItemClick(View v, int position);
        void onItemLongClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvPic;
        public View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public void add(Fach subject) {
        mEntries.add(subject);
        sort();
        int newPos = mEntries.indexOf(subject);
        notifyItemInserted(newPos);
    }

    public void update(Fach subject, int oldPos) {
        sort();
        int newPos = mEntries.indexOf(subject);
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
