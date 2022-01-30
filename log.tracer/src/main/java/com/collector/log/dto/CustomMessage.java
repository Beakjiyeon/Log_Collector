package com.collector.log.dto;

import java.io.Serializable;

public class CustomMessage implements Serializable {

    private String text;

    private int priority;

    private boolean secret;

    protected CustomMessage() {
    }

    public CustomMessage(String text, int priority, boolean secret) {
        this.text = text;
        this.priority = priority;
        this.secret = secret;
    }

    public String getText() {
        return text;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "com.collector.log.dto.CustomMessage{" +
                "text='" + text + '\'' +
                ", priority=" + priority +
                ", secret=" + secret +
                '}';
    }

}