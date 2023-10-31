package com.example.pfi_tesst;

public class Message {

    private String text;
    private long timestamp;

    private String ft;
    private String fromId;
    private String toId;

    public Message() {

    }

    public String getFt() {
        return ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String formId) {
        this.fromId = formId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
