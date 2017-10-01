package com.example.android.bakingapp.utilities;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.WidgetProvider;


/**
 * Service for remote views
 */

public class WidgetService extends RemoteViewsService {
    /**
     * Get appropriate  factory for data.
     *
     * @param intent
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetProvider(getApplicationContext(), intent);
    }
}
