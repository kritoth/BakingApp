package com.tiansirk.bakingapp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.databinding.ItemIngredientsBinding;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>{

    // member var for data
    private Ingredient[] mIngredients;

    /** Constructor */
    public IngredientsAdapter(Context context) {
        this.mIngredients = new Ingredient[0];
    }

    @Override
    public int getItemCount() {
        return mIngredients.length;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemIngredientsBinding iBinding = ItemIngredientsBinding.inflate(inflater, parent, false);
        return new IngredientsAdapter.IngredientViewHolder(iBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient currentIngredient = mIngredients[position];
        holder.populate(currentIngredient);
    }

    /** The custom ViewHolder class for Ingredient */
    public class IngredientViewHolder extends RecyclerView.ViewHolder{
        /** member var for the item layout */
        public ItemIngredientsBinding cardIngredientsBinding;

        /** Constructor for the custom ViewHolder */
        public IngredientViewHolder(@NonNull ItemIngredientsBinding itemViewBinding) {
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

    public Ingredient[] getIngredientsData() {
        return mIngredients;
    }

    public void setIngredientsData(Ingredient[] ingredients) {
        this.mIngredients = ingredients;
    }

}
