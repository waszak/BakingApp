package com.example.android.bakingapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.bakingapp.models.Recipe;

/**
 * Created by Waszak on 01.10.2017.
 */

public class RecipePreferences {
    public static final String PREF_RECIPE_ID = "recipe_to_check";
    public static void setRecipeId(Context context, Recipe recipe) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PREF_RECIPE_ID, recipe.getId());
        editor.apply();
    }

    public static int getRecipeId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(PREF_RECIPE_ID,-1);
    }

}
