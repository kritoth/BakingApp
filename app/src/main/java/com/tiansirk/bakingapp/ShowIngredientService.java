package com.tiansirk.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tiansirk.bakingapp.data.AppDatabase;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class ShowIngredientService extends IntentService {

    public static final String TAG = ShowIngredientService.class.getSimpleName();

    public static final String ACTION_UPDATE_INGREDIENTS = ".action_show_ingredients";

    public ShowIngredientService() {
        super("ShowIngredientService");
    }

    /**
     * Starts this service to perform updating the widget. If the service is performing this task will be queued.
     * TODO: This is called when new favorite is saved to show its ingredients
     * @param context
     */
    public static void startActionUpdateIngredients (Context context) {
        Intent intent = new Intent(context, ShowIngredientService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS);
        context.startService(intent);
        Log.wtf(TAG, "service started");
    }

    /**
     * This is called by the above method to handle the sent Intent. This will do its job through the
     * handleActionUpdateIngredients() method
     * @param intent to be handled
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.wtf(TAG, "onhandleIntent started");
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_INGREDIENTS.equals(action)){
                //Set the Ingreds to the widget
                handleActionUpdateIngredients();
            }
        }
    }

    /**
     * This method does the details to set the Ingredients to the ListView on the RemoteView
     */
    private void handleActionUpdateIngredients() {
        Log.wtf(TAG, "handleActionUpdateIngredients is called");
        //Query the DB to find the necessary Ingredients
        AppDatabase db = AppDatabase.getsInstance(getApplicationContext());
        List<RecipeWithIngredsSteps> recipes = db.recipeDao().queryAllFavoriteRecipesByDateAdded();
        Log.wtf(TAG, "DB queried. First Recipe's Ingredients list size: " + recipes.get(0).getIngredients().size());
        RecipeWithIngredsSteps latestFavoritedRecipe = recipes.get(0);
        List<Ingredient> ingreds = new ArrayList<>();
        ingreds = latestFavoritedRecipe.getIngredients();

        //Get the widget
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidgetProvider.class));

        //Trigger data update in the widget ListView and force data refresh
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        Log.wtf(TAG, "notifyAppWidgetViewDataChanged called");
        //Update the widget
        IngredientWidgetProvider.updateIngredientsWidgets(this, manager, appWidgetIds, ingreds);
    }
}
