package com.karankulx.von.Models;

public class Product {
    String photourl;
    Long timeStamp;
    Boolean isSelected = false;

    public Product() {
    }

    public Product(String photourl, Long timeStamp, Boolean isSelected) {
        this.photourl = photourl;
        this.timeStamp = timeStamp;
        this.isSelected = isSelected;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
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
