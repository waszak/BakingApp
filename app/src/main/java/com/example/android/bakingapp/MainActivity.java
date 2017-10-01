package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;

import com.example.android.bakingapp.adapters.RecipeListAdapter;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.example.android.bakingapp.utilities.RecipeService;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.RecipeListOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.recipe_list) RecyclerView mRecipeList;

    private LayoutManager mLayoutManager;
    private RecipeListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);
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

        RecipeService recipeService = NetworkUtils.buildRecipeService(this);
        recipeService.loadRecipeListing().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // tasks available
                        mAdapter.setRecipesList(response.body());

                    } else {
                        Timber.e("Error response, no access to resource");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                    // something went completely south (like no internet connection)
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
        //startChildActivityIntent.putExtra(MOVIES_ADAPTER_STATE, mMoviesAdapter.getList());
        //startActivityForResult(startChildActivityIntent, MOVIE_DETAILS_REQUEST);
        startActivity(startChildActivityIntent);
    }
}
