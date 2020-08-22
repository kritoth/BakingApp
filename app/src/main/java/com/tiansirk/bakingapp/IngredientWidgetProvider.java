package com.tiansirk.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;

import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.ui.MainActivity;
import com.tiansirk.bakingapp.ui.adapters.ListWidgetService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = MainActivity.PACKAGE_NAME + ".extra_item";
    public static final String EXTRA_INGREDIENTS_TO_SHOW = MainActivity.PACKAGE_NAME + ".extra_ingredients_to_show";

    public static void updateIngredientsWidgets(Context context, AppWidgetManager appWidgetManager,
                                                int[] appWidgetIds, List<Ingredient> ingredients) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, ingredients);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, List<Ingredient> ingredients) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_ingredients);
        // Set the ListWidgetService intent to act as the adapter for the ListView
        Intent serviceIntent = new Intent(context, ListWidgetService.class);

        serviceIntent.putParcelableArrayListExtra(EXTRA_INGREDIENTS_TO_SHOW, (ArrayList<? extends Parcelable>) ingredients);
        views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
        // Handle empty list of ingredients
        views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_text_view);

        // Intent to launch MainActivity
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
        // Set intent to the widget
        views.setOnClickPendingIntent(R.id.widget_list_placeholder, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        ShowIngredientService.startActionUpdateIngredients(context);
/*
        for(int appWidgetId : appWidgetIds){
            updateAppWidget(context.getApplicationContext(), appWidgetManager, appWidgetId, mRecipeId);//TODO: I don't have this mRecipeId here

        }

 */
        // This triggers onDatasetChanged from RemoteViewsService.RemoteViewsFactory
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
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