package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.models.Ingredient;
import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.utilities.NetworkUtils;
import com.example.android.bakingapp.utilities.RecipePreferences;
import com.example.android.bakingapp.utilities.RecipeService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * Provider for ListView in widget
 */

public class WidgetProvider implements RemoteViewsService.RemoteViewsFactory {

    List<Recipe> dataSource;
    Recipe current;
    private Context mContext;
    public final static String RECIPE_CHANGED = "RECIPE_CHANGED";
    public WidgetProvider(Context context, Intent intent){
        mContext = context;
    }

    /*
        Called when factory is created;
     */
    @Override
    public void onCreate() {
        Timber.d("Factory is created");
        onDataSetChanged();
    }


    @Override
    public void onDataSetChanged() {
        if (dataSource == null || dataSource.isEmpty()) {
            fillRecipeList();
            return;
        }
        Timber.d("list data changed");
        UpdateRecipe();
    }
    void UpdateRecipe(){
        if (dataSource == null || dataSource.isEmpty()) {
            return;
        }
        int id = RecipePreferences.getRecipeId(mContext);
        Recipe recipeToShow = null;
        for (Recipe recipe: dataSource) {
            recipeToShow = recipe; //in case there is nothing selected we show first one
            if(recipe.getId() == id){
                break;
            }
        }
        if(recipeToShow == null){
            return;
        }
        current = recipeToShow;
    }

    void fillRecipeList() {

        RecipeService recipeService = NetworkUtils.buildRecipeService(mContext);
        recipeService.loadRecipeListing().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null && (dataSource == null || dataSource.isEmpty())) {
                    // tasks available
                    dataSource = response.body();
                    Intent intent = new Intent(mContext, RecipeWidget.class);
                    intent.setAction(WidgetProvider.RECIPE_CHANGED);
                    mContext.sendBroadcast(intent);

                } else {
                    Timber.e("Error response, no access to resource");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                // something went completely south (like no internet connection)
                Timber.d(t, "Error");
            }
        });

    }



    /**
     *
     * @param position of view we want to get
     * @return view at position
     */
    @Override
    public RemoteViews getViewAt(int position) {
        Timber.d("Get view at position "+position);
        RemoteViews remoteViews =  new RemoteViews(mContext.getPackageName(),
                R.layout.ingredient_list_content);
        Ingredient ingredient = current.getIngredients().get(position);

        remoteViews.setTextViewText(R.id.id, ingredient.getIngredient());
        remoteViews.setTextViewText(R.id.content, ingredient.getQuantity()+" "+ingredient.getMeasure());

        return remoteViews;
    }




    @Override
    public int getCount() {
        return current==null?0:current.getIngredients().size();
    }

    @Override
    public void onDestroy() {

    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * @return numbers of views returned by this provider
     */
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    /**
     *
     * @param position in adapter
     * @return position of row in data set
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @return if false there are ItemId are unique.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
