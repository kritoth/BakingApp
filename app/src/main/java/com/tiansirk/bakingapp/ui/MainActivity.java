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
import android.widget.ImageView;
import android.widget.Toast;


import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.AppDatabase;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.ActivityMainBinding;
import com.tiansirk.bakingapp.model.FavoriteViewModel;
import com.tiansirk.bakingapp.model.FavoriteViewModelFactory;
import com.tiansirk.bakingapp.utils.AppExecutors;
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

    private static final String EXTRA_SELECTED_RECIPE = "selected_recipe";
    private final String STATE_RECIPES = "state_recipes";

    private ActivityMainBinding binding;

    private Recipe[] mRecipes;
    private RecipeAdapter mAdapter;

    private AppDatabase mDbase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getApplicationContext().deleteDatabase("favoriterecipes");

        // Initiating views
        initViews();
        // Initiating RecyclerView
        setupRecyclerView();

        // Get the saved Recipe array or load it from JSON
        if(savedInstanceState != null && savedInstanceState.containsKey(STATE_RECIPES)){
            //mRecipes = JsonParser.getRecipesFromJson(savedInstanceState.getString(STATE_RECIPES));
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(STATE_RECIPES);
            mRecipes = new Recipe[parcelables.length];
            for (int i = 0; i < parcelables.length; ++i) {
                mRecipes[i] = (Recipe) parcelables[i];
            }
            Log.d(TAG, "mRecipe is received from savedInstanceState");
        } else{
            // Load data from JSON
            parseJsonFromFile();
        }
        // Set up the custom Adapter
        mAdapter = new RecipeAdapter(this);
        mAdapter.setRecipesData(Arrays.asList(mRecipes));
        binding.rvRecipes.setAdapter(mAdapter);

        //Log.d(TAG, "\n***mRecipes array size: " + mRecipes.length + "\nFirst element: " + mRecipes[0]);
        //Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));

        // Set up the DB
        mDbase = AppDatabase.getsInstance(getApplicationContext());
        // Observe the DB whether to refresh the itemView
        //showFavoriteStatus(-1);

        // Set ItemClickListeners: single and long
        setupItemClickListeners();

        if(mAdapter.getRecipesData().isEmpty()) showErrorMessage();
        else showDataView();
    }

    private void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
    private void setupRecyclerView() {
        // use the respective number of columns for phones and tablets(sw600dp)
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, gridColumnCount);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        binding.rvRecipes.setHasFixedSize(true);
    }


    /**
     * Reads from the .json file and returns its String representation. Uses JSONLoader Library,
     * https://android-arsenal.com/details/1/7916,
     * a simple Android library to open .json file from the {assets} folder
     *
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

    /**
     * Sets RecipeAdapterItemClickListener and RecipeAdapterItemLongClickListener to the RecyclerView items
     * according to the respective interfaces are in {@link RecipeAdapter}
     */
    private void setupItemClickListeners(){
        mAdapter.setOnItemClickListener(new RecipeAdapter.RecipeAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Recipe clickedRecipe = mRecipes[position];
                startSelectRecipeStepActivity(clickedRecipe);
            }
        });

        mAdapter.setOnItemLongClickListener(new RecipeAdapter.RecipeAdapterItemLongClickListener() {
            @Override
            public void onItemLongClick(int position, View view) {
                Recipe longClickedRecipe = mRecipes[position];

                //Saves if it is not a favorite yet
                if(!longClickedRecipe.isFavorite()) {
                    saveRecipeAsFavorite(longClickedRecipe);

                    showFavoriteStatus(position);
                }
                //Removes if it is a favorite already
                else if(longClickedRecipe.isFavorite()) {
                    removeRecipeFromFavorites(longClickedRecipe);

                    showFavoriteStatus(position);
                }
                else{
                    Log.e(TAG, "The long-clicked Recipe's isFavorite is null!");
                }

            }
        });
    }

    /**
     * Starts the {@link SelectStepActivity} activity and passing the Recipe that was clicked on as
     * JSON by using Gson for converting
     * @param recipe that was clicked on
     */
    private void startSelectRecipeStepActivity(Recipe recipe) {
        String selectedRecipeToJson = JsonParser.serializeRecipeToJson(recipe);

        Intent intent = new Intent(this, SelectStepActivity.class);
        intent.putExtra(EXTRA_SELECTED_RECIPE, selectedRecipeToJson);
        startActivity(intent);
    }

    /**
     * Saves the {@param Recipe} into the App's Database as favorite
     */
    private void saveRecipeAsFavorite(final Recipe recipe) {
        recipe.setDateAddedToFav(DateConverter.toTimestamp(today()));
        recipe.setFavorite(true);
        final long[] insertedRecipeId = new long[1];
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                insertedRecipeId[0] = insertRecipeToDbase(recipe);
                for(int i=0; i<recipe.getIngredients().length; i++){
                    Ingredient currIngredient = recipe.getIngredients()[i];
                    currIngredient.setRecipeId(insertedRecipeId[0]);
                    insertIngredient(currIngredient);
                    //Log.d(TAG, "Ingredient inserted:\n" + i + ": " + currIngredient.toString());
                }
                for(int j=0; j<recipe.getSteps().length; j++){
                    Step currStep = recipe.getSteps()[j];
                    currStep.setRecipeId(insertedRecipeId[0]);
                    insertStep(currStep);
                    //Log.d(TAG, "Step inserted:\n" + j + ": " + currStep.toString());
                }
            }
        });
        if(insertedRecipeId[0] > -1){
            Toast.makeText(getApplicationContext(), recipe.getName() + " is saved as favorite!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Recipe INSERT successful\nisFavorite status: " + recipe.isFavorite());
        } else{
            Toast.makeText(getApplicationContext(), "Saving " + recipe.getName() + " as favorite was unsuccessful.", Toast.LENGTH_LONG).show();
            recipe.setFavorite(false);
            Log.d(TAG, "Recipe INSERT unsuccessful\nisFavorite status: " + recipe.isFavorite());
        }
        /* //These are for testing purposes only!!!
        int succesful = searchRecipe(recipe, insertedRecipeId);
        Log.d(TAG, "Recipe exists scnd: " + succesful);
        queryAll();
        */
    }

    /**
     * Inserts a {@param Recipe} object into the app's Database
     * @return the iD of the new row from the app's Database
     */
    private long insertRecipeToDbase(final Recipe recipe){
        final long[] iD = new long[1];
        //Log.d(TAG, "INSERTRecipe started: " + recipe.toString());
        iD[0] = mDbase.recipeDao().insertRecipeToFavorites(recipe); //- This is for testing purposes only
        /*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                iD[0] = mDbase.recipeDao().insertRecipeToFavorites(recipe);
            }
        });
*/
        Log.d(TAG, "INSERTRecipe executed: " + iD[0]);
        return iD[0];
    }

    /**
     * Inserts an {@param Ingredient} object into the app's Database
     * @return the iD of the new row from the app's Database
     */
    private long insertIngredient(final Ingredient ingredient){
        final long[] iD = new long[1];
        iD[0] = mDbase.ingredientDao().insertIngredient(ingredient);
        /*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                iD[0] = mDbase.ingredientDao().insertIngredient(ingredient);
            }
        });
        */
        return iD[0];
    }

    /**
     * Inserts a {@param Step} object into the app's Database
     * @return the iD of the new row from the app's Database
     */
    private long insertStep(final Step step){
        final long[] iD = new long[1];
        mDbase.stepDao().insertStep(step);
        /*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                iD[0] = mDbase.stepDao().insertStep(step);
            }
        });
         */
        return iD[0];
    }

    /**
     * Removes the {@param Recipe} from the App's Database
     */
    private void removeRecipeFromFavorites(final Recipe recipe) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                deleteIngredientsFromFavorites(recipe);
                deleteStepsFromFavorites(recipe);
                deleteRecipeFromFavorites(recipe);
            }
        });

        recipe.setFavorite(false);
        Log.d(TAG, "DELETERecipe executed, isFavorite: " + recipe.isFavorite());
        Toast.makeText(this, recipe.getName() + " is removed from favorites.", Toast.LENGTH_LONG).show();
    }

    /**
     * Deletes Ingredient objects related to the {@param Recipe} object from the app's Database
     */
    private void deleteIngredientsFromFavorites(final Recipe recipe){
        mDbase.ingredientDao().removeIngredientsOfRecipe(recipe.getId());
        /*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDbase.ingredientDao().removeIngredientsOfRecipe(recipe.getId());
            }
        });
         */
    }

    /**
     * Deletes Step objects related to the {@param Recipe} object from the app's Database
     */
    private void deleteStepsFromFavorites(final Recipe recipe){
        mDbase.stepDao().removeStepsOfRecipe(recipe.getId());
        /*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDbase.stepDao().removeStepsOfRecipe(recipe.getId());
            }
        });
         */
    }

    /**
     * Deletes the {@param Recipe} object from the app's Database
     * @return the iD of the deleted row from the app's Database
     */
    private void deleteRecipeFromFavorites(final Recipe recipe){
        mDbase.recipeDao().removeFavoriteRecipe(recipe);
        /*
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDbase.recipeDao().removeFavoriteRecipe(recipe);
            }
        });
         */
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, " onSaveInstanceState is called.");
        outState.putParcelableArray(STATE_RECIPES, mRecipes);
//        outState.putString(STATE_RECIPES, JsonParser.serializeRecipesToJson(mRecipes));
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

    /**
     * This method will make the RecyclerView visible and hide the error message
     */
    private void showDataView() {
        /* First, make sure the error is invisible */
        binding.tvErrorMessageRecipes.setVisibility(View.INVISIBLE);
        /* Then hide loading indicator */
        binding.pbLoadingIndicatorRecipes.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie is visible */
        binding.rvRecipes.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and hide the RecyclerView
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        binding.rvRecipes.setVisibility(View.INVISIBLE);
        /* Then hide loading indicator */
        binding.pbLoadingIndicatorRecipes.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        binding.tvErrorMessageRecipes.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the star either filled or not by
     */
    private void showFavoriteStatus(int position){

        if(position > -1) mAdapter.notifyItemChanged(position);
        else {
            // Sets up the LiveData with ViewModel to actively refresh the Adapter by observing the DB
            FavoriteViewModelFactory factory = new FavoriteViewModelFactory(getApplication());
            ViewModelProvider provider = new ViewModelProvider(this, factory);
            FavoriteViewModel viewModel = provider.get(FavoriteViewModel.class);

            viewModel.getRecipesById().observe(this, new Observer<List<Recipe>>() {
                @Override
                public void onChanged(List<Recipe> recipes) {
                    Log.d(TAG, "Updating Recipes from LiveData in ViewModel");
                    mAdapter.updateRecipeList(recipes);
                }
            });
        }
    }

    /**
     * Helper method to return the date of the day it's been called
     * @return Date object of the same day
     */
    private static Date today(){
        return Calendar.getInstance().getTime();
    }



    /*
    * For testing if Recipe insertion is OK. Returning 1 if exists and 0 if not
     */
    private int searchRecipe(Recipe recipe, long id){
        final Integer[] exists = {null};
        exists[0] = mDbase.recipeDao().queryIfRecipeExists(id);
        Log.d(TAG, "Recipe exists frst: " + exists[0]);
        return exists[0];
    }
    /*
     * For testing queries all Recipes, Ingredients and Steps
     */
    private void queryAll(){
        List<Recipe> queriedRecipes = new ArrayList<>();
        List<Ingredient> queriedIngreds = new ArrayList<>();
        List<Step> queriedSteps = new ArrayList<>();

        queriedRecipes.addAll(mDbase.recipeDao().queryAllFavoriteRecipes());
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
    }
}
