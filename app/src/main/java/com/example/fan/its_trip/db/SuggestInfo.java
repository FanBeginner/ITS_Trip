package com.example.fan.its_trip.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Fan on 2019/10/29.
 */

public class SuggestInfo extends LitePalSupport {
    private String user;
    private String text;
    private String time;
    private boolean isAccept;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void setAccept(boolean accept) {
        isAccept = accept;
    }

}
