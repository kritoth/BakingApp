package com.tiansirk.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.ui.MainActivity;
import com.tiansirk.bakingapp.ui.SelectStepActivity;
import com.tiansirk.bakingapp.ui.adapters.ListWidgetService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidgetProvider extends AppWidgetProvider {

    public static final String TAG = IngredientWidgetProvider.class.getSimpleName();
    public static final String EXTRA_INGREDIENTS_TO_SHOW = MainActivity.PACKAGE_NAME + ".extra_ingredients_to_show";


    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d(TAG, "updateAppWidget is called");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_ingredients);
        // Set the ListWidgetService intent to act as the adapter for the ListView
        Intent serviceIntent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
        Log.d(TAG, "setRemoteAdapter is called");
        // Handle empty list of ingredients
        views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_text_view);

        // Intent to launch the respective Recipe's SelectStepActivity with listItemClickListener, aka PendingIntent, see ListWidgetService's getViewAt() method
        Intent appIntent = new Intent(context, SelectStepActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Set intent to the widget
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "onUpdate is called");
        for(int appWidgetId : appWidgetIds){
            updateAppWidget(context.getApplicationContext(), appWidgetManager, appWidgetId);
        }
        // This triggers onDatasetChanged from RemoteViewsService.RemoteViewsFactory
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        Log.d(TAG, "onAppWidgetOptionsChanged is called");
        updateAppWidget(context.getApplicationContext(), appWidgetManager, appWidgetId);
        // This triggers onDatasetChanged from RemoteViewsService.RemoteViewsFactory
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}