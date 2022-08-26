package com.karankulx.von.Models;

import java.util.ArrayList;

public class Groups {
    public String gName, gSummary, gProfile;
    public boolean groupType;
    public ArrayList<Users> user;

    public Groups() {
    }

    public Groups(String gName, String gSummary, String gProfile, boolean groupType) {
        this.gName = gName;
        this.gSummary = gSummary;
        this.gProfile = gProfile;
        this.groupType = groupType;
    }

    public Groups(String gName, String gSummary, String gProfile, boolean groupType, ArrayList<Users> user) {
        this.gName = gName;
        this.gSummary = gSummary;
        this.gProfile = gProfile;
        this.user = user;
        this.groupType = groupType;
    }

    public boolean isGroupType() {
        return groupType;
    }

    public void setGroupType(boolean groupType) {
        this.groupType = groupType;
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
