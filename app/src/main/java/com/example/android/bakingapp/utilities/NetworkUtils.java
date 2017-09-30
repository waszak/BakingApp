package com.example.android.bakingapp.utilities;

/**
 * Created by Waszak on 30.09.2017.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.android.bakingapp.R;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.HashMap;

import okhttp3.Cache;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private static Retrofit retrofit;
    public static RecipeService buildRecipeService(Context context) {
        if(retrofit == null) {
            RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.recipe_network_source))
                    .addConverterFactory(JacksonConverterFactory.create())
                    .addCallAdapterFactory(rxAdapter)
                    .build();
        }
        return  retrofit.create(RecipeService.class);
    }

    private static final int CACHE_SIZE = 60 * 1024 * 1024;
    private static boolean sIsPicassoSingletonSet;
    public static Picasso getPicasso(Context context){
        if(!sIsPicassoSingletonSet) {
            Cache cache = new Cache(context.getCacheDir(), CACHE_SIZE);
            okhttp3.OkHttpClient okHttp3Client = new okhttp3.OkHttpClient.Builder().cache(cache).build();
            OkHttp3Downloader downloader = new OkHttp3Downloader(okHttp3Client);
            Picasso picasso = new Picasso.Builder(context).downloader(downloader).build();
            Picasso.setSingletonInstance(picasso);
            sIsPicassoSingletonSet = true;
        }
        return Picasso.with(context);
    }

    /**
     * Builds the URL used to query The Movie DB
     *
     * @param imageUrl image url
     * @return The Request to image
     */
    public static RequestCreator buildImageRequest(Context context, String imageUrl) {
        return getPicasso(context).load(imageUrl);
    }


    public static void loadThumbnail(ImageView imageView, final String movieUrl){
        new AsyncTask<Object, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Object... params) {
                Bitmap bitmap = null;
                MediaMetadataRetriever mediaMetadataRetriever = null;
                try {
                    mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(movieUrl, new HashMap<>());

                    bitmap = mediaMetadataRetriever.getFrameAtTime();
                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    if (mediaMetadataRetriever != null)
                        mediaMetadataRetriever.release();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

}
