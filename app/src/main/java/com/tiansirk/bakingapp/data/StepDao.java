package com.tiansirk.bakingapp.data;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StepDao {
    //Create
    @Insert
    long insertStep(Step step);

    //Read
    @Query("SELECT * FROM step_table WHERE recipeId = :id")
    LiveData<List<Step>> loadStepsForRecipe(int id);

    @Query("SELECT * FROM step_table")
    List<Step> loadAllSteps();

    @Query("SELECT COUNT (id) FROM step_table")
    int getStepsCount();

    //Update


    //Delete
    @Query("DELETE FROM step_table WHERE recipeId = :id")
    void removeStepsOfRecipe(int id);

    @Delete
    int removeStep(Step step);

    @Query("DELETE FROM step_table")
    void deleteAllSteps();
}
