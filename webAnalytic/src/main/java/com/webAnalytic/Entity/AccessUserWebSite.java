package com.webAnalytic.Entity;

public class AccessUserWebSite {

    private WebSite webSite;
    private User user;

    public AccessUserWebSite() {
    }

    public AccessUserWebSite(WebSite webSite, User user) {
        this.webSite = webSite;
        this.user = user;
    }

    public WebSite getWebSite() {
        return webSite;
    }

    public void setWebSite(WebSite webSite) {
        this.webSite = webSite;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
