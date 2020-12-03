package com.example.chat;

public class User {
    private String name, username, email, uid, status, imageurl, search, gender, phoneno,dob;
    private boolean verified;


    public User() {

    }

    public User(String name, String username, String email, String uid, String status, String imageurl, String search, String gender, boolean verified, String phoneno,String dob) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.status = status;
        this.imageurl = imageurl;
        this.search = search;
        this.gender = gender;
        this.verified = verified;
        this.phoneno = phoneno;
        this.dob=dob;
    }


    public User(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }

    public User(String name, String email, String uid, String status) {
        this.name = name;
        this.email = email;
        this.uid = uid;
    }


    public User(String name, String email) {
        this.name = name;
        this.email = email;

    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}
