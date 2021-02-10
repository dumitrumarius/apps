package com.example.expovitadmin;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Game {

    public String ean, image_path, name, platform;
    public boolean isNew, preorder;

    public Game(){
    }

    public Game(String mName, String mEan, String mImage_path, String mPlatform, boolean mPreorder, boolean mNew){
        name = mName;
        ean = mEan;
        image_path = mImage_path;
        platform = mPlatform;
        isNew = mNew;
        preorder = mPreorder;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public boolean getNew(){
        return isNew;
    }

    public void setNew(Boolean isNew){
        this.isNew = isNew;
    }
    public boolean getPreorder(){
        return preorder;
    }
    public void setPreorder(Boolean preorder){
        this.preorder = preorder;
    }
}
