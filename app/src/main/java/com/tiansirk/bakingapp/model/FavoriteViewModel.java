package com.tiansirk.bakingapp.model;

import android.app.Application;

import com.tiansirk.bakingapp.data.*;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class FavoriteViewModel extends AndroidViewModel {

    private AppDatabase mDb;

    private LiveData<Long> recipeIsExists;
    private LiveData<Integer> numberOfRows;

    private LiveData<List<Recipe>> recipesById; //used in FavoriteActivity to sort by id
    private LiveData<List<Recipe>> recipesByAlphabet; //used in FavoriteActivity to sort by alphabet
    private LiveData<List<Recipe>> recipesByServings; //used in FavoriteActivity to sort by no. of servings
    private LiveData<List<Recipe>> recipesByDate; //used in FavoriteActivity to sort by date

    private LiveData<List<Ingredient>> ingredientsForRecipe;
    private LiveData<List<Step>> stepsForRecipe;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getsInstance(application);
    }

    //Searches if the Recipe exsts or not, returning 1 if exists and 0 if not
    public LiveData<Long> searchRecipe(long id){
        recipeIsExists = mDb.recipeDao().searchRecipe(id);
        return recipeIsExists;
    }

    public LiveData<Integer> countNumberOfRows(){
        numberOfRows = mDb.recipeDao().checkTableIsEmpty();
        return numberOfRows;
    }

    public LiveData<List<Recipe>> getRecipesById() {
        recipesById = mDb.recipeDao().loadAllFavoriteRecipesById();
        return recipesById;
    }

    public LiveData<List<Recipe>> getRecipesByAlphabet() {
        recipesByAlphabet = mDb.recipeDao().loadAllFavoriteRecipesAlphabetically();
        return recipesByAlphabet;
    }

    public LiveData<List<Recipe>> getRecipesByServings() {
        recipesByServings = mDb.recipeDao().loadAllFavoriteRecipesByServings();
        return recipesByServings;
    }

    public LiveData<List<Recipe>> getRecipesByDate() {
        recipesByDate = mDb.recipeDao().loadAllFavoriteRecipesByDateAdded();
        return recipesByDate;
    }

    public LiveData<List<Ingredient>> getIngredientsForRecipe(int id){
        ingredientsForRecipe = mDb.ingredientDao().loadIngredientsForRecipe(id);
        return ingredientsForRecipe;
    }

    public LiveData<List<Step>> getStepsForRecipe(int id){
        stepsForRecipe = mDb.stepDao().loadStepsForRecipe(id);
        return stepsForRecipe;
    }

    public void deleteAllFavorites(){
        mDb.recipeDao().deleteAllFavoriteRecipe();
    }
}
