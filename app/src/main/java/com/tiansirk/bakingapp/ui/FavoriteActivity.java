package com.tiansirk.bakingapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.tiansirk.bakingapp.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class FavoriteActivity extends AppCompatActivity {

    private static final String TAG = FavoriteActivity.class.getSimpleName();
    private static final String KEY_ACTIVITY_INTENT = "CHOSEN_RECIPE";
    private static final String DEFAULT_SORT_PREFERENCE = "id";

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;

    private RecipeAdapter mAdapter;
    private String mSortingPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
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
                Log.d(TAG, "Sort alphabetical menu item selected");
                return true;
            case R.id.sort_id:
                Log.d(TAG, "Sort by id menu item selected");
                return true;
            case R.id.sort_servings_number:
                Log.d(TAG, "Sort by servings number menu item selected");
                return true;
            case R.id.sort_date:
                Log.d(TAG, "Sort date added menu item selected");
                return true;
            case R.id.delete_all:
                Log.d(TAG, "Delete All menu item selected");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
