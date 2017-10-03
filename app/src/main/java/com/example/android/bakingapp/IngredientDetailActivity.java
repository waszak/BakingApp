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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Step;
import com.google.android.exoplayer2.C;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Ingriedient detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link IngredientListActivity}.
 */
public class IngredientDetailActivity extends AppCompatActivity {

    public static final String POSITION_VIDEO = "position_video";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    @BindBool(R.bool.two_pane) boolean mTwoPane;
    @BindView(R.id.detail_toolbar) Toolbar mToolbar;

    private Step mStep;
    private Recipe mRecipe;
    private long mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_detail);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mPosition = C.TIME_UNSET;

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Step.TAG) && intent.hasExtra(Recipe.TAG)) {
            mStep = intent.getExtras().getParcelable(Step.TAG);
            mRecipe = intent.getExtras().getParcelable(Recipe.TAG);
        }
        onRestoreInstanceState(savedInstanceState);
        if( mStep == null)
        {
            throw new IllegalArgumentException("Pass step");
        }
        if( mRecipe == null)
        {
            throw new IllegalArgumentException("Pass recipe");
        }

        if (mTwoPane) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Context context = IngredientDetailActivity.this;
            Class destinationActivity = IngredientListActivity.class;
            Intent startChildActivityIntent = new Intent(context, destinationActivity);
            startChildActivityIntent.putExtra(Step.TAG, mStep);
            startChildActivityIntent.putExtra(Recipe.TAG, mRecipe);
            startChildActivityIntent.putExtra(IngredientDetailActivity.POSITION_VIDEO, mPosition);
            startActivity(startChildActivityIntent);
        } else{
            Bundle arguments = new Bundle();
            arguments.putParcelable(Step.TAG, mStep);
            arguments.putParcelable(Recipe.TAG, mRecipe);
            arguments.putLong(IngredientDetailActivity.POSITION_VIDEO, mPosition);
            IngredientDetailFragment fragment = new IngredientDetailFragment();
            fragment.setArguments(arguments);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ingredient_placeholder, fragment)
                    .commit();
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mRecipe != null){
            outState.putParcelable(Recipe.TAG, mRecipe);
        }if(mStep != null){
            outState.putParcelable(Step.TAG, mStep);
        }if (mPosition != C.TIME_UNSET) {
            outState.putLong(POSITION_VIDEO, mPosition);
        }
        super.onSaveInstanceState(outState);
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
        } if(savedInstanceState.containsKey(POSITION_VIDEO)){
            mPosition = savedInstanceState.getLong(POSITION_VIDEO);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, IngredientListActivity.class);
            intent.putExtra(Recipe.TAG, mRecipe);
            intent.putExtra(Step.TAG, mStep);
            intent.putExtra(POSITION_VIDEO, mPosition);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
