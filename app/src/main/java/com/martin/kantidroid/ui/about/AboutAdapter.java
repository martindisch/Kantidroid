package com.martin.kantidroid.ui.about;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Util;

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private String[] mTitles, mSubTitles, mLinks;
    private TypedArray mDrawables;
    private Resources mRes;
    private Context mContext;

    public AboutAdapter(Context context) {
        mContext = context;
        mRes = context.getResources();
        mTitles = mRes.getStringArray(R.array.about_titles);
        mSubTitles = mRes.getStringArray(R.array.about_subtitles);
        mLinks = mRes.getStringArray(R.array.about_links);
        mDrawables = mRes.obtainTypedArray(R.array.about_drawables);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        final int position = holder.getAdapterPosition();
        switch (holder.getItemViewType()) {
            case 0:
                ViewHolderHeader vh0 = (ViewHolderHeader) holder;
                vh0.tvTitle.setText(mTitles[position]);
                break;
            case 1:
                ViewHolderLetter vh1 = (ViewHolderLetter) holder;
                vh1.tvTitle.setText(mTitles[position]);
                vh1.tvSubTitle.setText(mSubTitles[position]);
                vh1.tvPic.getBackground().setColorFilter(Util.getSomeColor(mRes, position), PorterDuff.Mode.SRC_ATOP);
                vh1.tvPic.setText(mTitles[position].charAt(0) + "");
                break;
            default:
                ViewHolderIcon vh2 = (ViewHolderIcon) holder;
                vh2.tvTitle.setText(mTitles[position]);
                vh2.tvSubTitle.setText(mSubTitles[position]);
                vh2.ivPic.setImageDrawable(mDrawables.getDrawable(position));
                break;
        }
        ((ViewHolderBase) holder).rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mLinks[position].contentEquals("null")) {
                    if (mLinks[position].contains("http")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mLinks[position]));
                        mContext.startActivity(browserIntent);
                    } else {
                        Intent i = new Intent(mContext, LicenseActivity.class);
                        i.putExtra("license", mLinks[position]);
                        mContext.startActivity(i);
                    }
                }
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
        int d = mDrawables.getResourceId(position, -1);
        switch (d) {
            case R.integer.about_header:
                viewType = 0;
                break;
            case R.integer.about_letter:
                viewType = 1;
                break;
            default:
                viewType = 2;
                break;
        }
        return viewType;
    }
}
