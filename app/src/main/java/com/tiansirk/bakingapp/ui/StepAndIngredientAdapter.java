package com.tiansirk.bakingapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.CardIngredientsItemBinding;
import com.tiansirk.bakingapp.databinding.CardRecipeItemBinding;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StepAndIngredientAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // flags for deciding view type
    private final int VIEW_TYPE_INGREDIENT = 0;
    private final int VIEW_TYPE_STEP = 1;

    // member var for data
    private Ingredient[] mIngredients;
    private Step[] mSteps;

    /** Constructor */
    public StepAndIngredientAdapter(Context context) {
        this.mIngredients = new Ingredient[0];
        this.mSteps = new Step[0];
    }

    @Override
    public int getItemCount() {
        return mIngredients.length + mSteps.length;
    }

    @Override
    public int getItemViewType(int position) {
        if(position < mIngredients.length){
            return VIEW_TYPE_INGREDIENT;
        }
        if(position - mIngredients.length < mSteps.length){
            return  VIEW_TYPE_STEP;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_INGREDIENT:
                CardIngredientsItemBinding iBinding = CardIngredientsItemBinding.inflate(inflater, parent, false);
                return new IngredientViewHolder(iBinding);
            case VIEW_TYPE_STEP:
                CardRecipeItemBinding sBinding = CardRecipeItemBinding.inflate(inflater, parent, false);
                return new StepViewHolder(sBinding);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof IngredientViewHolder) {
            Ingredient currentIngredient = mIngredients[position];
            ((IngredientViewHolder) holder).populate(currentIngredient);
        }
        if(holder instanceof StepViewHolder) {
            Step currentStep = mSteps[position - mIngredients.length];
            ((StepViewHolder) holder).cardStepBinding.tvRecipeTitle.setText(currentStep.getShortDescription());
        }
    }

    /** The custom ViewHolder class for Ingredient */
    public class IngredientViewHolder extends RecyclerView.ViewHolder{
        /** member var for the item layout */
        public CardIngredientsItemBinding cardIngredientsBinding;

        /** Constructor for the custom ViewHolder */
        public IngredientViewHolder(@NonNull CardIngredientsItemBinding itemViewBinding) {
            super(itemViewBinding.getRoot());
            this.cardIngredientsBinding = itemViewBinding;
        }

        /** Populate the widgets of the itemview with data of the Ingredient object */
        public void populate(Ingredient ingredient){
            cardIngredientsBinding.tvIngredientName.setText(ingredient.getIngredient());
            cardIngredientsBinding.tvIngredientQuantity.setText(Double.toString(ingredient.getQuantity()));
            cardIngredientsBinding.tvIngredientMeasure.setText(ingredient.getMeasure());
        }
    }

    /** The custom ViewHolder class for Step */
    public class StepViewHolder extends RecyclerView.ViewHolder{
        /** member var for the item layout */
        public CardRecipeItemBinding cardStepBinding;

        /** Constructor for the custom ViewHolder */
        public StepViewHolder(@NonNull CardRecipeItemBinding itemViewBinding) {
            super(itemViewBinding.getRoot());
            this.cardStepBinding = itemViewBinding;
        }

    }

    public Ingredient[] getIngredientsData() {
        return mIngredients;
    }

    public void setIngredientsData(Ingredient[] ingredients) {
        this.mIngredients = ingredients;
    }

    /**
     * This method is to set the data of the Steps on a StepAndIngredientAdapter if there is already
     * created one. This is handy when there is new data but don't needed to create a
     * new StepAndIngredientAdapter to display it.
     *
     * @param steps The list of Steps to set to the Adapter
     */
    public void setStepsData (Step[] steps) {
        this.mSteps = steps;
    }

    /** Getter method for the Steps data present in the adapter */
    public Step[] getStepsData() {
        return mSteps;
    }
}
