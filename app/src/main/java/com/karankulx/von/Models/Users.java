package com.karankulx.von.Models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Users implements Parcelable {

    String profilePic, name, email, password, rePassword, phoneNumber, status, uid;
    Boolean isSelected;

    public Users() {};

    public Users(String profilePic, String name, String email, String password, String rePassword, String phoneNumber, String status, String uid, Boolean isSelected) {
        this.profilePic = profilePic;
        this.name = name;
        this.email = email;
        this.password = password;
        this.rePassword = rePassword;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.uid = uid;
        this.isSelected = isSelected;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Users(String email, String password, String rePassword) {
        this.email = email;
        this.password = password;
        this.rePassword = rePassword;
    }

    public Users(String profilePic, String name, String phoneNumber, String status) {
        this.profilePic = profilePic;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    protected Users(Parcel in) {
        profilePic = in.readString();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        rePassword = in.readString();
        phoneNumber = in.readString();
        status = in.readString();
        uid = in.readString();
    }

    public static final Creator<Users> CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel in) {
            return new Users(in);
        }

        @Override
        public Users[] newArray(int size) {
            return new Users[size];
        }
    };

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(profilePic);
        parcel.writeString(name);
        parcel.writeString(email);
        parcel.writeString(password);
        parcel.writeString(rePassword);
        parcel.writeString(phoneNumber);
        parcel.writeString(status);
        parcel.writeString(uid);
    }
}
