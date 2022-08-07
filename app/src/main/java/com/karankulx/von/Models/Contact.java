package com.karankulx.von.Models;

public class Contact {
    public String name;
    public String phoneNumber;

    public Contact() {
    }

    public Contact(String name, String phoneNumber ) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}