package com.example.chat;

public class Comments {

    private String sender;
    private String receiver;
    private String comment;
    private String comment_id;
    private long timestamp;

    public Comments() {
    }


    public Comments(String sender, String reciever, String comment, long timestamp,String comment_id) {
        this.sender = sender;
        this.receiver = reciever;
        this.comment = comment;
        this.timestamp = timestamp;
        this.comment_id=comment_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }




}
