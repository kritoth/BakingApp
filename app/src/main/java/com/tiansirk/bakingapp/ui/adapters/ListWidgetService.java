package com.tiansirk.bakingapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.AppDatabase;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;
import com.tiansirk.bakingapp.model.Step;
import com.tiansirk.bakingapp.utils.ParcelableUtil;

import java.util.ArrayList;
import java.util.List;

public class ListWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public static final String TAG = ListRemoteViewsFactory.class.getSimpleName();
    private static final String SELECTED_INGREDIENT = "selected_ingredient";
    private Context mContext;
    private Recipe mRecipe;
    private List<Ingredient> mIngredients;
    private AppDatabase mDb;

    public ListRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        this.mDb = AppDatabase.getsInstance(context);
        mIngredients = new ArrayList<>();

    }

    @Override
    public void onCreate() {
    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get all Ingredient info of the lastly favorited Recipe
        List<RecipeWithIngredsSteps> recipes = mDb.recipeDao().queryAllFavoriteRecipesByDateAdded();
        //If there are no ingredients saved, avoid getting nullpointer exception
        if (recipes != null && recipes.size() > 0){
            mRecipe = extractRecipe(recipes.get(0));
            mIngredients = recipes.get(0).getIngredients();
        }
        else{
            mRecipe = null;
            mIngredients.clear();
        }

        Log.d(TAG, "onDataSetChanged is called, mIngredients size: " + mIngredients.size());
    }

    /* Helper method for creating plain Recipe object - which properly contains Ingredient and Step arrays -
     * from RecipeWithIngredsSteps object */
    private static Recipe extractRecipe(RecipeWithIngredsSteps r) {
        Ingredient[] currIngreds = new Ingredient[r.getIngredients().size()];
        currIngreds = r.getIngredients().toArray(currIngreds);
        Step[] currSteps = new Step[r.getSteps().size()];
        currSteps = r.getSteps().toArray(currSteps);
        Recipe currRecipe = r.getRecipe();
        currRecipe.setIngredients(currIngreds);
        currRecipe.setSteps(currSteps);
        return currRecipe;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mIngredients == null || mIngredients.size() == 0) return 0;
        return mIngredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     * @param i The current position of the item in the ListView to be displayed
     * @return The RemoteViews object to display for the provided position
     */

    @Override
    public RemoteViews getViewAt(int i) {
        // position will always range from 0 to getCount() - 1.
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_view);
        // Update the textview with the ingredients' names
        views.setTextViewText(R.id.widget_item_ingredient_name, mIngredients.get(i).getIngredient());

        // Fill in the onClick PendingIntent Template
        Bundle extras = new Bundle();
        // Use byte[] instead of a custom Parcelable for sending through the Recipe: https://stackoverflow.com/questions/18000093/how-to-marshall-and-unmarshall-a-parcelable-to-a-byte-array-with-help-of-parcel/18000094#18000094
        // because the OS cannot handle it if the Intent is subject for "a core OS process to modify it": https://commonsware.com/blog/2016/07/22/be-careful-where-you-use-custom-parcelables.html
        byte[] bytes = ParcelableUtil.marshall(mRecipe);
        extras.putByteArray(SELECTED_INGREDIENT, bytes);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.widget_item_ingredient_name, fillInIntent);

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
