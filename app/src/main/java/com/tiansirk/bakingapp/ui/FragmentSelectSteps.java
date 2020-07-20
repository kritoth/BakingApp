package com.tiansirk.bakingapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.SelectStepsFragmentBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentSelectSteps extends Fragment {

    private final static String TAG = FragmentSelectSteps.class.getSimpleName();

    private final String ADAPTER_STATE = "adapter_state";
    private final String INGREDIENTS_LIST_STATE = "ingredients_list_state";
    private final String STEPS_LIST_STATE = "steps_list_state";

    private SelectStepsFragmentBinding binding;
    private RecyclerView mRecyclerViewIngredients;
    private RecyclerView mRecyclerViewSteps;

    private StepAndIngredientAdapter mAdapter;
    private IngredientsAdapter mIngredientsAdapter;
    private StepsAdapter mStepsAdapter;

    private Ingredient[] mIngredients;
    private Step[] mSteps;

    public FragmentSelectSteps() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the saved state (the list of steps) if there is one
        if(savedInstanceState != null) {
            mIngredients = JsonParser.getIngredientsFromJson(savedInstanceState.getString(INGREDIENTS_LIST_STATE));
            mSteps = JsonParser.getStepsFromJson(savedInstanceState.getString(STEPS_LIST_STATE));
        }

        // Inflate the Select-Step fragment layout
        binding = SelectStepsFragmentBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // set up Recyclerview
        setupRecyclerView();

        // initiate StepAndIngredientAdapter
        initAdapter();

        // set ItemClickListener onto StepsAdapter to change to ViewStep Fragment
        setupItemClickListeners();

        return rootView;
    }

    // Get a reference to the RecyclerView in the fragment layout and set it up
    private void setupRecyclerView(){
        mRecyclerViewIngredients = binding.rvSelectStepsIngredients;
        RecyclerView.LayoutManager linearLayoutManagerIngredients = new LinearLayoutManager(getContext());
        mRecyclerViewIngredients.setLayoutManager(linearLayoutManagerIngredients);
        mRecyclerViewIngredients.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerViewIngredients.getContext(),
                LinearLayoutManager.VERTICAL);
        mRecyclerViewIngredients.addItemDecoration(dividerItemDecoration);

        mRecyclerViewSteps = binding.rvSelectStepsSteps;
        RecyclerView.LayoutManager linearLayoutManagerSteps = new LinearLayoutManager(getContext());
        mRecyclerViewSteps.setLayoutManager(linearLayoutManagerSteps);
        mRecyclerViewSteps.setHasFixedSize(true);
    }

    // If a list of Ingredients and Steps exist, set them to the Adapter
    // Otherwise, create a Log statement that indicates that the list was not found
    private void initAdapter(){
        if(mIngredients != null && mSteps != null){
            mIngredientsAdapter = new IngredientsAdapter(getContext());
            mRecyclerViewIngredients.setAdapter(mIngredientsAdapter);
            mIngredientsAdapter.setIngredientsData(mIngredients);

            mStepsAdapter = new StepsAdapter(getContext());
            mRecyclerViewSteps.setAdapter(mStepsAdapter);
            mStepsAdapter.setStepsData(mSteps);
        }
        else {
            Log.wtf(TAG, "This fragment has a null list of Ingredients or Steps");
        }
    }

    public void setIngredients(Ingredient[] ingredients) {
        Log.d(TAG, "setIngredients called");
        this.mIngredients = ingredients;
    }

    public void setSteps(Step[] steps) {
        Log.d(TAG, "setSteps called");
        this.mSteps = steps;
    }

    /** Save the current state of this fragment */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putString(INGREDIENTS_LIST_STATE, JsonParser.serializeIngredientsToJson(mIngredients));
        currentState.putString(STEPS_LIST_STATE, JsonParser.serializeStepsToJson(mSteps));
    }

    /**
     * Sets StepsAdapterItemClickListener to the RecyclerView items
     * according to the respective interface is in {@link StepsAdapter}
     */
    private void setupItemClickListeners(){
        mStepsAdapter.setOnItemClickListener(new StepsAdapter.StepsAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Step clickedStep = mSteps[position];
                Toast.makeText(getContext(), "Clicked step: "+clickedStep.getShortDescription(), Toast.LENGTH_SHORT).show();
                showViewFragment(clickedStep);
            }
        });
    }

    //TODO Problem: this fragment is not getting hide and all clicks create new viewFragment
    /** Create and display the view fragment */
    private void showViewFragment(Step step){
        int idSelectedStep = step.getId();
        FragmentViewStep viewFragment = new FragmentViewStep();
        viewFragment.setSteps(mSteps, idSelectedStep);
        FragmentManager fragmentManager1 = getFragmentManager();
        fragmentManager1.beginTransaction()
                .add(R.id.view_step_container, viewFragment)
                .commit();
        this.getFragmentManager().beginTransaction().hide(this);
    }

    public Ingredient[] getIngredients() {
        return mIngredients;
    }
    public Step[] getSteps() {
        return mSteps;
    }

}
