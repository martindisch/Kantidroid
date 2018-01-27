package com.martin.kantidroid.ui.food;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private ArrayList<String[]> mEntries;
    private OnClickListener mCallback;
    private Context mContext;
    private int mType;

    public static final int TYPE_MENSA = 0;
    public static final int TYPE_KONVIKT = 1;

    public FoodAdapter(Context context, ArrayList<String[]> entries, OnClickListener callback, int type) {
        mContext = context;
        mEntries = entries;
        mCallback = callback;
        mType = type;
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
        holder.tvName.setText(mEntries.get(position)[0]);
        if (mType == TYPE_MENSA) {
            holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.mensa), PorterDuff.Mode.SRC_ATOP);
            holder.tvPic.setText("M");
        } else if (mType == TYPE_KONVIKT) {
            holder.tvPic.getBackground().setColorFilter(mContext.getResources().getColor(R.color.konvikt), PorterDuff.Mode.SRC_ATOP);
            holder.tvPic.setText("K");
        }
        holder.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemClick(view, mEntries.get(holder.getAdapterPosition())[1]);
            }
        });
    }

    public interface OnClickListener {
        void onItemClick(View v, final String URL);
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

    public List<String[]> getData() {
        return mEntries;
    }
}
