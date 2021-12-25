package com.webAnalytic.Entity;

public class Report {

    private long id;

    private User user;
    private String fileName;
    byte[] source;

    public Report() {
    }

    public Report(long id, User user, String fileName, byte[] source) {
        this.id = id;
        this.user = user;
        this.fileName = fileName;
        this.source = source;
    }

    public byte[] getSource() {
        return source;
    }

    public void setSource(byte[] source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
