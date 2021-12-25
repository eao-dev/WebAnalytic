package com.webAnalytic.Entity;

import javax.validation.constraints.Pattern;
import java.sql.Date;

public class Visitor {

    private long id;

    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9-_.]{1,10}$\n")
    private String  country;

    private String Browser;
    private String OS;
    private String Device;
    private String ScResolution;

    private byte[] ip;

    private Date dateReg;

    public Visitor(long id, @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9-_.]{1,10}$\n") String country,
                   String browser, String OS, String device, String scResolution, byte[] ip, Date dateReg) {
        this.id = id;
        this.country = country;
        Browser = browser;
        this.OS = OS;
        Device = device;
        ScResolution = scResolution;
        this.ip = ip;
        this.dateReg = dateReg;
    }

    public Visitor() {
    }

    public Visitor(long id) {
        this.id = id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDateReg(Date dateReg) {
        this.dateReg = dateReg;
    }

    public long getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public void setIp(byte[] ip) {
        this.ip = ip;
    }

    public byte[] getIp() {
        return ip;
    }

    public Date getDateReg() {
        return dateReg;
    }

    public String getBrowser() {
        return Browser;
    }

    public void setBrowser(String browser) {
        Browser = browser;
    }

    public String getOS() {
        return OS;
    }

    public void setOS(String OS) {
        this.OS = OS;
    }

    public String getDevice() {
        return Device;
    }

    public void setDevice(String device) {
        Device = device;
    }

    public String getScResolution() {
        return ScResolution;
    }

    public void setScResolution(String scResolution) {
        ScResolution = scResolution;
    }
}
