package com.karankulx.von.Models;

import java.util.ArrayList;

public class Groups {
    public String gName, gSummary, gProfile, groupCreator;
    public boolean isPrivate;
    public ArrayList<Users> user;

    public Groups() {
    }

    public Groups(String gName, String gSummary, String gProfile, String groupCreator, boolean isPrivate) {
        this.gName = gName;
        this.gSummary = gSummary;
        this.gProfile = gProfile;
        this.isPrivate = isPrivate;
        this.groupCreator = groupCreator;
    }

    public Groups(String gName, String gSummary, String gProfile, String groupCreator, boolean isPrivate, ArrayList<Users> user) {
        this.gName = gName;
        this.gSummary = gSummary;
        this.gProfile = gProfile;
        this.user = user;
        this.isPrivate = isPrivate;
        this.groupCreator = groupCreator;
    }

    public String getGroupCreator() {
        return groupCreator;
    }

    public void setGroupCreator(String groupCreator) {
        this.groupCreator = groupCreator;
    }

    public boolean isGroupType() {
        return isPrivate;
    }

    public void setGroupType(boolean groupType) {
        this.isPrivate = groupType;
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
