package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.android.bakingapp.adapters.RecipeRecyclerViewAdapter;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Step;

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
    private Recipe mRecipe;
    private Step mStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
        if(intent != null && intent.getExtras() != null &&
                intent.getExtras().containsKey(Step.TAG)){
            Bundle arguments = new Bundle();
            mStep =  intent.getExtras().getParcelable(Step.TAG);
            arguments.putParcelable(Step.TAG, mStep);
            arguments.putParcelable(Recipe.TAG, mRecipe);
            IngredientDetailFragment fragment = new IngredientDetailFragment();
            fragment.setArguments(arguments);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ingriedient_detail_container, fragment)
                    .commit();
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
