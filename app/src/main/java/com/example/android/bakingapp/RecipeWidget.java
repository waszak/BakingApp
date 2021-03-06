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

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.example.android.bakingapp.utilities.WidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidget extends AppWidgetProvider  {


    @NonNull
    private static Intent getServiceIntent(Context context, int [] appWidgetIds) {
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        return intent;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        ComponentName thisWidget = new ComponentName(context, RecipeWidget.class);
        onUpdate(context,AppWidgetManager.getInstance(context),AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget));
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // There may be multiple widgets active, so update all of them
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget);
        views.setTextViewText(R.id.appwidget_text, context.getString(R.string.Ingredients));
        views.setRemoteAdapter(R.id.recipes_widget, getServiceIntent(context, appWidgetIds));



        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.recipes_widget);
        appWidgetManager.updateAppWidget(appWidgetIds, views);

    }

}

