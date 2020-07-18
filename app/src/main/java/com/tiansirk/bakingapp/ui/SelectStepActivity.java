package com.tiansirk.bakingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.ActivitySelectRecipeStepBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.util.Arrays;

public class SelectStepActivity extends AppCompatActivity {

    private static final String TAG = SelectStepActivity.class.getSimpleName();

    private ActivitySelectRecipeStepBinding binding;

    private FrameLayout mSelectStepContainer;
    private FrameLayout mViewStepContainer;
    private BottomNavigationView mBottomNavigationView;

    private Recipe mRecipe;
    private Step[] mSteps;
    private Ingredient[] mIngredients;
    private StepAndIngredientAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe_step);

        /** get the received {@link Step} objects */
        if(getIntent() != null){
            if(getIntent().getStringExtra("selected_recipe") !=  null){
                mRecipe = JsonParser.getRecipeFromJson(getIntent().getStringExtra("selected_recipe"));
            }
        }
        Log.d(TAG, "Content of mRecipe: " + mRecipe.toString());
        /** set the Ingredient and Step objects from the received Recipe */
        mSteps = mRecipe.getSteps();
        mIngredients = mRecipe.getIngredients();
        Log.d(TAG, "Content of mSteps: " + Arrays.toString(mSteps));
        Log.d(TAG, "Content of mIngredients: " + Arrays.toString(mIngredients));

        // select fragment
        // Create and display the select fragment
        FragmentSelectSteps selectFragment = new FragmentSelectSteps();
        selectFragment.setIngredients(mIngredients);
        selectFragment.setSteps(mSteps);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.select_step_container, selectFragment)
                .commit();

        // view fragment


        // Remove shadow from the Actionbar
        removeShadowFromActionbar();



        // TODO: Set ItemClickListener to change ViewStep Fragment
        //setupItemClickListener();
    }


/*
    private void showViewFragment(){
        // Create and display the view fragment
        FragmentViewStep viewFragment = new FragmentViewStep();
        viewFragment;
        fragmentManager.beginTransaction()
                .add(R.id.body_container, bodyFragment)
                .commit();
    }
*/

    private void removeShadowFromActionbar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setElevation(0);
    }
/*
    private void setupBottomNavigation(){
        mBottomNavigationView = ;

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.previous:
                        showViewStep(Step previousStep);
                        return true;
                    case R.id.home:
                        showSelectSteps();
                        return true;
                    case R.id.next:
                        showViewStep(Step nextStep);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }
*/
}