package com.tiansirk.bakingapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.databinding.ActivityFavoriteBinding;
import com.tiansirk.bakingapp.model.FavoriteViewModel;
import com.tiansirk.bakingapp.model.FavoriteViewModelFactory;
import com.tiansirk.bakingapp.utils.AppExecutors;

import java.util.Arrays;
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
    private static final String DEFAULT_SORT_PREFERENCE = "id";

    private ActivityFavoriteBinding binding;

    private RecipeAdapter mAdapter;
    private String mSortingPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initViews();
        initRecyclerView();

        loadRecipesFromDB();

        Log.d(TAG, "Favorite Recipes:\n");
    }

    private void initViews() {
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
        mAdapter = new RecipeAdapter(this);
        binding.recyclerviewFavorites.setAdapter(mAdapter);
    }


    private void loadRecipesFromDB() {
        binding.pbLoadingIndicatorFavorites.setVisibility(View.VISIBLE);
        FavoriteViewModelFactory factory = new FavoriteViewModelFactory(getApplication());
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        final FavoriteViewModel viewModel = provider.get(FavoriteViewModel.class);
        // Querying the Database according to user's preference
        // TODO: Ingredient and Step -s are not queried here, only RecipeDAO
        if(mSortingPreference == null || mSortingPreference.isEmpty()){
            mSortingPreference = DEFAULT_SORT_PREFERENCE;
        }
        switch (mSortingPreference) {
            case "id":
                viewModel.getRecipesById().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(List<Recipe> recipes) {
                        showDataView();
                        mAdapter.setRecipesData(recipes);
                    }
                });
                Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));
                break;
            case "abc":
                viewModel.getRecipesByAlphabet().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(List<Recipe> recipes) {
                        showDataView();
                        mAdapter.setRecipesData(recipes);
                    }
                });
                Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));
                break;
            case "servings":
                viewModel.getRecipesByServings().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(List<Recipe> recipes) {
                        showDataView();
                        mAdapter.setRecipesData(recipes);
                    }
                });
                Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));
                break;
            case "date":
                viewModel.getRecipesByDate().observe(this, new Observer<List<Recipe>>() {
                    @Override
                    public void onChanged(List<Recipe> recipes) {
                        showDataView();
                        mAdapter.setRecipesData(recipes);
                    }
                });
                Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));
                break;
            case "delete":
                confirmAndNuke(viewModel);
                Log.d(TAG, "\n***mAdapter List size: " + mAdapter.getRecipesData().size() + "\nFirst element: " + mAdapter.getRecipesData().get(0));
                break;
        }
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
            case R.id.sort_alphabetical:
                mSortingPreference = "abc";
                Log.d(TAG, "Sort alphabetical menu item selected");
                return true;
            case R.id.sort_id:
                mSortingPreference = "id";
                Log.d(TAG, "Sort by id menu item selected");
                return true;
            case R.id.sort_servings_number:
                mSortingPreference = "servings";
                Log.d(TAG, "Sort by servings number menu item selected");
                return true;
            case R.id.sort_date:
                mSortingPreference = "date";
                Log.d(TAG, "Sort date added menu item selected");
                return true;
            case R.id.delete_all:
                mSortingPreference = "delete";
                Log.d(TAG, "Delete All menu item selected");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmAndNuke(final FavoriteViewModel viewModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm to delete ALL favorites!");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        viewModel.deleteAllFavorites();
                    }
                });
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

    /**
     * This method will make the RecyclerView visible and hide the error message
     */
    private void showDataView() {
        /* Hide loading indicator */
        binding.pbLoadingIndicatorFavorites.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie is visible */
        binding.recyclerviewFavorites.setVisibility(View.VISIBLE);
    }
}
