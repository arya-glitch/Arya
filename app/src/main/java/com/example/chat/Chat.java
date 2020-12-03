package com.example.chat;

public class Chat {

    private String sender;
    private String reciever;
    private String message;
    private Boolean isseen;
    private long timestamp;
    private String imageurl="default";
    private String videourl="default";

public Chat(){
     }
    public Chat(String sender, String reciever, String message,Boolean isseen,long timestamp,String imageurl,String videourl) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.isseen=isseen;
        this.timestamp=timestamp;
        this.imageurl=imageurl;
        this.videourl=videourl;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
