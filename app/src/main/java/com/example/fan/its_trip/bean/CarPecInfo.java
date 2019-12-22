package com.example.fan.its_trip.bean;

/**
 * Created by Fan on 2019/10/8.
 */

public class CarPecInfo {
    /**
     * carnumber : 鲁B10001
     * pcode : 1001A　　
     * pdatetime : 2016-5-21 08:19:21
     * paddr : 北京路
     */

    private String carnumber;
    private String pcode;
    private String pdatetime;
    private String paddr;

    public String getCarnumber() {
        return carnumber;
    }

    public void setCarnumber(String carnumber) {
        this.carnumber = carnumber;
    }

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public String getPdatetime() {
        return pdatetime;
    }

    public void setPdatetime(String pdatetime) {
        this.pdatetime = pdatetime;
    }

    public String getPaddr() {
        return paddr;
    }

    public void setPaddr(String paddr) {
        this.paddr = paddr;
    }
}
