package com.karankulx.von.Models;

import java.util.ArrayList;

public class Groups {
    public String groupId, gName, gSummary, gProfile, groupCreator;
    public boolean isPrivate;
    public ArrayList<Users> user;

    public Groups() {
    }

    public Groups(String groupId, String gName, String gSummary, String gProfile, String groupCreator, boolean isPrivate) {
        this.groupId = groupId;
        this.gName = gName;
        this.gSummary = gSummary;
        this.gProfile = gProfile;
        this.isPrivate = isPrivate;
        this.groupCreator = groupCreator;
    }

    public Groups(String groupId, String gName, String gSummary, String gProfile, String groupCreator, boolean isPrivate, ArrayList<Users> user) {
        this.groupId = groupId;
        this.gName = gName;
        this.gSummary = gSummary;
        this.gProfile = gProfile;
        this.user = user;
        this.isPrivate = isPrivate;
        this.groupCreator = groupCreator;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgSummary() {
        return gSummary;
    }

    public void setgSummary(String gSummary) {
        this.gSummary = gSummary;
    }

    public String getgProfile() {
        return gProfile;
    }

    public void setgProfile(String gProfile) {
        this.gProfile = gProfile;
    }

    public ArrayList<Users> getUser() {
        return user;
    }

    public void setUser(ArrayList<Users> user) {
        this.user = user;
    }
}
