package com.tiansirk.bakingapp.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tiansirk.bakingapp.model.Ingredient;
import com.tiansirk.bakingapp.model.Recipe;
import com.tiansirk.bakingapp.model.Step;

/**
 * Utility class
 */
public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName();

    /**
     * Serialize (ie. converts) the array of {@link Recipe} objects to JSON formatted String. Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeRecipesToJson(Recipe[] recipes){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(recipes);
    }

    /**
     * Deserialize (ie. reads from) the JSON and creates {@link Recipe} objects accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     * @return the array of {@link Recipe}s
     */
    public static Recipe[] getRecipesFromJson(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Recipe[] recipes = gson.fromJson(json, Recipe[].class);
        return recipes;
    }

    /**
     * Serialize (ie. converts) a {@link Recipe} object to JSON formatted String. Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeRecipeToJson(Recipe recipe){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(recipe);
    }

    /**
     * Deserialize (ie. reads from)  from the JSON and creates a {@link Recipe} object accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     * @return the {@link Recipe}
     */
    public static Recipe getRecipeFromJson(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.fromJson(json, Recipe.class);
    }

    /**
     * Serialize (ie. converts) the array of {@link Step} objects to JSON formatted String. Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeStepsToJson(Step[] steps){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(steps);
    }

    /**
     * Deserialize (ie. reads from)  from the JSON and creates {@link Step} objects accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     * @return the array of {@link Step}s
     */
    public static Step[] getStepsFromJson(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Step[] steps = gson.fromJson(json, Step[].class);
        return steps;
    }

    /**
     * Serialize (ie. converts) the array of {@link Ingredient} objects to JSON formatted String. Uses Gson, https://android-arsenal.com/details/1/229
     * @return JSON formatted String
     */
    public static String serializeIngredientsToJson(Ingredient[] ingredients){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(ingredients);
    }

    /**
     * Deserialize (ie. reads from)  from the JSON and creates {@link Ingredient} objects accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     * @return the array of {@link Ingredient}s
     */
    public static Ingredient[] getIngredientsFromJson(String json){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Ingredient[] ingredients = gson.fromJson(json, Ingredient[].class);
        return ingredients;
    }

    /**
     * For testing purposes
     * @return array of dummy Recipes
     */
    public static Recipe[] dummyRecipes(){
        Recipe[] recipes = new Recipe[10];
        for(int i=0; i<recipes.length; i++){
            recipes[i] = new Recipe("Az " + i + ". recept", new Ingredient[i], new Step[0], 10, "www.kep_helye.com");
        }
        return recipes;
    }
}
