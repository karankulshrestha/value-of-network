package com.karankulx.von.Models;

public class GroupLastMessage {
    String LastMessage;
    long timeStamp;

    public GroupLastMessage() {
    }

    public GroupLastMessage(String lastMessage, long timeStamp) {
        LastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }

    public String getLastMessage() {
        return LastMessage;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
