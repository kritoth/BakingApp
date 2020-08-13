package com.tiansirk.bakingapp.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.tiansirk.bakingapp.utils.AppExecutors;

import java.util.List;

import androidx.lifecycle.LiveData;

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();
    private AppDatabase mDb;

    private LiveData<Long> recipeIsExists; // use it to check if the given recipe exists  in recipe_table
    private LiveData<Integer> numberOfRowsInRecipeTable; //use it to check the num of entries in recipe_table

    private LiveData<List<Recipe>> recipesById; //used in FavoriteActivity to sort by id
    private LiveData<List<Recipe>> recipesByAlphabet; //used in FavoriteActivity to sort by alphabet
    private LiveData<List<Recipe>> recipesByServings; //used in FavoriteActivity to sort by no. of servings
    private LiveData<List<Recipe>> recipesByDate; //used in FavoriteActivity to sort by date

    private LiveData<List<Ingredient>> ingredientsForRecipe;
    private LiveData<List<Step>> stepsForRecipe;

    public Repository(Application application) {
        this.mDb = AppDatabase.getsInstance(application);
    }

    /* Inserts */
    public long insertRecipeToFavorites(final Recipe recipe) {
        final long[] rowIdRecipe = new long[1];
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                rowIdRecipe[0] = mDb.recipeDao().insertRecipeToFavorites(recipe);
                insertIngredientsOfRecipe(rowIdRecipe[0], recipe, mDb.ingredientDao());
                insertStepsOfRecipe(rowIdRecipe[0], recipe, mDb.stepDao());
            }
        });
        return rowIdRecipe[0];
    }
    private static boolean insertIngredientsOfRecipe(long foreignKey, Recipe recipe, IngredientDao ingredientDao){
        long[] rowIdOfInsertedIngredients = new long[recipe.getIngredients().length];
        for(int i=0;i<recipe.getIngredients().length;i++) {
            Ingredient currIngredietn = recipe.getIngredients()[i];
            currIngredietn.setRecipeId(foreignKey);
            rowIdOfInsertedIngredients[i] = ingredientDao.insertIngredient(currIngredietn);
        }
        if(rowIdOfInsertedIngredients.length == recipe.getIngredients().length) {
            Log.d(TAG, "All of the " + recipe.getName() + "'s Ingredients have been succesfully inserted into the Database!");
            return  true;
        } else {
            Log.e(TAG, "Failed to insert some of the " + recipe.getName() + "'s Ingredients into the Database!");
            return false;
        }
    }
    private static boolean insertStepsOfRecipe(long foreignKey, Recipe recipe, StepDao stepDao){
        long[] rowIdOfInsertedSteps = new long[recipe.getSteps().length];
        for(int i=0;i<recipe.getSteps().length;i++) {
            Step currStep = recipe.getSteps()[i];
            currStep.setRecipeId(foreignKey);
            rowIdOfInsertedSteps[i] = stepDao.insertStep(currStep);
        }
        if(rowIdOfInsertedSteps.length == recipe.getSteps().length) {
            Log.d(TAG, "All of the " + recipe.getName() + "'s Steps have been succesfully inserted into the Database!");
            return  true;
        } else {
            Log.e(TAG, "Failed to insert some of the " + recipe.getName() + "'s Steps into the Database!");
            return false;
        }
    }

    /* Deletes */
    public int[] deleteRecipe(final Recipe recipe){
        //At first delete the Steps while the Recipe has its unique ID
        DeleteStepsAndIngredientsAsyncTask deleteStepsAndIngredientsAsyncTask =
                new DeleteStepsAndIngredientsAsyncTask(mDb.stepDao(), mDb.ingredientDao());
        deleteStepsAndIngredientsAsyncTask.execute(recipe.getRoomId());
        int numOfDeletedSteps = deleteStepsAndIngredientsAsyncTask.getNumOfDeletedSteps();
        int numOfDeletedIngredients = deleteStepsAndIngredientsAsyncTask.getNumOfDeletedIngredients();
        //Check if the deletion from child tables were successful and proceed with the parent table, but quit if not
        if(numOfDeletedSteps != recipe.getSteps().length
                || numOfDeletedIngredients != recipe.getIngredients().length) {
            Log.e(TAG, "Failed to delete some of the " + recipe.getName() + "'s Steps or Ingredients from the Database!");
            return new int[]{numOfDeletedIngredients, numOfDeletedSteps};
        } else {
            Log.d(TAG, "All of the " + recipe.getName() + "'s Steps and Ingredients have been successfully deleted from the Database, proceed to delete Recipe itself!");
            DeleteRecipeAsyncTask deleteRecipeAsyncTask = new DeleteRecipeAsyncTask(mDb.recipeDao());
            deleteRecipeAsyncTask.execute(recipe);
            int numOfDeletedRecipes = deleteRecipeAsyncTask.getNumOfDeletedRecipes();
            if(numOfDeletedRecipes == 0){
                Log.e(TAG, "Failed to delete the " + recipe.getName() + " Recipe from the Database!");
            } else {
                Log.d(TAG, "The " + recipe.getName() + " Recipe has been successfully deleted from the Database");
            }
            return new int[]{numOfDeletedRecipes};
        }
    }

    public void deleteAllFavorites(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.ingredientDao().deleteAllIngredients();
                mDb.stepDao().deleteAllSteps();
                mDb.recipeDao().deleteAllFavoriteRecipe();
            }
        });
        Log.d(TAG, "All Recipes have been successfully deleted from the Database");
    }

    /* Queries */
    //Searches if the Recipe exists or not, returning 1 if exists and 0 if not
    public LiveData<Long> searchRecipe(long id){
        recipeIsExists = mDb.recipeDao().searchRecipe(id);
        return recipeIsExists;
    }

    public LiveData<Integer> countNumberOfRows(){
        numberOfRowsInRecipeTable = mDb.recipeDao().checkTableIsEmpty();
        return numberOfRowsInRecipeTable;
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

    private static class DeleteStepsAndIngredientsAsyncTask extends AsyncTask<Long, Void, Integer[]>{
        private StepDao stepDao;
        private IngredientDao ingredientDao;
        private int numOfDeletedSteps;
        private int numOfDeletedIngredients;

        public DeleteStepsAndIngredientsAsyncTask(StepDao stepDao, IngredientDao ingredientDao) {
            this.stepDao = stepDao;
            this.ingredientDao = ingredientDao;
        }
        @Override
        protected Integer[] doInBackground(Long... ids) {
            final Integer[] deletedChildEntities = new Integer[2];
            deletedChildEntities[0] = stepDao.removeStepsOfRecipe(ids[0]);
            deletedChildEntities[1] = ingredientDao.removeIngredientsOfRecipe(ids[0]);
            return deletedChildEntities;
        }
        @Override
        protected void onPostExecute(Integer[] numberOfRows) {
            numOfDeletedSteps = numberOfRows[0];
            numOfDeletedIngredients = numberOfRows[1];
        }
        public int getNumOfDeletedSteps() {
            return numOfDeletedSteps;
        }
        public int getNumOfDeletedIngredients() {
            return numOfDeletedIngredients;
        }
    }

    private static class DeleteRecipeAsyncTask extends AsyncTask<Recipe, Void, Integer>{
        private RecipeDao recipeDao;
        private int numOfDeletedRecipes;

        public DeleteRecipeAsyncTask(RecipeDao recipeDao) {
            this.recipeDao = recipeDao;
        }
        @Override
        protected Integer doInBackground(Recipe... recipes) {
            return recipeDao.removeFavoriteRecipe(recipes[0]);
        }
        @Override
        protected void onPostExecute(Integer numberOfRows) {
            numOfDeletedRecipes = numberOfRows;
        }
        public int getNumOfDeletedRecipes() {
            return numOfDeletedRecipes;
        }
    }




    /*
    private static class InsertRecipeAsyncTask extends AsyncTask<Recipe, Void, long[][]> {
        public interface AsyncRespone { void onDataLoaded(long[][] rowId);}
        private AsyncRespone delegate = null;
        private RecipeDao recipeDao;
        private StepDao stepDao;
        private IngredientDao ingredientDao;

        public InsertRecipeAsyncTask(RecipeDao recipeDao, StepDao stepDao, IngredientDao ingredientDao, AsyncRespone listener) {
            this.delegate = listener;
            this.recipeDao = recipeDao;
            this.stepDao = stepDao;
            this.ingredientDao = ingredientDao;
        }
        @Override
        protected long[][] doInBackground(Recipe... recipes) {
            android.os.Debug.waitForDebugger();
            //At first insert the Recipe to make Room creating unique ID, so wait until it finishes
            long[] idRecipe = new long[1];
            idRecipe[0] = recipeDao.insertRecipeToFavorites(recipes[0]);
            //Then Ingredients
            long[] idsIngredients = new long[recipes[0].getIngredients().length];
            for(int i=0;i<recipes[0].getIngredients().length;i++) {
                Ingredient currIngredietn = recipes[0].getIngredients()[i];
                currIngredietn.setRecipeId(idRecipe[0]);
                idsIngredients[i] = ingredientDao.insertIngredient(currIngredietn);
            }
            if(idsIngredients.length == recipes[0].getIngredients().length) {
                Log.d(TAG, "All of the " + recipes[0].getName() + "'s Ingredients have been succesfully inserted into the Database!");
            } else {
                Log.e(TAG, "Failed to insert some of the " + recipes[0].getName() + "'s Ingredients into the Database!");
            }
            //Then Steps
            long[] idsSteps = new long[recipes[0].getSteps().length];
            for(int i=0;i<recipes[0].getSteps().length;i++) {
                Step currStep = recipes[0].getSteps()[i];
                currStep.setRecipeId(idRecipe[0]);
                idsSteps[i] = stepDao.insertStep(currStep);
            }
            if(idsSteps.length == recipes[0].getSteps().length) {
                Log.d(TAG, "All of the " + recipes[0].getName() + "'s Steps have been succesfully inserted into the Database!");
            } else {
                Log.e(TAG, "Failed to insert some of the " + recipes[0].getName() + "'s Steps into the Database!");
            }
            return new long[][]{idRecipe, idsIngredients, idsSteps};
        }
        @Override
        protected void onPostExecute(long[][] rowIds) {
            Log.d(TAG, "onPostExecute Recipe rowId=" + rowIds[0][0]);
            delegate.onDataLoaded(rowIds);
        }
    }*/
}
