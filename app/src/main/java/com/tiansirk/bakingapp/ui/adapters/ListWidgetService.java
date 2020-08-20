package com.tiansirk.bakingapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.AppDatabase;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;

import java.util.List;

public class ListWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Ingredient> mIngredients;
    private AppDatabase mDb;
    private Recipe mRecipe;

    public ListRemoteViewsFactory(Context context) {
        this.mContext = context;
        this.mDb = AppDatabase.getsInstance(context);
    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all Ingredient info ordered by creation time
        List<RecipeWithIngredsSteps> recipes = mDb.recipeDao().loadAllFavoriteRecipesByDateAdded().getValue();
        mIngredients = recipes.get(0).getIngredients();
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
        if (mIngredients == null || mIngredients.size() == 0) return null;

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
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}