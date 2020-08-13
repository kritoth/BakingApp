package com.tiansirk.bakingapp.data;

import com.tiansirk.bakingapp.model.Ingredient;

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
    @Query("SELECT * FROM ingredient_table WHERE recipeName = :recipeName")
    LiveData<List<Ingredient>> loadIngredientsForRecipe(String recipeName);

    @Query("SELECT * FROM ingredient_table")
    LiveData<List<Ingredient>> loadAllIngredients();

    @Query("SELECT COUNT (id) FROM ingredient_table")
    LiveData<Integer> getIngredientsCount();

    //Delete
    @Query("DELETE FROM ingredient_table WHERE recipeName = :recipeName")
    int removeIngredientsOfRecipe(String recipeName); //return the number of rows deleted

    @Delete
    int removeIngredient(Ingredient ingredient);

    @Query("DELETE FROM ingredient_table")
    void deleteAllIngredients();

}
