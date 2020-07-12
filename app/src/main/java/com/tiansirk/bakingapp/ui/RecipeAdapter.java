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
    // member var for data
    private List<Recipe> mRecipes;
    // member var for own custom clicklistener
    private RecipeAdapterItemClickListener mClickListener;

     /** The interface that receives onClick messages.
     */
     public interface RecipeAdapterItemClickListener {
        void onItemClick(int position);
     }

    /**
     * Sets the received custom item click listener to the member custom item click listener
     * @param onItemClickListener
     */
     public void setOnItemClickListener(RecipeAdapterItemClickListener onItemClickListener){
        mClickListener = onItemClickListener;
     }

    /**
     * Constructor
     */
    public RecipeAdapter(Context context) {
        this.mRecipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardItemBinding itemBinding = CardItemBinding.inflate(inflater, parent, false);
        return new RecipeViewHolder(itemBinding, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeAdapter.RecipeViewHolder holder, int position) {
        Recipe currentItem = mRecipes.get(position);
        holder.cardBinding.tvRecipeTitle.setText(currentItem.getName());
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        public CardItemBinding cardBinding;

        public RecipeViewHolder(@NotNull CardItemBinding cardItemBinding, final RecipeAdapterItemClickListener clickListener) {
            super(cardItemBinding.getRoot());
            this.cardBinding = cardItemBinding;

            cardBinding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(clickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });

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

    /**
     * Getter method for the Recipes data present in the adapter
     * @return
     */
    public List<Recipe> getRecipesData() {
        return mRecipes;
    }
}

/**


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


}
 */
