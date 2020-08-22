package com.tiansirk.bakingapp.ui.adapters;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tiansirk.bakingapp.IngredientWidgetProvider;
import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.AppDatabase;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String TAG = ListRemoteViewsFactory.class.getSimpleName();
    private Context mContext;
    private List<Ingredient> mIngredients;
    private AppDatabase mDb;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        this.mDb = AppDatabase.getsInstance(context);
        mIngredients = new ArrayList<>();
        Log.d(TAG, "constructor is called, mIngredients size: " + mIngredients.size());
    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all Ingredient info of the lastly favorited Recipe
        List<RecipeWithIngredsSteps> recipes = mDb.recipeDao().queryAllFavoriteRecipesByDateAdded();
        mIngredients = recipes.get(0).getIngredients();
        Log.d(TAG, "onDataSetChanged is called, mIngredients size: " + mIngredients.size());
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mIngredients == null) return 0;
        return mIngredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     * @param i The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided position
     */

    @Override
    public RemoteViews getViewAt(int i) {
        Log.d(TAG, "getViewAt is called, mIngredients size: " + mIngredients.size());
        // If still there are no ingredients, get all Ingredient info ordered by creation time
        if (mIngredients == null || mIngredients.size() == 0) {
            List<RecipeWithIngredsSteps> recipes = mDb.recipeDao().queryAllFavoriteRecipesByDateAdded();
            mIngredients = recipes.get(0).getIngredients();
        }
        Log.d(TAG, "getViewAt after checking mIngredients size. The new size is: " + mIngredients.size());
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_view);
        // Update the textview with the ingredients' names
        views.setTextViewText(R.id.widget_item_ingredient_name, mIngredients.get(i).getIngredient());

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
