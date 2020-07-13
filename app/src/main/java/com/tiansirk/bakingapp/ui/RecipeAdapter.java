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
    // member var for own custom clickListener
    private RecipeAdapterItemClickListener mClickListener;
    // member var for own custom longClickListener
    private RecipeAdapterItemLongClickListener mLongClickListener;


     /** The interface that receives onClick messages. */
     public interface RecipeAdapterItemClickListener {
        void onItemClick(int position);
     }
    /** Sets the received custom item click listener to the member custom item click listener
     * @param onItemClickListener
     */
     public void setOnItemClickListener(RecipeAdapterItemClickListener onItemClickListener){
        mClickListener = onItemClickListener;
     }


    /** The interface that receives onLongClick messages. */
    public interface RecipeAdapterItemLongClickListener {
        void onItemLongClick(int position);
    }
    /** Sets the received custom item long click listener to the member custom item long click listener
     * @param onItemLongClickListener
     */
    public void setOnItemLongClickListener(RecipeAdapterItemLongClickListener onItemLongClickListener){
        mLongClickListener = onItemLongClickListener;
    }


    /** Constructor */
    public RecipeAdapter(Context context) {
        this.mRecipes = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        CardItemBinding itemBinding = CardItemBinding.inflate(inflater, parent, false);
        return new RecipeViewHolder(itemBinding, mClickListener, mLongClickListener);
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

    /** The custom ViewHolder class */
    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        /** member var for the item layout */
        public CardItemBinding cardBinding;

        /** Constructor for the custom ViewHolder */
        public RecipeViewHolder(@NotNull CardItemBinding cardItemBinding,
                                final RecipeAdapterItemClickListener clickListener,
                                final RecipeAdapterItemLongClickListener longClickListener) {
            super(cardItemBinding.getRoot());
            this.cardBinding = cardItemBinding;

            /** setting the clickListener to the card_view */
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
            /** setting the longClickListener to the card_view */
            cardBinding.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(clickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            longClickListener.onItemLongClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }

    /**
     * This method is to set the data of the Recipes on a RecipeAdapter if thers is already
     * created one. This is handy when there is new data but don't needed to create a
     * new RecipeAdapter to display it.
     *
     * @param recipes The list of Recipes to set to the Adapter
     */
    public void setRecipesData (List<Recipe> recipes) {
        this.mRecipes = recipes;
    }

    /** Getter method for the Recipes data present in the adapter */
    public List<Recipe> getRecipesData() {
        return mRecipes;
    }
}
