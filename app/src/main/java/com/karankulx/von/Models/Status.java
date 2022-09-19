package com.karankulx.von.Models;

public class Status {
    private String imageUrl;
    private String uid, key;
    private Long timeStamp;

    public Status() {
    }

    public Status(String imageUrl, Long timeStamp, String uid) {
        this.imageUrl = imageUrl;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
