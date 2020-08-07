package com.tiansirk.bakingapp.data;

import com.google.gson.annotations.Expose;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ingredient_table", foreignKeys = @ForeignKey(
        entity = Recipe.class,
        parentColumns = "roomId",
        childColumns = "recipeId",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE))
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    @ColumnInfo(index = true)
    private long recipeId; //This is the foreign key

    @Expose
    private double quantity;
    @Expose
    private String measure;
    @Expose
    private String ingredient;

    // Empty constructor for Room
    public Ingredient(){}

    @Ignore
    public Ingredient(long recipeId, double quantity, String measure, String ingredient) {
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Ignore
    public Ingredient(double quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(long recipeId) {
        this.recipeId = recipeId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return id + ". " + ingredient + ": " + quantity + ", " + measure + ", recipeId: " + recipeId;
    }
}
