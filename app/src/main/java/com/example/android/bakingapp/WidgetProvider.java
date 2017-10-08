/*
 *
 *  * Copyright (C) 2017. The Android Open Source Project
 *  *
 *  *   Licensed under the Apache License, Version 2.0 (the "License");
 *  *   you may not use this file except in compliance with the License.
 *  *   You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *   Unless required by applicable law or agreed to in writing, software
 *  *   distributed under the License is distributed on an "AS IS" BASIS,
 *  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *   See the License for the specific language governing permissions and
 *  *   limitations under the License.
 *  *
 *
 */

package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.models.Ingredient;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.example.android.bakingapp.utilities.RecipePreferences;
import com.example.android.bakingapp.utilities.RecipeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * Provider for ListView in widget
 */

public class WidgetProvider implements RemoteViewsService.RemoteViewsFactory {

    private List<Recipe> mDataSource;
    private Recipe mRecipe;
    private Context mContext;
    public final static String RECIPE_CHANGED = "RECIPE_CHANGED";
    public WidgetProvider(Context context, Intent intent){
        mContext = context;
    }

    /*
        Called when factory is created;
     */
    @Override
    public void onCreate() {
        Timber.d("Factory is created");
        onDataSetChanged();
    }


    @Override
    public void onDataSetChanged() {
        if (mDataSource == null || mDataSource.isEmpty()) {
            fillRecipeList();
            return;
        }
        Timber.d("list data changed");
        UpdateRecipe();
    }
    private void UpdateRecipe(){
        if (mDataSource == null || mDataSource.isEmpty()) {
            return;
        }
        int id = RecipePreferences.getRecipeId(mContext);
        Recipe recipeToShow = null;
        for (Recipe recipe: mDataSource) {
            recipeToShow = recipe; //in case there is nothing selected we show first one
            if(recipe.getId() == id){
                break;
            }
        }
        if(recipeToShow == null){
            return;
        }
        mRecipe = recipeToShow;
    }

    private void fillRecipeList() {

        RecipeService recipeService = NetworkUtils.buildRecipeService(mContext);
        recipeService.loadRecipeListing().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null && (mDataSource == null || mDataSource.isEmpty())) {
                    // tasks available
                    mDataSource = response.body();
                    Intent intent = new Intent(mContext, RecipeWidget.class);
                    intent.setAction(WidgetProvider.RECIPE_CHANGED);
                    mContext.sendBroadcast(intent);

                } else {
                    Timber.e("Error response, no access to resource");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Timber.d(t, "Error");
            }
        });

    }

    /**
     *
     * @param position of view we want to get
     * @return view at position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("Get view at position "+position);
        RemoteViews remoteViews =  new RemoteViews(mContext.getPackageName(),
                R.layout.ingredient_content_widget);
        Ingredient ingredient = mRecipe.getIngredients().get(position);

        remoteViews.setTextViewText(R.id.id, ingredient.getIngredient());
        remoteViews.setTextViewText(R.id.content, ingredient.getQuantity()+" "+ingredient.getMeasure());

        return remoteViews;
    }




    @Override
    public int getCount() {
        return mRecipe==null?0:mRecipe.getIngredients().size();
    }

    @Override
    public void onDestroy() {

    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * @return numbers of views returned by this provider
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     *
     * @param position in adapter
     * @return position of row in data set
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @return if false there are ItemId are unique.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
