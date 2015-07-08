package com.martin.kantidroid.ui.backup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.kantidroid.R;

public class BackupAdapter extends RecyclerView.Adapter<BackupAdapter.ViewHolder> {

    private String[] mTitles, mMessages;
    private int[] mDrawables;
    private final OnClickListener mCallback;

    public BackupAdapter(Context context, OnClickListener callback) {
        mTitles = new String[]{context.getString(R.string.backup_title_1), context.getString(R.string.backup_title_2)};
        mMessages = new String[]{context.getString(R.string.backup_msg_1), context.getString(R.string.backup_msg_2)};
        mDrawables = new int[]{R.drawable.ic_backup, R.drawable.ic_import};
        mCallback = callback;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backup_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvTitle.setText(mTitles[position]);
        holder.tvMessage.setText(mMessages[position]);
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
        public final TextView tvMessage;
        public final ImageView ivIcon;
        public final View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvMessage = (TextView) v.findViewById(R.id.tvMessage);
            ivIcon = (ImageView) v.findViewById(R.id.ivIcon);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public interface OnClickListener {
        void onItemClick(View v, final int position);
    }
}
