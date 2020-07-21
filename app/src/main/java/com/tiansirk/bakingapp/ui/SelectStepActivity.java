package com.tiansirk.bakingapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.ActivitySelectRecipeStepBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.util.Arrays;

public class SelectStepActivity extends AppCompatActivity implements FragmentSelectSteps.FragmentSelectStepsListener, FragmentViewStep.FragmentViewStepListener {

    private static final String TAG = SelectStepActivity.class.getSimpleName();

    /** Member vars for fragments of this activity */
    private FragmentSelectSteps mSelectStepFragment;
    private FragmentViewStep mViewStepFragment;

    /** Member vars for data to be shown */
    private Recipe mRecipe;
    private Step[] mSteps;
    private Ingredient[] mIngredients;

    /** Member var for adapter */
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

        /** set the Ingredient and Step objects from the received Recipe */
        mSteps = mRecipe.getSteps();
        mIngredients = mRecipe.getIngredients();

        // Get the fragments
        mSelectStepFragment = new FragmentSelectSteps();
        mViewStepFragment = new FragmentViewStep();

        // Set the data of the select fragment
        mSelectStepFragment.setIngredients(mIngredients);
        mSelectStepFragment.setSteps(mSteps);

        // Set up the select fragment with replace (ie. removes the existing fragment and adds it as new: https://stackoverflow.com/questions/24466302/basic-difference-between-add-and-replace-method-of-fragment/24466345 )
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.select_step_container, mSelectStepFragment);
        ft.commit();

        // start with showing select fragment but not view fragment
        ft.show(mSelectStepFragment);
        ft.hide(mViewStepFragment);

    }

    /** This method is defined in the select fragment's interface to send data from it to the view fragment */
    @Override
    public void onSelectionSent(int position) {
        mViewStepFragment.setSteps(mSteps, position); // saves newly received data
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // detach and re-attach each time makes sure the newly set data will be shown instead of the previously saved data
        ft.detach(mViewStepFragment);
        ft.attach(mViewStepFragment);
        ft.replace(R.id.view_step_container, mViewStepFragment);
        ft.commit();
        ft.hide(mSelectStepFragment);
        ft.show(mViewStepFragment);
    }

    /** This method is defined in the view fragment's interface to send data from it to the select fragment */
    @Override
    public void onBackSelected(Step[] steps) {
        mSelectStepFragment.setSteps(steps);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.select_step_container, mSelectStepFragment);
        ft.hide(mViewStepFragment);
        ft.show(mSelectStepFragment);
        ft.commit();
    }
}