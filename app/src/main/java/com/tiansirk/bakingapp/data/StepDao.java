package com.tiansirk.bakingapp.data;

import com.tiansirk.bakingapp.model.Step;

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
    @Query("SELECT * FROM step_table WHERE recipeName = :recipeName")
    LiveData<List<Step>> loadStepsForRecipe(String recipeName);

    @Query("SELECT * FROM step_table")
    LiveData<List<Step>> loadAllSteps();

    @Query("SELECT COUNT (id) FROM step_table")
    LiveData<Integer> getStepsCount();

    //Delete
    @Query("DELETE FROM step_table WHERE recipeName = :recipeName")
    int removeStepsOfRecipe(String recipeName); //return the number of rows deleted

    @Delete
    int removeStep(Step step);

    @Query("DELETE FROM step_table")
    void deleteAllSteps();

}
