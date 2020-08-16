package com.tiansirk.bakingapp.model;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RecipeWithIngredsSteps {
        @Embedded
        public Recipe recipe;
        @Relation(
                parentColumn = "name",
                entityColumn = "recipeName"
        )
        public List<Ingredient> ingredients;
        @Relation(
                parentColumn = "name",
                entityColumn = "recipeName"
        )
        public List<Step> steps;

        public RecipeWithIngredsSteps(Recipe recipe, List<Ingredient> ingredients, List<Step> steps) {
                this.recipe = recipe;
                this.ingredients = ingredients;
                this.steps = steps;
        }

        public Recipe getRecipe() {
                return recipe;
        }

        public void setRecipe(Recipe recipe) {
                this.recipe = recipe;
        }

        public List<Ingredient> getIngredients() {
                return ingredients;
        }

        public void setIngredients(List<Ingredient> ingredients) {
                this.ingredients = ingredients;
        }

        public List<Step> getSteps() {
                return steps;
        }

        public void setSteps(List<Step> steps) {
                this.steps = steps;
        }
}
