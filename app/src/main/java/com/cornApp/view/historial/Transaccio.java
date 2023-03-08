package com.cornApp.view.historial;

import java.util.Date;

public class Transaccio {
    private String userDestiny;
    private String userOrigin;
    private String destinyName;
    private String destinySurname;
    private String originName;
    private String originSurname;
    private String accepted;
    private double ammount;
    private Date timeSetup;
    private Date timeStart;
    private Date timeFinish;
    private String token;

    public Transaccio(String userDestiny, String userOrigin, String destinyName, String destinySurname, String originName, String originSurname, String accepted, double ammount, Date timeSetup, Date timeStart, Date timeFinish, String token) {
        this.userDestiny = userDestiny;
        this.userOrigin = userOrigin;
        this.destinyName = destinyName;
        this.destinySurname = destinySurname;
        this.originName = originName;
        this.originSurname = originSurname;
        this.accepted = accepted;
        this.ammount = ammount;
        this.timeSetup = timeSetup;
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
        this.token = token;
    }

    public String getUserDestiny() {
        return userDestiny;
    }

    public String getUserOrigin() {
        return userOrigin;
    }

    public String getDestinyName() {
        return destinyName;
    }

    public String getDestinySurname() {
        return destinySurname;
    }

    public String getOriginName() {
        return originName;
    }

    public String getOriginSurname() {
        return originSurname;
    }

    public String getAccepted() {
        return accepted;
    }

    public double getAmmount() {
        return ammount;
    }

    public Date getTimeSetup() {
        return timeSetup;
    }

    public Date getTimeStart() {
        return timeStart;
    }

    public Date getTimeFinish() {
        return timeFinish;
    }

    public String getToken() {
        return token;
    }
}
