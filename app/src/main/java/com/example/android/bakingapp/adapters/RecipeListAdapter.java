package com.example.android.bakingapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Step;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Waszak on 22.07.2017.
 */

public class RecipeListAdapter  extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>{

    private final RecipeListOnClickHandler mClickHandler;
    private ArrayList<Recipe> mRecipes;

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.recipe_list_item, viewGroup, false);
        return new RecipeViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = mRecipes.get(position);
        holder.mRecipeNameTextView.setText(recipe.getName());
        if(!Strings.isNullOrEmpty(recipe.getImage())){
            NetworkUtils.buildImageRequest(holder.mContext, recipe.getImage())
                    .into(holder.mRecipeImage);
        }else{
            Step stepForThumbnail = null;
            for (Step step: Lists.reverse(recipe.getSteps())) {
                if(!Strings.isNullOrEmpty(step.getVideoURL())){
                    stepForThumbnail = step;
                    break;
                }
            }
            if(stepForThumbnail != null){
                NetworkUtils.loadThumbnail(holder.mRecipeImage, stepForThumbnail.getVideoURL());
            }
        }
    }

    @Override
    public int getItemCount() {
        return (mRecipes ==  null) ? 0 : mRecipes.size();
    }

    public void setRecipesList(List<Recipe> recipes) {
        mRecipes = new ArrayList<>(recipes);
        notifyDataSetChanged();
    }

    public ArrayList<Recipe> getRecipes(){
        return mRecipes;
    }

    public interface RecipeListOnClickHandler{
        void onClick(Recipe recipe);
    }
    public RecipeListAdapter(RecipeListOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }
    /**
     * Cache of the children views for a list item.
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.recipe_name) TextView mRecipeNameTextView;
        @BindView(R.id.recipe_image) ImageView mRecipeImage;

        // Will display the position in the grid, ie 0 through getItemCount() - 1
        final Context mContext;
        /**
         * Constructor for our ViewHolder. Within this constructor, we get a reference to our
         * TextView,ImageViews and set an onClickListener to listen for clicks. Those will be handled in the
         * onClick method below.
         * @param itemView The View that you inflated in
         *                 {@link RecipeViewHolder#onCreateViewHolder(ViewGroup, int)}
         */
        RecipeViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            itemView.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Recipe recipe = mRecipes.get(adapterPosition);
            mClickHandler.onClick(recipe);
        }
    }
}
