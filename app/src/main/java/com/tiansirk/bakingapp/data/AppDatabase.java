package com.tiansirk.bakingapp.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {Recipe.class, Ingredient.class, Step.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String TAG = AppDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "favoriterecipes";
    private static AppDatabase sInstance;

    public static AppDatabase getsInstance(Context context){
        if(sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {

                    Log.d(TAG, "Instantiating a new database");
                    sInstance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            AppDatabase.DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        Log.d(TAG, "Returning the database instance already available");
        return sInstance;
    }

    public abstract RecipeDao recipeDao();
    public abstract IngredientDao ingredientDao();
    public abstract StepDao stepDao();
}
