package com.example.chatapp.Model;

public class Group {

    private String id;
    private String Name;
    private String imageURL;
    private String idName;

    public Group() {
    }

    public Group(String id, String name, String imageURL, String idName) {
        this.id = id;
        this.Name = name;
        this.imageURL = imageURL;
        this.idName = idName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public boolean isNull(){
        return id==null || Name ==null || imageURL==null;
    }
}
