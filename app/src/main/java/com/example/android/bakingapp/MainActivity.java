package com.example.android.bakingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.util.Log;

import com.example.android.bakingapp.adapters.RecipeListAdapter;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.utilities.RecipeService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recipe_list) RecyclerView mMovieList;

    private LayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mLayoutManager = new LinearLayoutManager(this);

        mMovieList.setLayoutManager(mLayoutManager);
        mMovieList.setHasFixedSize(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://d17h27t6h515a5.cloudfront.net/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        RecipeService recipeService = retrofit.create(RecipeService.class);
        recipeService.loadRecipeListing().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    if (response.isSuccessful()) {
                        // tasks available
                        RecipeListAdapter adapter = new RecipeListAdapter();
                        adapter.setRecipesList(new ArrayList<>(response.body()));
                        mMovieList.setAdapter(adapter);
                    } else {
                        // error response, no access to resource?
                    }
                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Log.d("Error", t.getMessage());
                }
            });


    }
}
