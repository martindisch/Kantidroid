package com.martin.kantidroid.ui.about;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.kantidroid.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private String[] mTitles, mSubTitles, mLinks;
    private int[] mDrawables;

    public AboutAdapter(Context context) {
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feedback_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvTitle.setText(mTitles[position]);
        holder.ivIcon.setImageResource(mDrawables[position]);
        holder.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemClick(view, position);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvTitle;
        public final ImageView ivIcon;
        public final View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, final int position);
    }
}
