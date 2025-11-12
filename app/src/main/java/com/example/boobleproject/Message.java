package com.example.boobleproject;

import java.util.Date;

public class Message {
    public int id;
    public int userid1;
    public int userid2;
    public String text;
    public String timestamp;
    public boolean isRead;

    public Message() {
    }

    public Message(int userid1, int userid2, String text) {
        this.userid1 = userid1;
        this.userid2 = userid2;
        this.text = text;
        this.timestamp = new java.util.Date().toString();
    }

    public boolean isSentByMe(int currentUserId) {
        return userid1 == currentUserId;
    }
}