package com.tiansirk.bakingapp.data;

public class Step {

    private int number;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String imageURL;

    public Step(int number, String shortDescription, String description, String videoURL, String imageURL) {
        this.number = number;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.imageURL = imageURL;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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
                "number=" + number +
                ", shortDescription='" + shortDescription + '\'' +
                ", description='" + description + '\'' +
                ", videoURL='" + videoURL + '\'' +
                ", imageURL='" + imageURL + '\'' +
                "}\n";
    }
}
