package com.tiansirk.bakingapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.Step;
import com.tiansirk.bakingapp.utils.JsonParser;

public class SelectStepActivity extends AppCompatActivity implements FragmentSelectSteps.FragmentSelectStepsListener, FragmentViewStep.FragmentViewStepListener {

    private static final String TAG = SelectStepActivity.class.getSimpleName();

    /** Member constants for saving state */
    private final String STATE_RECIPE = "state_recipe";
    private final String STATE_STEPS = "state_steps";
    private final String STATE_INGREDIENTS = "state_ingredients";

    /** Member vars for fragments of this activity */
    private FragmentSelectSteps mSelectStepFragment;
    private FragmentViewStep mViewStepFragment;

    /** Member vars for data to be shown */
    private Recipe mRecipe;
    private Step[] mSteps;
    private Ingredient[] mIngredients;

    /** Member var for adapter */
    private StepAndIngredientAdapter mAdapter;

    /** Member var for keeping track of UI state */
    private boolean isDualPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe_step);

        // check what the device is, ie. if it has the view in sw600dp.xml layout, then it is a tablet
        if (findViewById(R.id.view_container_tablet) != null) isDualPane = true;
        else isDualPane = false;

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        // Check if recreating a previously destroyed instance.
        if (savedInstanceState != null) {
            //First recreate saved Recipe
            mRecipe = savedInstanceState.getParcelable(STATE_RECIPE);
            //Second recreate saved Ingredients array
            Parcelable[] parcelables = savedInstanceState.getParcelableArray(STATE_INGREDIENTS);
            mIngredients = new Ingredient[parcelables.length];
            for (int i = 0; i < parcelables.length; ++i) {
                mIngredients[i] = (Ingredient) parcelables[i];
            }
            //Third recreate saved Steps array
            Parcelable[] parcelables2 = savedInstanceState.getParcelableArray(STATE_STEPS);
            mSteps = new Step[parcelables2.length];
            for (int i = 0; i < parcelables2.length; ++i) {
                mSteps[i] = (Step) parcelables2[i];
            }
            //Fourth recreate saved Fragments
            mSelectStepFragment = (FragmentSelectSteps) fragmentManager.getFragment(savedInstanceState, FragmentSelectSteps.TAG);
            mViewStepFragment = (FragmentViewStep) fragmentManager.getFragment(savedInstanceState, FragmentViewStep.TAG);
        } else {
            // Get the received Recipe object
            if(getIntent() != null){
                if(getIntent().getExtras().getParcelable("selected_recipe") !=  null){
                    mRecipe = getIntent().getExtras().getParcelable("selected_recipe");
                }
            }
            // Set the Ingredient and Step objects from the received Recipe
            mSteps = mRecipe.getSteps();
            mIngredients = mRecipe.getIngredients();
            // Get the fragments
            mSelectStepFragment = new FragmentSelectSteps();
            mViewStepFragment = new FragmentViewStep();
            // Set the data of the select fragment
            mSelectStepFragment.setIngredients(mIngredients);
            mSelectStepFragment.setSteps(mSteps);
            mViewStepFragment.setSteps(mSteps, 0); // shows first step as default behavior
            // Setup the fragments according to the device configuration

        }
        if(isDualPane){
            ft.replace(R.id.select_container_tablet, mSelectStepFragment);
            ft.replace(R.id.view_container_tablet, mViewStepFragment);
            ft.commit();
        } else{
            // Set up the select fragment with replace (ie. removes the existing fragment and adds it as new: https://stackoverflow.com/questions/24466302/basic-difference-between-add-and-replace-method-of-fragment/24466345 )
            ft.replace(R.id.select_step_container, mSelectStepFragment);
            ft.replace(R.id.view_step_container, mViewStepFragment);
            ft.commit();
            // Start with showing select fragment but not view fragment
            ft.show(mSelectStepFragment);
            ft.hide(mViewStepFragment);
        }
        setTitle(mRecipe.getName()); //Sets the title in the action bar
    }

    /** This method is defined in the select fragment's interface to send data from it to the
     * view fragment and change its content accordingly */
    @Override
    public void onSelectionSent(int position) {
        mViewStepFragment.setSteps(mSteps, position); // saves newly received data
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(isDualPane){
            ft.replace(R.id.view_container_tablet, mViewStepFragment);
            ft.commit();
        } else{
            // detach and re-attach each time makes sure the newly set data will be shown instead of the previously saved data
            ft.detach(mViewStepFragment);
            Log.d(TAG, "mViewFragment is detached");
            ft.attach(mViewStepFragment);
            Log.d(TAG, "mViewFragment is attached");
            ft.replace(R.id.view_step_container, mViewStepFragment);
            ft.commit();
            ft.hide(mSelectStepFragment);
            ft.show(mViewStepFragment);
        }
    }

    /** This method is defined in the view fragment's interface to send data from it to the
     * select fragment and show it. */
    @Override
    public void onBackSelected(Step[] steps) {
        mSelectStepFragment.setSteps(steps);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(isDualPane){
            ft.replace(R.id.select_container_tablet, mSelectStepFragment);
            ft.commit();
        } else{
            ft.replace(R.id.select_step_container, mSelectStepFragment);
            ft.hide(mViewStepFragment);
            ft.show(mSelectStepFragment);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_RECIPE, mRecipe);
        outState.putParcelableArray(STATE_STEPS, mSteps);
        outState.putParcelableArray(STATE_INGREDIENTS,  mIngredients);

        FragmentManager manager = getSupportFragmentManager();
        if (mSelectStepFragment != null && mSelectStepFragment.isAdded()) {
            manager.putFragment(outState, FragmentSelectSteps.TAG, mSelectStepFragment);
            Log.d(TAG, "SelectFragment is saved in onSaveInstanceState!");
        }
        if(mViewStepFragment != null && mViewStepFragment.isAdded()) {
            manager.putFragment(outState, FragmentViewStep.TAG, mViewStepFragment);
            Log.d(TAG, "ViewFragment is saved in onSaveInstanceState!");
        }
    }

}