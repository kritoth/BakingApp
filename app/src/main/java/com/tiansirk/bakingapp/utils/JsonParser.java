package com.tiansirk.bakingapp.utils;

import android.content.Context;
import android.util.Log;

import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.ui.RecipeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import men.ngopi.zain.jsonloaderlibrary.JSONArrayLoaderListener;
import men.ngopi.zain.jsonloaderlibrary.JSONLoader;
import men.ngopi.zain.jsonloaderlibrary.JSONObjectLoaderListener;

/**
 * JSON parser using theJSONLoader Library: https://android-arsenal.com/details/1/7916
 * Parses a .json file resent in the {assets} folder
 */
public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName();

    /**
     * Reads from the JSON and creates {@link Recipe} objects accordingly.
     *
     * @return the list of Recipes
     */
    public static List<Recipe> parseJsonFromFile(Context context) {
        final List<Recipe> recipes = new ArrayList<>();

        // JSON as JSONArray
        JSONLoader.with(context)
                .fileName("baking.json")
                .getAsJSONArray(new JSONArrayLoaderListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(LOG_TAG, response.toString());
                        try{
                            for(int i=0; i<response.length(); i++){
                                JSONObject recipe = response.getJSONObject(i);
                                String name = recipe.getString("name");
                                JSONArray ingredients = recipe.getJSONArray("ingredients");

                            }
                            recipes.add(new Recipe());
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Problem with parsing JSON!\n", e);
                        }
                    }

                    @Override
                    public void onFailure(Exception error) {
                        Log.e(LOG_TAG, "Problem with reading from JSON file!\n", error);
                    }
                });
        return recipes;
    }
}
