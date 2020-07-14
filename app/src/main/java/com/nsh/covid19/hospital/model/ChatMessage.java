package com.nsh.covid19.hospital.model;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private String uid;
    private long messageTime;

    public ChatMessage(String messageText, String messageUser, String uid) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.uid = uid;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getUid() {
        return uid;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}