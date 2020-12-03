package com.example.chat;

public class User1 {
    private String name,email,uid,status,imageurl,search;
    boolean isfriend;


    public User1(){

    }

    public User1(String name, String email, String uid, String status, String imageurl, String search) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.status = status;
        this.imageurl = imageurl;
        this.search=search;

    }

    public boolean isIsfriend() {
        return isfriend;
    }

    public void setIsfriend(boolean isfriend) {
        this.isfriend = isfriend;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public User1(String name){
        this.name=name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User1(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status=status;
    }

    public User1(String name, String email, String uid, String status) {
        this.name = name;
        this.email = email;
        this.uid=uid;
    }


    public User1(String name, String email) {
        this.name = name;
        this.email = email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
