package com.martin.kantidroid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FoodFragment extends Fragment {

	private Typeface rLight, rThin;
	private TextView tvDay, tvMensa, tvMensamenu, tvBodmer, tvBodmermenu, tvKonvikt, tvKonviktmenu, tvCafemartin, tvCafemartinmenu, tvDate;
	private RestsLoaded mCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.foodfragment, container, false);

		rLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
		rThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");

		tvDay = (TextView) view.findViewById(R.id.tvFoodDay);
		tvMensa = (TextView) view.findViewById(R.id.tvFoodMensa);
		tvMensamenu = (TextView) view.findViewById(R.id.tvFoodMensamenu);
		tvBodmer = (TextView) view.findViewById(R.id.tvFoodBodmer);
		tvBodmermenu = (TextView) view.findViewById(R.id.tvFoodBodmermenu);
		tvKonvikt = (TextView) view.findViewById(R.id.tvFoodKonvikt);
		tvKonviktmenu = (TextView) view.findViewById(R.id.tvFoodKonviktmenu);
		tvCafemartin = (TextView) view.findViewById(R.id.tvFoodCafemartin);
		tvCafemartinmenu = (TextView) view.findViewById(R.id.tvFoodCafemartinmenu);
		tvDate = (TextView) view.findViewById(R.id.tvFoodDate);

		tvDay.setTypeface(rThin);
		tvDay.setText(getArguments().getString("day"));
		tvMensa.setTypeface(rLight, Typeface.BOLD);
		tvMensamenu.setTypeface(rLight);
		tvBodmer.setTypeface(rLight, Typeface.BOLD);
		tvBodmermenu.setTypeface(rLight);
		tvKonvikt.setTypeface(rLight, Typeface.BOLD);
		tvKonviktmenu.setTypeface(rLight);
		tvCafemartin.setTypeface(rLight, Typeface.BOLD);
		tvCafemartinmenu.setTypeface(rLight);
		tvDate.setTypeface(rThin);

		String[][] crappers = mCallback.getMenus();

		try {
			for (int i = 0; i < crappers[getArguments().getInt("position")].length; i++) {
				if (crappers[getArguments().getInt("position")][i].contentEquals("null")) {
					crappers[getArguments().getInt("position")][i] = "Nicht verfügbar";
				}
			}
			// In case we got bullshit from getMenus()
		} catch (NullPointerException e) {
			getActivity().finish();
		}

		tvMensamenu.setText(crappers[getArguments().getInt("position")][0]);
		tvBodmermenu.setText(crappers[getArguments().getInt("position")][1]);
		tvKonviktmenu.setText(crappers[getArguments().getInt("position")][2]);
		tvCafemartinmenu.setText(crappers[getArguments().getInt("position")][3]);

		tvDate.setText(mCallback.getDates()[getArguments().getInt("position")]);

		return view;
	}

	@Override
	public void onAttach(org.holoeverywhere.app.Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (RestsLoaded) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement RestsLoaded");
		}
	}

}
