package com.tiansirk.bakingapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Step;
import com.tiansirk.bakingapp.databinding.FragmentSelectStepsBinding;
import com.tiansirk.bakingapp.utils.JsonParser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentSelectSteps extends Fragment {

    public final static String TAG = FragmentSelectSteps.class.getSimpleName();

    /** Member constants for saving state */
    private final String ADAPTER_STATE = "adapter_state";
    private final String INGREDIENTS_LIST_STATE = "ingredients_list_state";
    private final String STEPS_LIST_STATE = "steps_list_state";

    /** Member vars for views */
    private FragmentSelectStepsBinding binding;
    private RecyclerView mRecyclerViewIngredients;
    private RecyclerView mRecyclerViewSteps;

    /** Member vars for adapters */
    private StepAndIngredientAdapter mAdapter;
    private IngredientsAdapter mIngredientsAdapter;
    private StepsAdapter mStepsAdapter;

    /** Member vars for data to be shown */
    private Ingredient[] mIngredients;
    private Step[] mSteps;

    /** Member var for own custom data-to-be-sent listener */
    private FragmentSelectStepsListener listener;

    /** The interface that receives onClick messages */
    public interface FragmentSelectStepsListener{
        void onSelectionSent(int position);
    }

    /* Compulsory empty constructor */
    public FragmentSelectSteps() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Load the saved state (the list of steps) if there is one
        if(savedInstanceState != null) {
            Log.d(TAG, "onCreateView's savedInstanceState is called");
            mIngredients = JsonParser.getIngredientsFromJson(savedInstanceState.getString(INGREDIENTS_LIST_STATE));
            mSteps = JsonParser.getStepsFromJson(savedInstanceState.getString(STEPS_LIST_STATE));
        }

        // Inflate the Select-Step fragment layout
        binding = FragmentSelectStepsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // set up Recyclerview
        setupRecyclerView();

        // initiate necessary Adapters
        initAdapter();

        // set ItemClickListener onto StepsAdapter to change to ViewStep Fragment
        setupItemClickListeners();

        return rootView;
    }

    /** Get a reference to the RecyclerView in the fragment layout and set it up. */
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

    /** If a list of Ingredients and Steps exist, set them to the Adapter
     * Otherwise, create a Log statement that indicates that the list was not found
     */
    private void initAdapter(){
        mIngredientsAdapter = new IngredientsAdapter(getContext());
        mRecyclerViewIngredients.setAdapter(mIngredientsAdapter);

        mStepsAdapter = new StepsAdapter(getContext());
        mRecyclerViewSteps.setAdapter(mStepsAdapter);

        if(mIngredients != null && mSteps != null){
            mIngredientsAdapter.setIngredientsData(mIngredients);
            mStepsAdapter.setStepsData(mSteps);
        }
        else {
            Log.wtf(TAG, "This fragment has a null list of Ingredients or Steps");
        }
    }

    /**
     * Sets StepsAdapterItemClickListener to the RecyclerView items
     * according to the respective interface is in {@link StepsAdapter}
     */
    private void setupItemClickListeners(){
        if(mStepsAdapter == null) initAdapter();
        mStepsAdapter.setOnItemClickListener(new StepsAdapter.StepsAdapterItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Step clickedStep = mSteps[position];
                Log.d(TAG, "Clicked step: #" + position + ", " + clickedStep.getShortDescription());
                // at click the host Activity is notified and the clicked position is sent
                listener.onSelectionSent(position);
            }
        });
    }

    /** When this fragment is attached to its host activity, ie {@link SelectStepActivity} the listener interface is connected
     * If not then an error exception is thrown to notify the developer.
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentSelectStepsListener) {
            listener = (FragmentSelectStepsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentSelectStepsListener");
        }
    }

    /** When this fragment is detached from the host, the listeners is set to null, to decouple. */
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /** Save the current state of this fragment */
    @Override
    public void onSaveInstanceState(Bundle currentState) {
        Log.d(TAG, "onSaveInstanceState is called");
        currentState.putString(INGREDIENTS_LIST_STATE, JsonParser.serializeIngredientsToJson(mIngredients));
        currentState.putString(STEPS_LIST_STATE, JsonParser.serializeStepsToJson(mSteps));
    }

    /** Setter for member var */
    public void setIngredients(Ingredient[] ingredients) {
        Log.d(TAG, "setIngredients called");
        this.mIngredients = ingredients;
    }

    /** Setter for member var */
    public void setSteps(Step[] steps) {
        Log.d(TAG, "setSteps called");
        this.mSteps = steps;
    }


    /** Shows or hides the
     * @param fragment according to its current state */
    private void showHideFragment(Fragment fragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if(fragment.isHidden()) ft.show(fragment);
        else ft.hide(fragment);
        ft.commit();
    }
}
