package com.example.android.bakingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.android.bakingapp.adapters.RecipeRecyclerViewAdapter;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Step;
import com.example.android.bakingapp.utilities.RecipePreferences;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Ingriedients. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link IngredientDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class IngredientListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    @BindBool(R.bool.two_pane) boolean mTwoPane;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.ingredient_list) RecyclerView mRecyclerView;

    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;
    private Recipe mRecipe;
    private Step mStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        mFloatingActionButton.setOnClickListener(view -> {
            RecipePreferences.setRecipeId(this,mRecipe);
            Intent intent = new Intent(this, RecipeWidget.class);
            intent.setAction(WidgetProvider.RECIPE_CHANGED);
            sendBroadcast(intent);
        });

        onRestoreInstanceState(savedInstanceState);
        setupRecyclerView(mRecyclerView);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Intent intent = getIntent();
        if(intent != null && intent.getExtras() != null &&
                intent.getExtras().containsKey(Recipe.TAG)) {
            mRecipe = intent.getExtras().getParcelable(Recipe.TAG);
        }
        if( mRecipe == null) {
            throw new IllegalArgumentException("Pass recipe");
        }

        recyclerView.setAdapter(new RecipeRecyclerViewAdapter(this, mRecipe, mTwoPane));
        if( intent != null && intent.getExtras() != null &&
                intent.getExtras().containsKey(Step.TAG)){
            Bundle arguments = new Bundle();
            mStep =  intent.getExtras().getParcelable(Step.TAG);
            if(mTwoPane) {
                arguments.putParcelable(Step.TAG, mStep);
                arguments.putParcelable(Recipe.TAG, mRecipe);
                IngredientDetailFragment fragment = new IngredientDetailFragment();
                fragment.setArguments(arguments);
                this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ingriedient_detail_container, fragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            return;
        }
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(Recipe.TAG)){
            mRecipe = savedInstanceState.getParcelable(Recipe.TAG);
        } if(savedInstanceState.containsKey(Step.TAG)){
            mStep = savedInstanceState.getParcelable(Step.TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mRecipe != null){
            outState.putParcelable(Recipe.TAG, mRecipe);
        }if(mStep != null){
            outState.putParcelable(Step.TAG, mStep);
        }
        super.onSaveInstanceState(outState);
    }
}
