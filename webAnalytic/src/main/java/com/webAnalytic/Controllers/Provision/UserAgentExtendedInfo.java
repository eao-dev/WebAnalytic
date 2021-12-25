package com.webAnalytic.Controllers.Provision;

public class UserAgentExtendedInfo {
    private String device;
    private String browser;
    private String os;

    public UserAgentExtendedInfo(String device, String os, String browser) {
        this.device = device;
        this.os = os;
        this.browser = browser;
    }

    public String getDevice() {
        return device;
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }
}
