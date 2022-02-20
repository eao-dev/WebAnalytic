package com.webAnalytic.Domains;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class Resource implements IMapper<Resource> {

    private long id;
    private WebSite domain;

    private String page;

    public Resource() {}

    public Resource(WebSite webSite, String page) {
        this.domain = webSite;
        this.page = page;
    }

    public Resource(long id) {
        this.id = id;
    }

    public Resource(long id, WebSite domain, String page) {
        this.id = id;
        this.domain = domain;
        this.page = page;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public WebSite getDomain() {
        return domain;
    }

    public String getPage() {
        return page;
    }

    @Override
    public Resource map(ResultSet resultSet) throws SQLException {
        return new Resource(
                resultSet.getLong("id"),
                new WebSite(resultSet.getLong("webSite_Id")),
                resultSet.getString("Page"));
    }

}
