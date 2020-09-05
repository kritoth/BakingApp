package com.tiansirk.bakingapp.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.databinding.ActivityFavoriteBinding;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;
import com.tiansirk.bakingapp.model.Step;
import com.tiansirk.bakingapp.ui.adapters.RecipeAdapter;
import com.tiansirk.bakingapp.viewmodel.FavoriteViewModel;
import com.tiansirk.bakingapp.viewmodel.FavoriteViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteActivity extends AppCompatActivity {

    private static final String TAG = FavoriteActivity.class.getSimpleName();
    private static final String DEFAULT_SORT_PREFERENCE = "date";
    private static final String SELECTED_RECIPE = "selected_recipe";

    private ActivityFavoriteBinding binding;

    private FavoriteViewModel mViewModel;

    private List<Recipe> mFavRecipes;
    private RecipeAdapter mAdapter;
    private String mSortingPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mFavRecipes = new ArrayList<>();
        // Init views
        initViews();
        // Init RecyclerView
        initRecyclerView();
        // Set up ViewModel
        setupViewModel();
        // Set up Adapter
        setupAdapter();
        // Observe Database
        loadRecipesFromDB();

        // Set up ItemClickListener
        setupItemClickListener();

        if(mFavRecipes == null || mFavRecipes.isEmpty()) showNoFavMessage();
        else showDataView();

    }

    private void initViews() {
        Log.d(TAG, "Viewbinding is initiated.");
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }

    private void initRecyclerView() {
        // use the respective number of columns for phones and tablets(sw600dp)
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(this, gridColumnCount);
        binding.recyclerviewFavorites.setLayoutManager(gridLayoutManager);
        binding.recyclerviewFavorites.setHasFixedSize(true);
        Log.d(TAG, "RecyclerView has been set up.");
    }

    private void setupViewModel(){
        FavoriteViewModelFactory factory = new FavoriteViewModelFactory(getApplication());
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        mViewModel = provider.get(FavoriteViewModel.class);
        Log.d(TAG, "ViewModel has been set up.");
    }

    private void setupAdapter(){
        mAdapter = new RecipeAdapter(this);
        binding.recyclerviewFavorites.setAdapter(mAdapter);
        Log.d(TAG, "Adapter has been set up.");
    }

    /** Queries the DB using LiveData */
    private void loadRecipesFromDB() {
        binding.pbLoadingIndicatorFavorites.setVisibility(View.VISIBLE);
        if (mSortingPreference == null || mSortingPreference.isEmpty()) {
            mSortingPreference = DEFAULT_SORT_PREFERENCE;
        }
        switch (mSortingPreference) {
            case "date":
                    mViewModel.getRecipesByDate().observe(this, new Observer<List<RecipeWithIngredsSteps>>() {
                        @Override
                        public void onChanged(List<RecipeWithIngredsSteps> recipes) {
                            showDataView();
                            for(RecipeWithIngredsSteps r : recipes) {
                                Recipe currRecipe = extractRecipe(r);
                                mFavRecipes.add(currRecipe);
                            }
                            mAdapter.setRecipesData(mFavRecipes);
                        }
                    });
                    break;
            case "delete":
                    confirmAndNuke();
                    break;
        }
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

    /** Sets RecipeAdapterItemClickListener to the RecyclerView items according to the respective interface is in {@link RecipeAdapter} */
    private void setupItemClickListener(){
        mAdapter.setOnItemClickListener(new RecipeAdapter.RecipeAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Recipe clickedRecipe = mFavRecipes.get(position);
                startSelectRecipeStepActivity(clickedRecipe);
            }
        });
    }
    /** Starts the {@link SelectStepActivity} activity and passing the Recipe that was clicked on */
    private void startSelectRecipeStepActivity(Recipe recipe) {
        Intent intent = new Intent(this, SelectStepActivity.class);
        setIngredsAndSteps(recipe);
        intent.putExtra(SELECTED_RECIPE, recipe);
        startActivity(intent);
    }

    private void setIngredsAndSteps(Recipe recipe){

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int selectedItem = item.getItemId();
        switch (selectedItem){
            case R.id.delete_all:
                mSortingPreference = "delete";
                Log.d(TAG, "Delete All menu item selected");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmAndNuke() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm to delete ALL favorites!");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mViewModel.deleteAllFavorites();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                showDataView();
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** This method will make the RecyclerView visible and hide the error message */
    private void showDataView() {
        Log.d(TAG, "Show data view initiated.");
        /* Hide loading indicator */
        binding.pbLoadingIndicatorFavorites.setVisibility(View.INVISIBLE);
        /* Hide show-no-favorites message */
        binding.tvErrorMessageFavorites.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie is visible */
        binding.recyclerviewFavorites.setVisibility(View.VISIBLE);
    }

    /** This method will make the error message visible and hide the RecyclerView */
    private void showNoFavMessage() {
        Log.d(TAG, "Show no-fav message initiated.");
        /* First, hide the currently visible data */
        binding.recyclerviewFavorites.setVisibility(View.INVISIBLE);
        /* Then hide loading indicator */
        binding.pbLoadingIndicatorFavorites.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        binding.tvErrorMessageFavorites.setVisibility(View.VISIBLE);
    }
}
