package com.martin.kantidroid.ui.about;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.kantidroid.R;

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] mTitles, mSubTitles, mLinks;
    private int[] mDrawables;
    private Resources mRes;

    public AboutAdapter(Context context) {
        mRes = context.getResources();
        mTitles = mRes.getStringArray(R.array.about_titles);
        mSubTitles = mRes.getStringArray(R.array.about_subtitles);
        mLinks = mRes.getStringArray(R.array.about_links);
        mDrawables = mRes.getIntArray(R.array.about_drawables);
    }

    @Override
    public int getItemCount() {
        return mTitles.length;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        RecyclerView.ViewHolder vh;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.about_header, viewGroup, false);
                vh = new ViewHolderHeader(v);
                break;
            case 1:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.about_letter, viewGroup, false);
                vh = new ViewHolderLetter(v);
                break;
            default:
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.about_drawable, viewGroup, false);
                vh = new ViewHolderIcon(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderHeader vh0 = (ViewHolderHeader) holder;
                break;
            case 1:
                ViewHolderLetter vh1 = (ViewHolderLetter) holder;
                break;
            default:
                ViewHolderIcon vh2 = (ViewHolderIcon) holder;
                break;
        }
        ((ViewHolderBase) holder).rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open link
            }
        });
    }

    public static class ViewHolderBase extends RecyclerView.ViewHolder {
        public View rlRoot;

        public ViewHolderBase(View v) {
            super(v);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public static class ViewHolderHeader extends ViewHolderBase {
        public TextView tvTitle;

        public ViewHolderHeader(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        }
    }

    public static class ViewHolderIcon extends ViewHolderBase {
        public TextView tvTitle, tvSubTitle;
        public ImageView ivPic;

        public ViewHolderIcon(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) v.findViewById(R.id.tvSubtitle);
            ivPic = (ImageView) v.findViewById(R.id.ivPic);
        }
    }

    public static class ViewHolderLetter extends ViewHolderBase {
        public final TextView tvTitle, tvSubTitle, tvPic;

        public ViewHolderLetter(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tvTitle);
            tvSubTitle = (TextView) v.findViewById(R.id.tvSubtitle);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        switch (mDrawables[position]) {
            case 0:
                viewType = 0;
                break;
            case 1:
                viewType = 1;
                break;
            default:
                viewType = 2;
                break;
        }
        return viewType;
    }
}
