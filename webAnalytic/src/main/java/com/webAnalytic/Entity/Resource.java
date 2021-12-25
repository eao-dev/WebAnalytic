package com.webAnalytic.Entity;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Resource {

    private long id;

    private WebSite domain;

    @NotNull
    @Pattern(regexp = "^[а-яА-ЯёЁa-zA-Z0-9]+$\n")
    private String page;

    public Resource(WebSite domain, String page) {
        this.domain = domain;
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
}
