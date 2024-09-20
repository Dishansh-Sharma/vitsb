package com.example.vitsp.models;

public class ModelChats {

    String profileImageUrl;
    String name;
    String chatkey;
    String receiptUid;
    String messageId;
    String messageType;
    String message;
    String fromUid;
    String toUid;
    long timestamp;


    public ModelChats(){

    }


    public ModelChats(String profileImageUrl, String name, String chatkey, String receiptUid, String messageId, String messageType, String message, String fromUid, String toUid, long timestamp) {
        this.profileImageUrl = profileImageUrl;
        this.name = name;
        this.chatkey = chatkey;
        this.receiptUid = receiptUid;
        this.messageId = messageId;
        this.messageType = messageType;
        this.message = message;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.timestamp = timestamp;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatkey() {
        return chatkey;
    }

    public void setChatkey(String chatkey) {
        this.chatkey = chatkey;
    }

    public String getReceiptUid() {
        return receiptUid;
    }

    public void setReceiptUid(String receiptUid) {
        this.receiptUid = receiptUid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFromUid() {
        return fromUid;
    }

    public void setFromUid(String fromUid) {
        this.fromUid = fromUid;
    }

    public String getToUid() {
        return toUid;
    }

    public void setToUid(String toUid) {
        this.toUid = toUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
