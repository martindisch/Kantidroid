package com.martin.noten;

import java.util.List;
import java.util.Map;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.martin.kantidroid.R;

public class MyAdapter extends SimpleAdapter {

	private final Activity context;
	List<? extends Map<String, ?>> list;

	public MyAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = (Activity) context;
		this.list = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		LayoutInflater inflater = context.getLayoutInflater();
		rowView = inflater.inflate(R.layout.overview_list_item, null);
		TextView left = (TextView) rowView.findViewById(R.id.tvLeft);
		TextView right = (TextView) rowView.findViewById(R.id.tvRight);

		left.setText(list.get(position).get("fach").toString());
		right.setText(list.get(position).get("anzahl").toString());

		String mark = list.get(position).get("anzahl").toString();

		if (!mark.contentEquals("-")) {
			mark = mark.replace(",", ".");
			if (Double.parseDouble(mark) < 3.75) {
				left.setTextColor(Color.RED);
				right.setTextColor(Color.RED);
			}
		}

		return rowView;
	}
}
