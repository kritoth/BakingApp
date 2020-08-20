package com.tiansirk.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;
import com.tiansirk.bakingapp.ui.MainActivity;
import com.tiansirk.bakingapp.viewmodel.FavoriteViewModel;
import com.tiansirk.bakingapp.viewmodel.FavoriteViewModelFactory;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

public class ShowIngredientService extends IntentService {

    public static final String ACTION_UPDATE_INGREDIENTS = MainActivity.PACKAGE_NAME + ".action_show_ingredients";

    private FavoriteViewModel mViewModel;
    private Ingredient[] mIngredients;

    public ShowIngredientService(String name) {
        super(name);
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
    }

    /**
     * This is called by the above method to handle the sent Intent. This will do its job through the
     * handleActionUpdateIngredients() method
     * @param intent to be handled
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent != null){
            final String action = intent.getAction();
            if(ACTION_UPDATE_INGREDIENTS.equals(action)){
                //Set the Ingreds to the widget
                handleActionUpdateIngredients();
            }
        }
    }

    /**
     * This method does the detailes to set the Ingredients to the ListView on the RemoteView.
     */
    private void handleActionUpdateIngredients() {
        //TODO: this query maybe directly from DAO instead of through ViewModel since this is a Service in a background thread
        //Query the DB to find the necessary Ingredients
        FavoriteViewModelFactory factory = new FavoriteViewModelFactory(getApplication());
        ViewModelProvider provider = new ViewModelProvider((ViewModelStoreOwner) this, factory);
        mViewModel = provider.get(FavoriteViewModel.class);
        mViewModel.getRecipesByDate().observe((LifecycleOwner) this, new Observer<List<RecipeWithIngredsSteps>>() {
            @Override
            public void onChanged(List<RecipeWithIngredsSteps> recipeWithIngredsSteps) {
                RecipeWithIngredsSteps latestFavoritedRecipe = recipeWithIngredsSteps.get(0);
                mIngredients = new Ingredient[latestFavoritedRecipe.getIngredients().size()];
                Ingredient[] currIngreds = new Ingredient[latestFavoritedRecipe.getIngredients().size()];
                mIngredients = latestFavoritedRecipe.getIngredients().toArray(currIngreds);

            }
        });
        //Get the widget
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidgetProvider.class));
        //Trigger data update in the widget ListView and force data refresh
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        //Update the widget
        IngredientWidgetProvider.updateIngredientsWidgets(this, manager, appWidgetIds, mIngredients[0].getIngredient());
    }
}
