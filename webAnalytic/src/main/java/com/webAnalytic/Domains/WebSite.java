package com.webAnalytic.Domains;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WebSite implements IMapper<WebSite> {

    private long id;

    @NotNull(message = "Не должно быть null!")
    @NotEmpty(message = "Не должно быть пустым!")
    @Size(max = 255, message = "Длина от 3 до 255 символов!")
    private String domain;

    private User admin;

    private Date date;

    public WebSite(String domain, User admin) {
        this.domain = domain;
        this.admin = admin;
    }

    public WebSite(String domain) {
        this.domain = domain;
    }

    public WebSite(long id) {
        this.id = id;
    }

    public WebSite() {
    }

    public WebSite(long id, @NotNull(message = "Не должно быть null")
    @NotEmpty(message = "Не должно быть пустым")
    @Size(max = 255, message = "Длина от 3 до 255 символов") String domain, User admin) {
        this.id = id;
        this.domain = domain;
        this.admin = admin;
    }

    public WebSite(long id, @NotNull(message = "Не должно быть null")
    @NotEmpty(message = "Не должно быть пустым")
    @Size(max = 255, message = "Длина от 3 до 255 символов") String domain, User admin, Date date) {
        this.id = id;
        this.domain = domain;
        this.admin = admin;
        this.date = date;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public long getId() {
        return id;
    }

    public String getDomain() {
        return domain;
    }

    public User getAdmin() {
        return admin;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public WebSite map(ResultSet resultSet) throws SQLException {
        var user = new User(resultSet.getLong("Admin_id"));

        return new WebSite(
                resultSet.getLong("id"),
                resultSet.getString("domain"),
                user,
                resultSet.getDate("DateTime")
        );
    }

}
