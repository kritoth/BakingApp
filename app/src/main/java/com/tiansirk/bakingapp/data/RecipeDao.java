package com.tiansirk.bakingapp.data;

import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

@Dao
public interface RecipeDao {
    //Create
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertRecipeToFavorites(Recipe recipe);

    //Read
    @Query("SELECT EXISTS(SELECT 1 FROM recipe_table WHERE name = :name)")
    LiveData<Long> searchRecipe(String name); //Searches if the Recipe exsts or not, returning 1 if exists and 0 if not

    @Query("SELECT COUNT(*) FROM recipe_table")
    LiveData<Integer> checkTableIsEmpty(); // Counts the number of rows exists in the table

    @Query("SELECT * FROM recipe_table ORDER BY name ASC")
    LiveData<List<Recipe>> loadAllFavoriteRecipesAlphabetically();

    @Query("SELECT * FROM recipe_table ORDER BY servings ASC")
    LiveData<List<Recipe>> loadAllFavoriteRecipesByServings();

    @Transaction
    @Query("SELECT * FROM recipe_table ORDER BY dateAddedToFav DESC")
    LiveData<List<RecipeWithIngredsSteps>> loadAllFavoriteRecipesByDateAdded();

    @Transaction
    @Query("SELECT * FROM recipe_table WHERE name = :name")
    LiveData<RecipeWithIngredsSteps> loadFavoriteRecipe(String name);

    //Delete
    @Delete
    int removeFavoriteRecipe(Recipe recipe); //return the number of rows deleted

    @Query("DELETE FROM recipe_table")
    int deleteAllFavoriteRecipe();


    //Queries without LiveData
    @Query("SELECT * FROM recipe_table ORDER BY roomId ASC")
    List<Recipe> queryAllFavoriteRecipesById();

    @Query("SELECT * FROM recipe_table ORDER BY name ASC")
    List<Recipe> queryAllFavoriteRecipesAlphabetically();

    @Query("SELECT * FROM recipe_table ORDER BY servings ASC")
    List<Recipe> queryAllFavoriteRecipesByServings();

    @Query("SELECT * FROM recipe_table ORDER BY dateAddedToFav DESC")
    List<Recipe> queryAllFavoriteRecipesByDateAdded();

    @Query("SELECT EXISTS(SELECT 1 FROM recipe_table WHERE roomId = :id)")
    int queryIfRecipeExists(long id);
}
