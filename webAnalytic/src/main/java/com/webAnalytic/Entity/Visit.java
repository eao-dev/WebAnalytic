package com.webAnalytic.Entity;

import java.sql.Date;

public class Visit {

    private long ID;

    private String referer;

    private Resource resource;

    private Visitor visitor;

    private Date dateTime;

    public Visit() {}

    public Visit(long ID) {
        this.ID = ID;
    }

    public Visit(String referer, Resource resource, Visitor visitor) {
        this.referer = referer;
        this.resource = resource;
        this.visitor = visitor;
    }

    public Visit(long ID, String referer, Resource resource, Visitor visitor) {
        this.ID = ID;
        this.referer = referer;
        this.resource = resource;
        this.visitor = visitor;
    }

    public Visit(long ID, String referer, Resource resource, Visitor visitor, Date dateTime) {
        this.ID = ID;
        this.referer = referer;
        this.resource = resource;
        this.visitor = visitor;
        this.dateTime = dateTime;
    }

    public long getID() {
        return ID;
    }

    public Resource getTargetResource() {
        return resource;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getReferer() {
        return referer;
    }
}
