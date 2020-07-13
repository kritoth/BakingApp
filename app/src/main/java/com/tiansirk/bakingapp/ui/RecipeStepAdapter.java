package com.tiansirk.bakingapp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    // member var for data
    private List<Step> mSteps;

    /** Constructor */
    public RecipeStepAdapter(Context context) {
        this.mSteps = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mSteps.size();
    }

    /** The custom ViewHolder class */
    public class RecipeStepViewHolder extends RecyclerView.ViewHolder{

        public RecipeStepViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * This method is to set the data of the Steps on a RecipeStepAdapter if there is already
     * created one. This is handy when there is new data but don't needed to create a
     * new RecipeStepAdapter to display it.
     *
     * @param steps The list of Steps to set to the Adapter
     */
    public void setRecipeStepsData (List<Step> steps) {
        this.mSteps = steps;
    }

    /**
     * Getter method for the Steps data present in the adapter */
    public List<Step> getRecipeStepsData() {
        return mSteps;
    }
}
