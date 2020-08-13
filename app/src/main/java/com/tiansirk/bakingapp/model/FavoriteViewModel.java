package com.tiansirk.bakingapp.model;

import android.app.Application;

import com.tiansirk.bakingapp.data.*;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FavoriteViewModel extends AndroidViewModel {

    private Repository mRepository;

    private LiveData<Long> recipeIsExists; // use it to check if the given recipe exists  in recipe_table
    private LiveData<Integer> numberOfRowsInRecipeTable; //use it to check the num of entries in recipe_table

    private LiveData<List<Recipe>> recipesById; //used in FavoriteActivity to sort by id
    private LiveData<List<Recipe>> recipesByAlphabet; //used in FavoriteActivity to sort by alphabet
    private LiveData<List<Recipe>> recipesByServings; //used in FavoriteActivity to sort by no. of servings
    private LiveData<List<Recipe>> recipesByDate; //used in FavoriteActivity to sort by date

    private LiveData<List<Ingredient>> ingredientsForRecipe;
    private LiveData<List<Step>> stepsForRecipe;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    /* Inserts */
    public long insertRecipeToFavorites(Recipe recipe){
        return mRepository.insertRecipeToFavorites(recipe);
    }

    /* Deletes */
    public int[] deleteRecipe(Recipe recipe){
        return mRepository.deleteRecipe(recipe);
    }
    public void deleteAllFavorites(){
        mRepository.deleteAllFavorites();
    }

    /* Queries */
    //Searches if the Recipe exsts or not, returning 1 if exists and 0 if not
    public LiveData<Long> searchRecipe(long id){
        recipeIsExists = mRepository.searchRecipe(id);
        return recipeIsExists;
    }

    public LiveData<Integer> countNumberOfRows(){
        numberOfRowsInRecipeTable = mRepository.countNumberOfRows();
        return numberOfRowsInRecipeTable;
    }

    public LiveData<List<Recipe>> getRecipesById() {
        recipesById = mRepository.getRecipesById();
        return recipesById;
    }

    public LiveData<List<Recipe>> getRecipesByAlphabet() {
        recipesByAlphabet = mRepository.getRecipesByAlphabet();
        return recipesByAlphabet;
    }

    public LiveData<List<Recipe>> getRecipesByServings() {
        recipesByServings = mRepository.getRecipesByServings();
        return recipesByServings;
    }

    public LiveData<List<Recipe>> getRecipesByDate() {
        recipesByDate = mRepository.getRecipesByDate();
        return recipesByDate;
    }

    public LiveData<List<Ingredient>> getIngredientsForRecipe(int id){
        ingredientsForRecipe = mRepository.getIngredientsForRecipe(id);
        return ingredientsForRecipe;
    }

    public LiveData<List<Step>> getStepsForRecipe(int id){
        stepsForRecipe = mRepository.getStepsForRecipe(id);
        return stepsForRecipe;
    }
}
