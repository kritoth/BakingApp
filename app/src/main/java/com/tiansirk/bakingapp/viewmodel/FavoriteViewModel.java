package com.tiansirk.bakingapp.viewmodel;

import android.app.Application;

import com.tiansirk.bakingapp.data.*;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.RecipeWithIngredsSteps;
import com.tiansirk.bakingapp.model.Step;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FavoriteViewModel extends AndroidViewModel {

    private Repository mRepository;

    private LiveData<Long> recipeIsExists; // use it to check if the given recipe exists  in recipe_table
    private LiveData<Integer> numberOfRowsInRecipeTable; //use it to check the num of entries in recipe_table

    private LiveData<List<Recipe>> recipesByAlphabet; //used in FavoriteActivity to sort by alphabet
    private LiveData<List<Recipe>> recipesByServings; //used in FavoriteActivity to sort by no. of servings
    private LiveData<List<RecipeWithIngredsSteps>> recipesByDate; //used in FavoriteActivity to sort by date

    private LiveData<List<Ingredient>> ingredientsForRecipe;
    private LiveData<List<Step>> stepsForRecipe;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    /* Inserts */
    public void insertRecipeToFavorites(Recipe recipe){
        mRepository.insertRecipeToFavorites(recipe);
    }

    /* Deletes */
    public void deleteRecipe(Recipe recipe){
        mRepository.deleteRecipe(recipe);
    }
    public void deleteAllFavorites(){
        mRepository.deleteAllFavorites();
    }

    /* Queries */
    //Searches if the Recipe exsts or not, returning 1 if exists and 0 if not
    public LiveData<Long> searchRecipe(String name){
        recipeIsExists = mRepository.searchRecipe(name);
        return recipeIsExists;
    }

    public LiveData<Integer> countNumberOfRows(){
        numberOfRowsInRecipeTable = mRepository.countNumberOfRows();
        return numberOfRowsInRecipeTable;
    }

    public LiveData<List<RecipeWithIngredsSteps>> getRecipesByDate() {
        recipesByDate = mRepository.getRecipesByDate();
        return recipesByDate;
    }

}
