package com.tiansirk.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_table")
public class Recipe implements Parcelable {

    //@PrimaryKey(autoGenerate = true)
    @NonNull
    private long roomId;

    @Expose
    @Ignore
    private int id;
    @PrimaryKey
    @NonNull
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

    private long dateAddedToFav;
    boolean isFavorite;

    // Empty constructor for Room
    public Recipe(){}

    @Ignore
    public Recipe(String name, Ingredient[] ingredients, Step[] steps, int servings, String imgUrl) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.imgUrl = imgUrl;
        this.isFavorite = false;
    }

    @Ignore
    public Recipe(String name, int servings, String imgUrl) {
        this.name = name;
        this.ingredients = new Ingredient[0];
        this.steps  = new Step[0];
        this.servings = servings;
        this.imgUrl = imgUrl;
        this.isFavorite = false;
    }

    public long getRoomId() {
        return roomId;
    }

    public int getId() { return id;}

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

    public long getDateAddedToFav() {
        return dateAddedToFav;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setDateAddedToFav(long dateAddedToFav) {
        this.dateAddedToFav = dateAddedToFav;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id= " + id +
                ", roomId= " + roomId +
                ", name='" + name + '\'' +
                ",\n ingredients=" + Arrays.toString(ingredients) +
                ",\n steps=" + Arrays.toString(steps) +
                ",\n servings=" + servings +
                ", imgUrl='" + imgUrl + '\'' +
                ", favorite=" + isFavorite +
                ", datAddedToFav= " + dateAddedToFav +
                '}';
    }

    /* Implementing equals */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        return getName().equals(recipe.getName());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    /* Implementing Parcelable */
    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ingredients = new Ingredient[0];
        in.readTypedArray(ingredients, Ingredient.CREATOR);
        in.readTypedArray(steps, Step.CREATOR);
        servings = in.readInt();
        imgUrl = in.readString();
        dateAddedToFav = in.readLong();
        isFavorite = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedArray(ingredients, 0);
        dest.writeTypedArray(steps, 0);
        dest.writeInt(servings);
        dest.writeString(imgUrl);
        dest.writeLong(dateAddedToFav);
        dest.writeByte((byte) (isFavorite ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
