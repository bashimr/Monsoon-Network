package com.apptellect.model;

import java.io.File;

public class CropImageModel {
    private String imageLocation;
    private String imageName;
    private File image;
    private boolean isSuccess;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public File getImage() {
        return image;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public String getImageName() {
        return imageName;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }


}
