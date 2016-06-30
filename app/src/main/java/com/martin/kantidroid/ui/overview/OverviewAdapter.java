package com.martin.kantidroid.ui.overview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Fach;
import com.martin.kantidroid.logic.Util;
import com.martin.kantidroid.ui.util.ItemTouchHelperAdapter;
import com.martin.kantidroid.ui.util.OnStartDragListener;

import java.util.Collections;
import java.util.List;

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private List<Fach> mEntries;
    private final OnClickListener mCallback;
    private final OnStartDragListener mDragCallback;
    private final int mTempSemester;
    private final Context mContext;
    private String mTempGrades, mTempKontUs, mTempKontAv;
    private boolean mBad;
    private Fach mTempFach;

    public OverviewAdapter(Context context, List<Fach> entries, OnClickListener callback, OnStartDragListener dragCallback, int semester) {
        mContext = context;
        mEntries = entries;
        mCallback = callback;
        mDragCallback = dragCallback;
        mTempSemester = semester;
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
        mTempFach = mEntries.get(position);
        mBad = false;
        if (mTempSemester == 1) {
            mTempGrades = mTempFach.getMathAverage1();
            mTempKontUs = mTempFach.getKont1();
        } else {
            mTempGrades = mTempFach.getMathAverage2();
            mTempKontUs = mTempFach.getKont2();
        }
        mTempKontAv = mTempFach.getKontAvailable(mContext, true);

        if (mTempGrades.contentEquals("")) {
            mTempGrades = "-";
        } else if (Double.parseDouble(mTempGrades) < 3.75) {
            mBad = true;
        }

        holder.tvName.setText(mTempFach.getName());
        holder.tvKont.setText(Util.formatKont(mTempKontUs, mTempKontAv));
        holder.tvGrades.setText(mTempGrades);
        holder.tvPic.setText(mTempFach.getShort());
        if (mBad) {
            holder.tvGrades.setBackgroundColor(mContext.getResources().getColor(R.color.red_dark));
            holder.tvName.setBackgroundColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.promo_black));
            holder.tvKont.setBackgroundColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvKont.setTextColor(mContext.getResources().getColor(R.color.promo_black));
            holder.tvPic.setBackgroundColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvPic.setTextColor(mContext.getResources().getColor(R.color.promo_black));
        } else {
            holder.tvGrades.setBackgroundColor(Util.getNormal(mContext, mTempFach.getColor()));
            holder.tvName.setBackgroundColor(Util.getDark(mContext, mTempFach.getColor()));
            holder.tvName.setTextColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvKont.setBackgroundColor(Util.getDark(mContext, mTempFach.getColor()));
            holder.tvKont.setTextColor(mContext.getResources().getColor(R.color.promo_white));
            holder.tvPic.setBackgroundColor(Util.getLight(mContext, mTempFach.getColor()));
            holder.tvPic.setTextColor(mContext.getResources().getColor(R.color.promo_white));
        }

        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemClick(holder.getAdapterPosition());
            }
        });
        holder.rlItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallback.onLongItemClick(holder.getAdapterPosition());
                return true;
            }
        });
        holder.rlItem.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragCallback.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition > toPosition) {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mEntries, i, i - 1);
            }
        } else {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mEntries, i, i + 1);
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public interface OnClickListener {
        void onItemClick(int position);
        void onLongItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final TextView tvPic;
        public final TextView tvGrades;
        public final TextView tvKont;
        public final View rlItem;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvPic = (TextView) v.findViewById(R.id.tvPic);
            tvGrades = (TextView) v.findViewById(R.id.tvGrades);
            tvKont = (TextView) v.findViewById(R.id.tvKont);
            rlItem = v.findViewById(R.id.rlItem);
        }
    }

    public List<Fach> getData() {
        return mEntries;
    }

    public void setData(List<Fach> faecher) {
        mEntries = faecher;
    }
}
