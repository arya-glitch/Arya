package com.example.chat;


import java.util.List;

public class Post {
    private String uid;
    private String caption = null;
    private List<String> imageurl;
    private String postid;
    private String videourl="default";
    private Long timestamp;

    public Post() {
    }

    public Post(String uid, String caption, List<String> imageurl, String postid,String videourl,Long timestamp) {

        this.uid = uid;
        this.caption = caption;
        this.imageurl = imageurl;
        this.postid = postid;
        this.videourl=videourl;
        this.timestamp=timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<String>  getImageurl() {
        return imageurl;
    }

    public void setImageurl(List<String> imageurl) {

        this.imageurl = imageurl;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }


    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }



}