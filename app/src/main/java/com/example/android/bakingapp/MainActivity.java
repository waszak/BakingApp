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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.widget.Toast;

import com.example.android.bakingapp.adapters.RecipeListAdapter;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.utilities.ConnectivityReceiver;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.example.android.bakingapp.utilities.RecipeService;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.recipe_list) RecyclerView mRecipeList;
    @BindInt(R.integer.columns_recipes) int columns;

    private LayoutManager mLayoutManager;
    private RecipeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLayoutManager = new GridLayoutManager(this,columns);
        mRecipeList.setLayoutManager(mLayoutManager);
        mRecipeList.setHasFixedSize(true);
        mAdapter = new RecipeListAdapter(MainActivity.this);
        mAdapter.setRecipesList(new LinkedList<>());
        mRecipeList.setAdapter(mAdapter);
        onRestoreInstanceState(savedInstanceState);
        if(mAdapter.getItemCount() == 0) {
            fillRecipeList();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mAdapter.getRecipes() != null && mAdapter.getItemCount() != 0) {
            outState.putParcelableArrayList(TAG, mAdapter.getRecipes());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            return;
        }
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(TAG)){
            mAdapter.setRecipesList(savedInstanceState.getParcelableArrayList(TAG));
        }
    }

    private void fillRecipeList() {
        if(!ConnectivityReceiver.isConnected()){
            Toast.makeText(this, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
            return;
        }
        RecipeService recipeService = NetworkUtils.buildRecipeService(this);
        recipeService.loadRecipeListing().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // tasks available
                        mAdapter.setRecipesList(response.body());

                    } else {
                        Toast.makeText(MainActivity.this
                                ,getString(R.string.connection_failed),Toast.LENGTH_LONG).show();
                        Timber.e("Error response, no access to resource");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                    // something went completely south (like no internet connection)
                    Toast.makeText(MainActivity.this
                            ,getString(R.string.connection_failed),Toast.LENGTH_LONG).show();
                    Timber.d(t,"Error");
                }
            });
    }

    @Override
    public void onClick(Recipe recipe) {
        Context context = MainActivity.this;
        Class destinationActivity = IngredientListActivity.class;
        Intent startChildActivityIntent = new Intent(context, destinationActivity);
        startChildActivityIntent.putExtra(Recipe.TAG, recipe);
        startActivity(startChildActivityIntent);
    }
}
