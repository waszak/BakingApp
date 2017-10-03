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

package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.IngredientDetailActivity;
import com.example.android.bakingapp.IngredientDetailFragment;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Step;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.google.common.base.Strings;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Waszak on 01.10.2017.
 */

class StepItemAdapterDelegate extends AbsListItemAdapterDelegate<Step, Parcelable,
        StepItemAdapterDelegate.StepViewHolder> {

    private LayoutInflater inflater;
    private Recipe mRecipe;
    private AppCompatActivity mActivity;
    private boolean mTwoPane;

    StepItemAdapterDelegate(AppCompatActivity activity, Recipe recipe, boolean twoPane) {
        inflater = activity.getLayoutInflater();
        mRecipe = recipe;
        mActivity = activity;
        mTwoPane = twoPane;
    }

    @Override
    protected boolean isForViewType(@NonNull Parcelable item, @NonNull List<Parcelable> list, int i) {
            return  item instanceof Step;
    }

    @NonNull
    @Override
    protected StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return  new StepViewHolder(inflater.inflate(R.layout.recipe_step,
            parent, false), mRecipe);
    }

    @Override
    protected void onBindViewHolder(@NonNull Step step,
    @NonNull StepViewHolder holder,
    @NonNull List<Object> mValues) {
            holder.mStep = step;
            holder.mTitle.setText(step.getShortDescription());
            if(!Strings.isNullOrEmpty(step.getThumbnailURL())) {
                NetworkUtils.buildImageRequest(holder.mView.getContext(), step.getThumbnailURL())
                        .into(holder.mImage);
            }else if(!Strings.isNullOrEmpty(step.getVideoURL())){
                NetworkUtils.loadThumbnail(holder.mImage, step.getVideoURL());
            }

            holder.mView.setOnClickListener(v -> {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(Step.TAG, holder.mStep);
                    arguments.putParcelable(Recipe.TAG, holder.mRecipe);
                    IngredientDetailFragment fragment = new IngredientDetailFragment();
                    fragment.setArguments(arguments);
                    mActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.ingriedient_detail_container, fragment)
                        .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, IngredientDetailActivity.class);
                    intent.putExtra(Step.TAG, holder.mStep);
                    intent.putExtra(Recipe.TAG, holder.mRecipe);

                    context.startActivity(intent);
                }
            });
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.step_image)
        ImageView mImage;
        @BindView(R.id.step_title) TextView mTitle;
        Step mStep;
        Recipe mRecipe;
        StepViewHolder(View view, Recipe recipe) {
            super(view);
            ButterKnife.bind(this, itemView);
            mView = view;
            mRecipe = recipe;
        }

    }
}
