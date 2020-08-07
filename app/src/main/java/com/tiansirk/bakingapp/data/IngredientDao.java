package com.tiansirk.bakingapp.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface IngredientDao {
    //Create
    @Insert
    long insertIngredient(Ingredient ingredient);

    //Read
    @Query("SELECT * FROM ingredient_table WHERE recipeId = :id")
    LiveData<List<Ingredient>> loadIngredientsForRecipe(int id);

    @Query("SELECT * FROM ingredient_table")
    List<Ingredient> loadAllIngredients();

    @Query("SELECT COUNT (id) FROM ingredient_table")
    int getIngredientsCount();

    //Update


    //Delete
    @Query("DELETE FROM ingredient_table WHERE recipeId = :id")
    void removeIngredientsOfRecipe(int id);

    @Delete
    int removeIngredient(Ingredient ingredient);

    @Query("DELETE FROM ingredient_table")
    void deleteAllIngredients();


    //For testing only
    @Query("SELECT * FROM ingredient_table")
    List<Ingredient> queryAllIngreds();
}
