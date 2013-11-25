package com.martin.noten;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.app.DialogFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.martin.kantidroid.R;

public class SortFragment extends DialogFragment implements OnClickListener {

	int selected;
<<<<<<< HEAD
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
		selected = settings.getInt("sorting", 0);
		
=======

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences("MarkSettings",
				Context.MODE_PRIVATE);
		selected = settings.getInt("sorting", 0);

>>>>>>> beta
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("Sortieren");
		builder.setSingleChoiceItems(R.array.sorting_entries, selected, this);
		builder.setNeutralButton("Abbrechen", null);
		builder.setPositiveButton("OK", this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == -1) {
<<<<<<< HEAD
			SharedPreferences settings = getSharedPreferences("MarkSettings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			
			editor.putInt("sorting", selected);
			editor.commit();
			
		}
		else {
			selected = which;
		}		
	}

	
=======
			SharedPreferences settings = getSharedPreferences("MarkSettings",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();

			editor.putInt("sorting", selected);
			editor.commit();

		} else {
			selected = which;
		}
	}

>>>>>>> beta
}
