package com.tiansirk.bakingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.databinding.ActivityMainBinding;
import com.tiansirk.bakingapp.ui.RecipeAdapter;
import com.tiansirk.bakingapp.utils.JsonParser;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

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
        JsonParser.parseJsonFromFile(this);
    }

    private void initViews() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initRecycler() {
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rvRecipes.setLayoutManager(linearLayoutManager);
        binding.rvRecipes.setHasFixedSize(true);
        mAdapter = new RecipeAdapter(this);
        binding.rvRecipes.setAdapter(mAdapter);
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

    @Override
    public void onClick(Recipe clickedRecipe) {

    }
}
