package com.areeb.chat;

public class UserModel {

    String uid;
    String Name;
    String email;
    String imageUri;
    String status;

    public UserModel() {
    }

    public UserModel(String uid, String name, String email, String imageUri,String status) { // Contains all strings linked to user and to be added to firebase
        this.uid = uid;
        Name = name;
        this.email = email;
        this.imageUri = imageUri;
        this.status = status;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
