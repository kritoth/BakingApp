package com.tiansirk.bakingapp.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.tiansirk.bakingapp.data.Ingredient;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.data.Step;

import java.util.ArrayList;



/**
 * Utility class
 */
public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName();

    /**
     * Deserialize (ie. reads from) the JSON and creates {@link Recipe} objects accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     *
     * @return the array of {@link Recipe}s
     */
    public static Recipe[] getRecipesFromJson(String json){

        Gson gson = new Gson();
        Recipe[] recipes = gson.fromJson(json, Recipe[].class);

        return recipes;
    }

    /**
     * Serialize (ie. converts) the array of {@link Step} objects to JSON formatted String. Uses Gson, https://android-arsenal.com/details/1/229,
     *
     * @return JSON formatted String
     */
    public static String serializeStepsToJson(Step[] steps){
        Gson gson = new Gson();
        return gson.toJson(steps);
    }

    /**
     * Deserialize (ie. reads from)  from the JSON and creates {@link Step} objects accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     *
     * @return the array of {@link Step}s
     */
    public static Step[] getStepsFromJson(String json){
        Gson gson = new Gson();
        Step[] steps = gson.fromJson(json, Step[].class);

        return steps;
    }

    /**
     * For testing purposes
     * @return array of dummy Recipes
     */
    public static Recipe[] dummyRecipes(){
        Recipe[] recipes = new Recipe[10];
        for(int i=0; i<recipes.length; i++){
            recipes[i] = new Recipe("Az " + i + ". recept", new ArrayList<Ingredient>(), new ArrayList<Step>(), 10, "www.kep_helye.com");
        }

        return recipes;
    }
}
