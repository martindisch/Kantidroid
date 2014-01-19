package com.martin.kantidroid;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.app.Fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FoodFragment extends Fragment {

	private Typeface rLight, rThin;
	private TextView tvDay, tvMensa, tvMensamenu, tvBodmer, tvBodmermenu,
			tvKonvikt, tvKonviktmenu;
	private RestsLoaded mCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.foodfragment, container, false);

		rLight = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Light.ttf");
		rThin = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Thin.ttf");

		tvDay = (TextView) view.findViewById(R.id.tvFoodDay);
		tvMensa = (TextView) view.findViewById(R.id.tvFoodMensa);
		tvMensamenu = (TextView) view.findViewById(R.id.tvFoodMensamenu);
		tvBodmer = (TextView) view.findViewById(R.id.tvFoodBodmer);
		tvBodmermenu = (TextView) view.findViewById(R.id.tvFoodBodmermenu);
		tvKonvikt = (TextView) view.findViewById(R.id.tvFoodKonvikt);
		tvKonviktmenu = (TextView) view.findViewById(R.id.tvFoodKonviktmenu);

		tvDay.setTypeface(rThin);
		tvDay.setText(getArguments().getString("day"));
		tvMensa.setTypeface(rLight, Typeface.BOLD);
		tvMensamenu.setTypeface(rLight);
		tvBodmer.setTypeface(rLight, Typeface.BOLD);
		tvBodmermenu.setTypeface(rLight);
		tvKonvikt.setTypeface(rLight, Typeface.BOLD);
		tvKonviktmenu.setTypeface(rLight);

		String[][] crappers = mCallback.getMenus();
		tvMensamenu.setText(crappers[getArguments().getInt(
				"position")][0]);
		tvBodmermenu.setText(crappers[getArguments().getInt(
				"position")][1]);
		tvKonviktmenu.setText(crappers[getArguments().getInt(
				"position")][2]);
		
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		
	}


	@Override
	public void onAttach(org.holoeverywhere.app.Activity activity) {
		super.onAttach(activity);
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (RestsLoaded) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement RestsLoaded");
		}
	}

}
