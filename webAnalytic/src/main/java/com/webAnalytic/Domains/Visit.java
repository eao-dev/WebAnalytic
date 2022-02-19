package com.webAnalytic.Domains;

import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class Visit implements IMapper<Visit> {

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

    @Override
    public Visit map(ResultSet resultSet) throws SQLException {
        var visitor = new Visitor(resultSet.getLong("Visitor_id"));
        var resource = new Resource(resultSet.getLong("Resource_id"));

        return new Visit(
                resultSet.getLong("id"),
                resultSet.getString("referer"),
                resource,
                visitor,
                resultSet.getDate("DateTime"));
    }

}
