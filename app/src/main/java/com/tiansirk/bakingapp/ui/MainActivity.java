package com.tiansirk.bakingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import men.ngopi.zain.jsonloaderlibrary.JSONLoader;
import men.ngopi.zain.jsonloaderlibrary.StringLoaderListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.AppDatabase;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.ActivityMainBinding;
import com.tiansirk.bakingapp.model.FavoriteViewModel;
import com.tiansirk.bakingapp.model.FavoriteViewModelFactory;
import com.tiansirk.bakingapp.utils.DateConverter;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String SELECTED_RECIPE = "selected_recipe";
    private static final String STATE_OF_RECIPES = "state_of_recipes";

    private ActivityMainBinding binding;

    private Recipe[] mRecipes;
    private RecipeAdapter mAdapter;

    private FavoriteViewModel mViewModel;
    private AppDatabase mDbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the DB
        //mDbase = AppDatabase.getsInstance(getApplicationContext());
        getApplicationContext().deleteDatabase("favoriterecipes");//TODO: Uncomment this - It Deletes whole DB

        // Initiating views
        initViews();
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
            parseJsonFromFile();
            Log.d(TAG, "mRecipe is received from JSON");
            // And update the list to set their favorite status
            mViewModel.getRecipesById().observe(this, new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    for(Recipe recipe :recipes){
                        updateRecipeInArray(recipe);//TODO: ezt observe()-en kívül is meg kell tenni, hogy a csillagokat az elején jelölje,mert akkor még nincs change
                    }
                }
            });
        }

        // Set up Adapter
        setupAdapter();

        //Log.d(TAG, "\n***mRecipes array size: " + mRecipes.length + "\nFirst element: " + mRecipes[0]);
        //Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));

        // Set up ItemClickListener
        setupItemClickListeners();

        // Set up ItemLongClickListener
        setupItemLongClickListeners();

        if(mAdapter.getRecipesData().isEmpty()) showErrorMessage();
        else showDataView();
    }

    private void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle("Baking Time");//Sets the title in the action bar
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
        mAdapter = new RecipeAdapter(this);
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
        String selectedRecipeToJson = JsonParser.serializeRecipeToJson(recipe);
        Intent intent = new Intent(this, SelectStepActivity.class);
        intent.putExtra(SELECTED_RECIPE, selectedRecipeToJson);
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
                    //showFavoriteStatus(position);
                }
                //Removes if it is a favorite already
                else if(longClickedRecipe.getIsFavorite()) {
                    removeRecipeFromFavorites(longClickedRecipe);
                    //showFavoriteStatus(position);
                }
            }
        });
    }
    /** Saves the {@param Recipe} into the App's Database as favorite */
    private void saveRecipeAsFavorite(Recipe recipe) {
        recipe.setDateAddedToFav(DateConverter.toTimestamp(today()));
        recipe.setFavorite(true);
        mViewModel.insertRecipeToFavorites(recipe);

            updateRecipeInArray(recipe);
            mAdapter.setRecipesData(Arrays.asList(mRecipes));
            Toast.makeText(getApplicationContext(), recipe.getName() + " is saved as favorite!", Toast.LENGTH_SHORT).show();

        /* //These are for testing purposes only!!!
        int succesful = searchRecipe(recipe, insertedRecipeId);
        Log.d(TAG, "Recipe exists scnd: " + succesful);
        queryAll();
        */
    }
    /** Removes the {@param Recipe} from the App's Database */
    private void removeRecipeFromFavorites(Recipe recipe) {

        mViewModel.deleteRecipe(recipe);
        recipe.setFavorite(false);
        Log.d(TAG, "DELETERecipe executed");

            updateRecipeInArray(recipe);
            mAdapter.setRecipesData(Arrays.asList(mRecipes));
            Toast.makeText(this, recipe.getName() + " is removed from favorites.", Toast.LENGTH_SHORT).show();

    }

    /**
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
                        Log.d(TAG, "onResponse get called");
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, " onSaveInstanceState is called.");
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
                Log.d(TAG, "Menu item selected: " + item.toString());
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** This method will make the RecyclerView visible and hide the error message */
    private void showDataView() {
        /* First, make sure the error is invisible */
        binding.tvErrorMessageRecipes.setVisibility(View.INVISIBLE);
        /* Then hide loading indicator */
        binding.pbLoadingIndicatorRecipes.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie is visible */
        binding.rvRecipes.setVisibility(View.VISIBLE);
    }
    /** This method will make the error message visible and hide the RecyclerView */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        binding.rvRecipes.setVisibility(View.INVISIBLE);
        /* Then hide loading indicator */
        binding.pbLoadingIndicatorRecipes.setVisibility(View.INVISIBLE);
        /* Then, show the error */
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
    }




    /** This method will make the star either filled or not by */
    private void showFavoriteStatus(int position){
        if(position > -1) mAdapter.notifyItemChanged(position);
        else {
            // Sets up the LiveData with ViewModel to actively refresh the Adapter by observing the DB
            mViewModel.getRecipesById().observe(this, new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    Log.d(TAG, "Updating Recipes from LiveData in ViewModel");
                    mAdapter.updateRecipeList(recipes);
                }
            });
        }
    }




    /** For testing if Recipe insertion is OK. Returning 1 if exists and 0 if not */
    private int searchRecipe(Recipe recipe, long id){
        final Integer[] exists = {null};
        exists[0] = mDbase.recipeDao().queryIfRecipeExists(id);
        Log.d(TAG, "Recipe exists frst: " + exists[0]);
        return exists[0];
    }
/*    *//** For testing queries all Recipes, Ingredients and Steps *//*
    private void queryAll(){
        List<Recipe> queriedRecipes = new ArrayList<>();
        List<Ingredient> queriedIngreds = new ArrayList<>();
        List<Step> queriedSteps = new ArrayList<>();

        queriedRecipes.addAll(mDbase.recipeDao().queryAllFavoriteRecipesById());
        queriedIngreds.addAll(mDbase.ingredientDao().queryAllIngreds());
        queriedSteps.addAll(mDbase.stepDao().queryAllSteps());

        Log.d(TAG, "List of queried Recipes size: " + queriedRecipes.size());
        Log.d(TAG, "\nList of queried Ingredients size: " + queriedIngreds.size());
        Log.d(TAG, "\nList of queried Step size: " + queriedSteps.size());

        if(queriedRecipes != null && !queriedRecipes.isEmpty()) {
            Log.d(TAG,"\nFirst Recipe in DB: " + queriedRecipes.get(0).toString());
        }
        if(queriedIngreds != null && !queriedIngreds.isEmpty()) {
            Log.d(TAG,"\nFirst Ingredient in DB: " + queriedIngreds.get(0).toString());
        }
        if(queriedSteps != null && !queriedSteps.isEmpty()) {
            Log.d(TAG, "\nFirst Step in DB: " + queriedSteps.get(0).toString());
        }
    }*/
}
