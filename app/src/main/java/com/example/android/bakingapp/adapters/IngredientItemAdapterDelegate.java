package com.example.android.bakingapp.adapters;

import android.app.Activity;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.models.Ingredient;
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Waszak on 30.09.2017.
 */

class IngredientItemAdapterDelegate extends AbsListItemAdapterDelegate<Ingredient, Parcelable,
        IngredientItemAdapterDelegate.IngredientViewHolder> {


    private LayoutInflater inflater;

    IngredientItemAdapterDelegate(Activity activity) {
        inflater = activity.getLayoutInflater();
    }

    @Override
    protected boolean isForViewType(@NonNull Parcelable item, @NonNull List<Parcelable> list, int i) {
        return  item instanceof Ingredient;
    }

    @NonNull
    @Override
    protected IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
        return  new IngredientViewHolder(inflater.inflate(R.layout.recipe_ingredient,
                parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull Ingredient ingredient,
                                    @NonNull IngredientViewHolder holder,
                                    @NonNull List<Object> mValues) {
        holder.mIngredient = ingredient;
        holder.mIngredientName.setText(ingredient.getIngredient());
        holder.mIngredientQuantity.setText(ingredient.getQuantity().toString()+" "+ ingredient.getMeasure());
    }

     class IngredientViewHolder extends RecyclerView.ViewHolder {
         final View mView;
         @BindView(R.id.ingredient_name) TextView mIngredientName;
         @BindView(R.id.ingredient_quantity) TextView mIngredientQuantity;
         Ingredient mIngredient;
         IngredientViewHolder(View view) {
             super(view);
             ButterKnife.bind(this, itemView);
             mView = view;
        }

    }
}
