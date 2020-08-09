package com.tiansirk.bakingapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "step_table", foreignKeys = @ForeignKey(
        entity = Recipe.class,
        parentColumns = "roomId",
        childColumns = "recipeId",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE))
public class Step implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    @ColumnInfo(index = true)
    private long recipeId; //This is the foreign key

    @Expose
    private String shortDescription;
    @Expose
    private String description;
    @Expose
    private String videoURL;
    @Expose
    private String imageURL;

    // Empty constructor for Room
    public Step(){}

    @Ignore
    public Step(String shortDescription, String description, String videoURL, String imageURL) {
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.imageURL = imageURL;
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", shortDescription='" + shortDescription + '\'' +
                ", description='" + description + '\'' +
                ", videoURL='" + videoURL + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", recipeId= " + recipeId +
                "}\n";
    }

    protected Step(Parcel in){
        shortDescription = in.readString();
        description = in.readString();
        videoURL = in.readString();
        imageURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoURL);
        parcel.writeString(imageURL);
    }

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
