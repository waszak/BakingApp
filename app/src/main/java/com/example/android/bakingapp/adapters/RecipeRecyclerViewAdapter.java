package com.example.android.bakingapp.adapters;

import android.app.Activity;
import android.os.Parcelable;

import com.example.android.bakingapp.models.Recipe;
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Waszak on 30.09.2017.
 */
public class RecipeRecyclerViewAdapter
        extends ListDelegationAdapter<List<Parcelable>> {

    public RecipeRecyclerViewAdapter(Activity activity, Recipe recipe) {
        delegatesManager.addDelegate( new IngredientItemAdapterDelegate(activity));
        delegatesManager.addDelegate( new StepItemAdapterDelegate(activity));
        List<Parcelable> parcelableList = new ArrayList<>();
        parcelableList.addAll(recipe.getIngredients());
        parcelableList.addAll(recipe.getSteps());
        setItems(parcelableList);
    }
}
