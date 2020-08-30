package com.tiansirk.bakingapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import men.ngopi.zain.jsonloaderlibrary.JSONLoader;
import men.ngopi.zain.jsonloaderlibrary.StringLoaderListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.tiansirk.bakingapp.IngredientWidgetProvider;
import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.databinding.ActivityMainBinding;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;
import com.tiansirk.bakingapp.ui.adapters.RecipeAdapter;
import com.tiansirk.bakingapp.viewmodel.FavoriteViewModel;
import com.tiansirk.bakingapp.viewmodel.FavoriteViewModelFactory;
import com.tiansirk.bakingapp.utils.DateConverter;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String PACKAGE_NAME;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SELECTED_RECIPE = "selected_recipe";
    private static final String STATE_OF_RECIPES = "state_of_recipes";

    private ActivityMainBinding binding;

    private Recipe[] mRecipes;
    private RecipeAdapter mAdapter;

    private FavoriteViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        //getApplicationContext().deleteDatabase("favoriterecipes");// Uncomment this - It Deletes whole DB

        // Initiating views
        initViews();
        // Initiating the custom adapter
        initAdapter();
        // Set up RecyclerView
        setupRecyclerView();
        // Set up ViewModel
        setupViewModel();

        // Get the saved Recipe array or load it from JSON
        if(savedInstanceState != null && savedInstanceState.containsKey(STATE_OF_RECIPES)){
            //mRecipes = JsonParser.getRecipesFromJson(savedInstanceState.getString(STATE_RECIPES));
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(STATE_OF_RECIPES);
            mRecipes = new Recipe[parcelables.length];
            for (int i = 0; i < parcelables.length; ++i) {
                mRecipes[i] = (Recipe) parcelables[i];
            }
            Log.d(TAG, "mRecipe is received from savedInstanceState");
        } else{
            // Load data from JSON
            parseJsonFromWeb();
            Log.d(TAG, "mRecipe is received from JSON");
            // And update the list to set their favorite status
            mViewModel.getRecipesByDate().observe(this, new Observer<List<RecipeWithIngredsSteps>>() {
                @Override
                public void onChanged(List<RecipeWithIngredsSteps> recipes) {
                    for(RecipeWithIngredsSteps recipe : recipes){
                        updateRecipeInArray(recipe.getRecipe());
                    }

                }
            });
        }
        // Show loading indicator while parsing from web
        while (mRecipes == null) {
            binding.pbLoadingIndicatorRecipes.setVisibility(View.VISIBLE);
        }
        // When parsed set up the Adapter
        if(mRecipes != null) {
            // Set up Adapter
            setupAdapter();
            // Set up ItemClickListener
            setupItemClickListeners();
            // Set up ItemLongClickListener
            setupItemLongClickListeners();
        }
        // Finally show data or error msg depending on parsing result
        if(mAdapter.getRecipesData().isEmpty()) showErrorMessage();
        else showDataView();
    }

    private void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle(getString(R.string.app_title));//Sets the title in the action bar
    }
    private void initAdapter(){
        mAdapter = new RecipeAdapter(this);
    }
    private void setupViewModel(){
        FavoriteViewModelFactory factory = new FavoriteViewModelFactory(getApplication());
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        mViewModel = provider.get(FavoriteViewModel.class);
    }
    private void setupRecyclerView() {
        // use the respective number of columns for phones and tablets(sw600dp)
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, gridColumnCount);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        binding.rvRecipes.setHasFixedSize(true);
    }
    private void setupAdapter(){
        mAdapter.setRecipesData(Arrays.asList(mRecipes));
        binding.rvRecipes.setAdapter(mAdapter);
    }

    /** Sets RecipeAdapterItemClickListener to the RecyclerView items according to the respective interface is in {@link RecipeAdapter} */
    private void setupItemClickListeners(){
        mAdapter.setOnItemClickListener(new RecipeAdapter.RecipeAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Recipe clickedRecipe = mRecipes[position];
                startSelectRecipeStepActivity(clickedRecipe);
            }
        });
    }
    /** Starts the {@link SelectStepActivity} activity and passing the Recipe that was clicked on */
    private void startSelectRecipeStepActivity(Recipe recipe) {
        Intent intent = new Intent(this, SelectStepActivity.class);
        intent.putExtra(SELECTED_RECIPE, recipe);
        startActivity(intent);
    }

    /** Sets RecipeAdapterItemLongClickListener to the RecyclerView items according to the respective interface is in {@link RecipeAdapter} */
    private void setupItemLongClickListeners(){
        mAdapter.setOnItemLongClickListener(new RecipeAdapter.RecipeAdapterItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                Recipe longClickedRecipe = mRecipes[position];
                //Saves if it is not a favorite yet
                if(!longClickedRecipe.getIsFavorite()) {
                    saveRecipeAsFavorite(longClickedRecipe);
                }
                //Removes if it is a favorite already
                else if(longClickedRecipe.getIsFavorite()) {
                    removeRecipeFromFavorites(longClickedRecipe);
                }
            }
        });
    }
    /** Saves the {@param Recipe} into the App's Database as favorite */
    private void saveRecipeAsFavorite(Recipe recipe) {
        recipe.setDateAddedToFav(DateConverter.toTimestamp(today()));
        recipe.setFavorite(true);
        mViewModel.insertRecipeToFavorites(recipe);
        Log.d(TAG, "SAVE Recipe executed");
        updateRecipeInArray(recipe);
        mAdapter.setRecipesData(Arrays.asList(mRecipes));
        Toast.makeText(getApplicationContext(), recipe.getName() + " is saved as favorite!", Toast.LENGTH_SHORT).show();
        updateWidget();
    }
    /** Removes the {@param Recipe} from the App's Database */
    private void removeRecipeFromFavorites(Recipe recipe) {
        mViewModel.deleteRecipe(recipe);
        recipe.setFavorite(false);
        Log.d(TAG, "DELETE Recipe executed");
        updateRecipeInArray(recipe);
        mAdapter.setRecipesData(Arrays.asList(mRecipes));
        Toast.makeText(this, recipe.getName() + " is removed from favorites.", Toast.LENGTH_SHORT).show();

        updateWidget();
    }

    /**
     * Reads from the http URL and returns its response's String representation to be used by GSON for parsing.
     * Uses OKHttp library: https://github.com/square/okhttp
     */
    private void parseJsonFromWeb(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // store the result
                    mRecipes = JsonParser.getRecipesFromJson(response.body().string());
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(STATE_OF_RECIPES, mRecipes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.favorite:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** This method will make the RecyclerView visible and hide the error message */
    private void showDataView() {
        // First, make sure the error is invisible
        binding.tvErrorMessageRecipes.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbLoadingIndicatorRecipes.setVisibility(View.INVISIBLE);
        // Then, make sure the movie is visible
        binding.rvRecipes.setVisibility(View.VISIBLE);
    }
    /** This method will make the error message visible and hide the RecyclerView */
    private void showErrorMessage() {
        // First, hide the currently visible data
        binding.rvRecipes.setVisibility(View.INVISIBLE);
        // Then hide loading indicator
        binding.pbLoadingIndicatorRecipes.setVisibility(View.INVISIBLE);
        // Then, show the error
        binding.tvErrorMessageRecipes.setVisibility(View.VISIBLE);
    }

    /** Helper method to return the date of the day it's been called */
    private static Date today(){
        return Calendar.getInstance().getTime();
    }

    /** Helper method to update one element in the array of Recipes */
    private void updateRecipeInArray(Recipe recipe){
        for(int i=0; i<mRecipes.length;i++){
            if(mRecipes[i].equals(recipe))
                mRecipes[i].setFavorite(recipe.getIsFavorite());
                Log.d(TAG, "Updating " + recipe.getName() + "'s favorite status to: " + recipe.getIsFavorite());
        }
        mAdapter.setRecipesData(Arrays.asList(mRecipes));
    }

    /** Update the widget */
    private void updateWidget(){
        //Get the widget
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(this, IngredientWidgetProvider.class));
        //Trigger data update in the widget ListView and force data refresh
        manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }



    /** UNUSED
     * This is to be used when the json is on the disk instead of parsing it from the web
     *
     * Reads from the .json file and returns its String representation. Uses JSONLoader Library,
     * https://android-arsenal.com/details/1/7916,
     * a simple Android library to open .json file from the {assets} folder
     * @return String file
     */
    private void parseJsonFromFile(){
        JSONLoader.with(this)
                .fileName("baking.json")
                .get(new StringLoaderListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "parseJsonFromFile - onResponse get called");
                        // response as String to be used to parse content into array of Recipe
                        mRecipes = JsonParser.getRecipesFromJson(response);
                    }
                    @Override
                    public void onFailure(IOException error) {
                        // error with opening/reading file
                        Log.e(TAG, "Error with reading from .json file!:", error);
                        showErrorMessage();
                    }
                });
    }

}