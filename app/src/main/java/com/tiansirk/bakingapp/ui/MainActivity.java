package com.tiansirk.bakingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import men.ngopi.zain.jsonloaderlibrary.JSONLoader;
import men.ngopi.zain.jsonloaderlibrary.StringLoaderListener;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.ActivityMainBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String EXTRA_SELECTED_RECIPE = "selected_recipe";

    private ActivityMainBinding binding;

    private Recipe[] mRecipes;
    private RecipeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initiating views
        initViews();

        // Initiating RecyclerView
        initRecycler();

        // Load data from JSON
        parseJsonFromFile();

        // Set ItemClickListeners: plain and long
        setupItemClickListeners();

        mAdapter.setRecipesData(Arrays.asList(mRecipes));

        Log.d(TAG, "\n***mRecipes array size: " + mRecipes.length + "\nFirst element: " + mRecipes[0]);
        Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));

        if(mAdapter.getRecipesData().isEmpty()) showErrorMessage();
        else showDataView();
    }

    private void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initRecycler() {
        // use the respective number of columns for phones and tablets(sw600dp)
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, gridColumnCount);
        binding.rvRecipes.setLayoutManager(gridLayoutManager);
        binding.rvRecipes.setHasFixedSize(true);
        mAdapter = new RecipeAdapter(this);
        binding.rvRecipes.setAdapter(mAdapter);


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
                startSelectRecipeStep(clickedRecipe);
            }
        });

        mAdapter.setOnItemLongClickListener(new RecipeAdapter.RecipeAdapterItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                Toast.makeText(getApplicationContext(), "Item LONG clicked: " + mRecipes[position].getName(), Toast.LENGTH_SHORT).show();
                //TODO: make it favorite into local db
            }
        });
    }

    /**
     * Starts the {@link SelectRecipeStep} activity and passing the Recipe that was clicked on as
     * JSON by using Gson for converting
     * @param recipe that was clicked on
     */
    private void startSelectRecipeStep(Recipe recipe) {
        Step[] steps = recipe.getSteps().toArray(new Step[0]);
        String selectedRecipeToJson = JsonParser.serializeStepsToJson(steps);

        Intent intent = new Intent(this, SelectRecipeStep.class);
        intent.putExtra(EXTRA_SELECTED_RECIPE, selectedRecipeToJson);

        startActivity(intent);
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
     * This method will make the RecyclerView visible and hide the error message
     */
    private void showDataView() {
        /* First, make sure the error is invisible */
        binding.tvErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then hide loading indicator */
        binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
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
        binding.pbLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        binding.tvErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
}
