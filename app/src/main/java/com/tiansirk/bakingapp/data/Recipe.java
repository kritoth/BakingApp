package com.tiansirk.bakingapp.data;

import java.util.ArrayList;
import java.util.List;

public class Recipe {

    private String name;
    private Ingredient[] ingredients;
    private Step[] steps;
    private int servings;
    private String imgUrl;

    public Recipe(String name, Ingredient[] ingredients, Step[] steps, int servings, String imgUrl) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imgUrl = imgUrl;
    }

    public Recipe(String name, int servings, String imgUrl) {
        this.name = name;
        this.ingredients = new Ingredient[0];
        this.steps  = new Step[0];
        this.servings = servings;
        this.imgUrl = imgUrl;
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
