package com.example.android.bakingapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.IngredientDetailActivity;
import com.example.android.bakingapp.IngredientDetailFragment;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.models.Step;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.google.common.base.Strings;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Waszak on 01.10.2017.
 */

class StepItemAdapterDelegate extends AbsListItemAdapterDelegate<Step, Parcelable,
        StepItemAdapterDelegate.StepViewHolder> {

    private LayoutInflater inflater;

    StepItemAdapterDelegate(Activity activity) {
            inflater = activity.getLayoutInflater();
    }

    @Override
    protected boolean isForViewType(@NonNull Parcelable item, @NonNull List<Parcelable> list, int i) {
            return  item instanceof Step;
    }

    @NonNull
    @Override
    protected StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
            return  new StepViewHolder(inflater.inflate(R.layout.recipe_step,
            parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull Step step,
    @NonNull StepViewHolder holder,
    @NonNull List<Object> mValues) {
            holder.mStep = step;
            holder.mTitle.setText(step.getShortDescription());
            if(!Strings.isNullOrEmpty(step.getVideoURL())){
                NetworkUtils.loadThumbnail(holder.mImage, step.getVideoURL());
            }

            holder.mView.setOnClickListener(v -> {
                          /* if (mTwoPane) {
                                Bundle arguments = new Bundle();
                                arguments.putString(IngredientDetailFragment.ARG_ITEM_ID, holder.mItem);
                                IngredientDetailFragment fragment = new IngredientDetailFragment();
                                fragment.setArguments(arguments);
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.ingriedient_detail_container, fragment)
                                        .commit();
                            } else {*/
                                Context context = v.getContext();
                                Intent intent = new Intent(context, IngredientDetailActivity.class);
                                intent.putExtra(Step.TAG, holder.mStep);

                                context.startActivity(intent);
                            //}
            });
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.step_image)
        ImageView mImage;
        @BindView(R.id.step_title) TextView mTitle;
        Step mStep;
        StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            mView = view;
        }

    }
}
