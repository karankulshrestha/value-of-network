package com.karankulx.von.Models;

public class Product {
    String photourl;
    Long timeStamp;

    public Product() {
    }

    public Product(String photourl, Long timeStamp) {
        this.photourl = photourl;
        this.timeStamp = timeStamp;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
