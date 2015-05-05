package com.martin.kantidroid;

import android.content.Context;
import android.content.SharedPreferences;

public class Check {
    public boolean getSeen(String ClassName, Context context) {
        SharedPreferences sp = context.getSharedPreferences("check", Context.MODE_PRIVATE);
        return sp.getBoolean(ClassName, false);
    }

    public void setSeen(String ClassName, Context context) {
        SharedPreferences sp = context.getSharedPreferences("check", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ClassName, true);
        editor.commit();
    }
}
