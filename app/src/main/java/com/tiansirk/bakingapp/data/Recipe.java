package com.tiansirk.bakingapp.data;

import com.google.gson.annotations.Expose;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_table")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;

    @Expose
    private String name;
    @Expose
    @Ignore
    private Ingredient[] ingredients;
    @Expose
    @Ignore
    private Step[] steps;
    @Expose
    private int servings;
    @Expose
    private String imgUrl;

    // Empty constructor for Room
    public Recipe(){}

    @Ignore
    public Recipe(String name, Ingredient[] ingredients, Step[] steps, int servings, String imgUrl) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imgUrl = imgUrl;
    }

    @Ignore
    public Recipe(String name, int servings, String imgUrl) {
        this.name = name;
        this.ingredients = new Ingredient[0];
        this.steps  = new Step[0];
        this.servings = servings;
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public Step[] getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ",\n ingredients=" + ingredients +
                ",\n steps=" + steps +
                ", servings=" + servings +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
