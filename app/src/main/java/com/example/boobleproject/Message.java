package com.example.boobleproject;

import java.util.Date;

public class Message {
    public int id;
    public int senderId;
    public int recipientId;
    public String text;
    public String timestamp;
    public boolean isRead;

    public Message() {
    }

    public Message(int senderId, int recipientId, String text) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.text = text;
        this.timestamp = new java.util.Date().toString();
    }

    public boolean isSentByMe(int currentUserId) {
        return senderId == currentUserId;
    }
}