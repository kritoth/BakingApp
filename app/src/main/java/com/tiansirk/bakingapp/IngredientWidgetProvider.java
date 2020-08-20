package com.tiansirk.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.tiansirk.bakingapp.ui.MainActivity;
import com.tiansirk.bakingapp.ui.adapters.ListWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientWidgetProvider extends AppWidgetProvider {

    public static void updateIngredientsWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds, String content) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, content);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String content) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_ingredients);
        // Set the ListWidgetService intent to act as the adapter for the ListView
        Intent serviceIntent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);
        // Intent to launch MainActivity
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
        // Set intent to the widget
        views.setOnClickPendingIntent(R.id.widget_list_placeholder, pendingIntent);
        // Update widget content
        views.setTextViewText(R.id.widget_empty_text_view, content);
        // Handle empty list of ingredients
        views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_view_placeholder);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update widget action, the service takes care of updating the widgets UI
        ShowIngredientService.startActionUpdateIngredients(context);
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