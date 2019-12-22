package com.example.fan.its_trip.bean;

/**
 * Created by Fan on 2019/9/30.
 */

public class UserInfoEvent
{
    private String username;
    private String pname;
    private String pcardid;
    private String psex;
    private String ptel;
    private String pregisterdate;

    public UserInfoEvent(String username, String pname, String psex,String ptel,String pcardid) {
        this.username = username;
        this.pname = pname;
        this.psex = psex;
        this.ptel=ptel;
        this.pcardid=pcardid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getPcardid() {
        return pcardid;
    }

    public void setPcardid(String pcardid) {
        this.pcardid = pcardid;
    }

    public String getPsex() {
        return psex;
    }

    public void setPsex(String psex) {
        this.psex = psex;
    }

    public String getPtel() {
        return ptel;
    }

    public void setPtel(String ptel) {
        this.ptel = ptel;
    }

    public String getPregisterdate() {
        return pregisterdate;
    }

    public void setPregisterdate(String pregisterdate) {
        this.pregisterdate = pregisterdate;
    }
}
