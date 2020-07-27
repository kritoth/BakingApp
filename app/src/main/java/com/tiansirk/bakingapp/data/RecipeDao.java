package com.tiansirk.bakingapp.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RecipeDao {
    //Create
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRecipeToFavorites(Recipe recipe);

    //Read
    @Query("SELECT EXISTS(SELECT 1 FROM recipe_table WHERE id = :id)")
    LiveData<Integer> searchRecipe(int id); //Searches if the Recipe exsts or not, returning 1 if exists and 0 if not

    @Query("SELECT * FROM recipe_table ORDER BY id ASC")
    LiveData<List<Recipe>> loadAllFavoriteRecipesById();

    @Query("SELECT * FROM recipe_table ORDER BY name ASC")
    LiveData<List<Recipe>> loadAllFavoriteRecipesAlphabetically();

    @Query("SELECT * FROM recipe_table ORDER BY servings ASC")
    LiveData<List<Recipe>> loadAllFavoriteRecipesByServings();

    @Query("SELECT * FROM recipe_table ORDER BY dateAddedToFav DESC")
    LiveData<List<Recipe>> loadAllFavoriteRecipesByDateAdded();

    @Query("SELECT * FROM recipe_table WHERE id = :id")
    LiveData<Recipe> loadFavoriteRecipe(int id);

    //Update


    //Delete
    @Delete
    int removeFavoriteRecipe(Recipe recipe);

    @Query("DELETE FROM recipe_table")
    void deleteAllFavoriteRecipe();
}
