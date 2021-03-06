package com.example.android.bakingapp.utilities;

import com.example.android.bakingapp.models.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Waszak on 22.07.2017.
 */

public interface RecipeService {
    @GET("android-baking-app-json")
    Call<List<Recipe>> loadRecipeListing();
}
