package com.martin.kantidroid.ui.overview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.martin.kantidroid.R;
import com.martin.kantidroid.logic.Fach;

import java.util.List;

public class ZeugnisAdapter extends RecyclerView.Adapter<ZeugnisAdapter.ViewHolder> {

    private List<Fach> mEntries;

    public ZeugnisAdapter(List<Fach> entries) {
        mEntries = entries;
    }

    @Override
    public int getItemCount() {
        return mEntries.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.overview_zeugnis_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(mEntries.get(position).getName());
        holder.tvSem1.setText(mEntries.get(position).getRealAverage1());
        holder.tvSem2.setText(mEntries.get(position).getRealAverage2());
        holder.tvFinal.setText(mEntries.get(position).getZeugnis());
        holder.rlRoot.setBackgroundResource(R.drawable.btn_flat_selector);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvSem1, tvSem2, tvFinal;
        public View rlRoot;

        public ViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.tvName);
            tvSem1 = (TextView) v.findViewById(R.id.tvFirstSem);
            tvSem2 = (TextView) v.findViewById(R.id.tvSecondSem);
            tvFinal = (TextView) v.findViewById(R.id.tvFinal);
            rlRoot = v.findViewById(R.id.rlRoot);
        }
    }

    public List<Fach> getData() {
        return mEntries;
    }

    public void setData(List<Fach> faecher) {
        mEntries = faecher;
    }
}
