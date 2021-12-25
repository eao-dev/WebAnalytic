package com.webAnalytic.Entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Date;

public class WebSite {

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
}
