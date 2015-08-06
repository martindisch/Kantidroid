package com.martin.kantidroid.ui.timetable;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private ArrayList<File> mEntries;
    private OnClickListener mCallback;
    private Context mContext;

    public TimetableAdapter(Context context, File[] entries, OnClickListener callback) {
        mContext = context;
        mEntries = new ArrayList<>(Arrays.asList(entries));
        sort();
        mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timetable_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(mEntries.get(position).getName());
        holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.material_blue_grey_800), PorterDuff.Mode.SRC_ATOP);
        holder.tvPic.setText(mEntries.get(position).getName().substring(0, 1));
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
    }

    public interface OnClickListener {
        void onItemClick(View v, final int position);

        void onItemLongClick(View v, final int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvPic;
        public final View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public void add(File pdf) {
        mEntries.add(pdf);
        sort();
        int newPos = mEntries.indexOf(pdf);
        notifyItemInserted(newPos);
    }

    public void remove(int pos) {
        mEntries.remove(pos);
        notifyItemRemoved(pos);
    }

    private void sort() {
        Collections.sort(mEntries, new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                return f1.getName().compareTo(f2.getName());
            }
        });
    }

    public List<File> getData() {
        return mEntries;
    }
}
