package com.martin.kantidroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerAdapter extends ArrayAdapter<NavDrawerItem> {

    private Context mContext;
    private NavDrawerItem[] mItems;
    private int mSelected = 0;

    public NavigationDrawerAdapter(Context context, NavDrawerItem[] items) {
        super(context, R.layout.drawer_item, items);
        mContext = context;
        mItems = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.drawer_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.text1);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon1);
        textView.setText(mItems[position].getText());
        imageView.setImageResource(mItems[position].getIcon());
        if (position == mSelected) {
            rowView.setBackgroundColor(getContext().getResources().getColor(R.color.divider));
        }
        return rowView;
    }

    public void selectItem(int position) {
        mSelected = position;
    }
}
