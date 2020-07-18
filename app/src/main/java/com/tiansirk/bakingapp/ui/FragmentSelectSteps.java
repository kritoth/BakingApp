package com.tiansirk.bakingapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.R;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.SelectStepsFragmentBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentSelectSteps extends Fragment {

    private final static String TAG = FragmentSelectSteps.class.getSimpleName();

    private final String ADAPTER_STATE = "adapter_state";
    private final String INGREDIENTS_LIST_STATE = "ingredients_list_state";
    private final String STEPS_LIST_STATE = "steps_list_state";

    private SelectStepsFragmentBinding binding;
    private RecyclerView mRecyclerView;

    private StepAndIngredientAdapter mAdapter;
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

        return rootView;
    }

    private void setupRecyclerView(){
        // Get a reference to the RecyclerView in the fragment layout and set it up
        mRecyclerView = binding.rvSelectSteps;
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
    }

    private void initAdapter(){
        // If a list of Ingredients and Steps exist, set them to the Adapter
        // Otherwise, create a Log statement that indicates that the list was not found
        if(mIngredients != null && mSteps != null){
            // Set the data to the list item at the stored index
            mAdapter = new StepAndIngredientAdapter(getContext());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setIngredientsData(mIngredients);
            mAdapter.setStepsData(mSteps);
        }
        else {
            Log.wtf(TAG, "This fragment has a null list of Ingredients or Steps");
        }
    }

    public Ingredient[] getIngredients() {
        return mIngredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        Log.d(TAG, "setIngredients called");
        this.mIngredients = ingredients;
    }

    public Step[] getSteps() {
        return mSteps;
    }

    public void setSteps(Step[] steps) {
        Log.d(TAG, "setSteps called");
        this.mSteps = steps;
    }

    /**
     * Save the current state of this fragment
     */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putString(INGREDIENTS_LIST_STATE, JsonParser.serializeIngredientsToJson(mIngredients));
        currentState.putString(STEPS_LIST_STATE, JsonParser.serializeStepsToJson(mSteps));
    }

}
