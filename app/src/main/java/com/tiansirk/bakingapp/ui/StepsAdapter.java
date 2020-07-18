package com.tiansirk.bakingapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Step;
import com.tiansirk.bakingapp.databinding.CardIngredientsItemBinding;
import com.tiansirk.bakingapp.databinding.CardRecipeItemBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepViewHolder>{

    // member var for data
    private Step[] mSteps;

    /** Constructor */
    public StepsAdapter(Context context) {

        this.mSteps = new Step[0];
    }

    @Override
    public int getItemCount() {
        return mSteps.length;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardRecipeItemBinding sBinding = CardRecipeItemBinding.inflate(inflater, parent, false);
        return new StepViewHolder(sBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step currentStep = mSteps[position];
        holder.cardStepBinding.tvRecipeTitle.setText(currentStep.getShortDescription());
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
