package com.tiansirk.bakingapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.databinding.CardItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private static final String LOG_TAG = RecipeAdapter.class.getSimpleName();

    private List<Recipe> mRecipes;

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe clickedRecipe);
    }

    private final RecipeAdapterOnClickHandler mClickHandler;

    /**
     * Constructor
     *
     * @param onClickHandler registers the click handler
     */
    public RecipeAdapter(RecipeAdapterOnClickHandler onClickHandler) {
        mClickHandler = onClickHandler;
        this.mRecipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new RecipeViewHolder(CardItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipeForThisItem = mRecipes.get(position);
        holder.cardBinding.tvRecipeTitle.setText(recipeForThisItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public CardItemBinding cardBinding;

        public RecipeViewHolder(@NotNull CardItemBinding cardItemBinding) {
            super(cardItemBinding.getRoot());
            this.cardBinding = cardItemBinding;
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            Recipe recipe = mRecipes.get(clickedPosition);
            mClickHandler.onClick(recipe);
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }

    /**
     * This method is to set the data of the Recipes on a RecipeAdapter if we've already
     * created one. This is handy when there is new data but don't needed to create a
     * new RecipeAdapter to display it.
     *
     * @param recipes The list of Recipes to set to the Adapter
     */
    public void setRecipesData (List<Recipe> recipes) {
        this.mRecipes = recipes;
    }
}
