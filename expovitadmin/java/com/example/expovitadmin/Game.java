package com.example.expovitadmin;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Game {

    public String ean, image_path, name, platform;
    public boolean new_flag, preorder;

    public Game(){
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
        return new_flag;
    }

    public void setNew(Boolean new_flag){
        this.new_flag = new_flag;
    }
    public boolean getPreorder(){
        return preorder;
    }
    public void setPreorder(Boolean preorder){
        this.preorder = preorder;
    }
}
