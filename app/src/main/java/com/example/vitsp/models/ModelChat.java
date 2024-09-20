package com.example.vitsp.models;

public class ModelChat {

     String messageId;
     String fromUid;
     String toUid;
     String message;
     String messageType;
     long timestamp;

    // Default constructor
    public ModelChat() {
    }

    // Parameterized constructor
    public ModelChat(String messageId, String fromUid, String toUid, String message, String messageType, long timestamp) {
        this.messageId = messageId;
        this.fromUid = fromUid;
        this.toUid = toUid;
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    // Getter and setter methods
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
