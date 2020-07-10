package com.tiansirk.bakingapp.utils;

import android.content.Context;
import com.google.gson.stream.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.tiansirk.bakingapp.data.Recipe;
import com.tiansirk.bakingapp.ui.RecipeAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import men.ngopi.zain.jsonloaderlibrary.JSONLoader;
import men.ngopi.zain.jsonloaderlibrary.StringLoaderListener;


/**
 * Utility class
 */
public class JsonParser {

    private static final String LOG_TAG = JsonParser.class.getSimpleName();

    /**
     * Reads from the JSON and creates {@link Recipe} objects accordingly. Uses Gson, https://android-arsenal.com/details/1/229,
     * to convert a JSON string to equivalent Java object.
     *
     * @return the list of {@link Recipe}s
     */
    public static Recipe[] jsonToJavaDeserialization(String json){

        Gson gson = new Gson();
        Recipe[] recipes = gson.fromJson(json, Recipe[].class);

        return recipes;
    }


}
