package com.webAnalytic.Domains;

import org.springframework.stereotype.Component;

import javax.validation.constraints.Pattern;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class Visitor implements IMapper<Visitor> {

    private long id;

    private String country;
    private String Browser;
    private String OS;
    private String Device;
    private String ScResolution;

    private Date dateReg;

    public Visitor(long id, String country,String browser, String OS, String device,
                   String scResolution, Date dateReg) {
        this.id = id;
        this.country = country;
        Browser = browser;
        this.OS = OS;
        Device = device;
        ScResolution = scResolution;
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

    @Override
    public Visitor map(ResultSet resultSet) throws SQLException {
        return new Visitor(resultSet.getLong("id"),
                resultSet.getString("Country"),
                resultSet.getString("Browser"),
                resultSet.getString("OS"),
                resultSet.getString("Device"),
                resultSet.getString("ScResolution"),
                resultSet.getDate("DateReg")
        );
    }

}
