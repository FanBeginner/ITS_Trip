package com.example.fan.its_trip.bean;

/**
 * Created by Fan on 2019/10/8.
 */

public class CarPecType {
    /**
     * pcode : 1001A
     * pmoney : 1000
     * pscore : 0
     * premarks : A 驾驶拼装的非汽车类机动车上道路行驶的
     */

    private String pcode;
    private int pmoney;
    private int pscore;
    private String premarks;

    public String getPcode() {
        return pcode;
    }

    public void setPcode(String pcode) {
        this.pcode = pcode;
    }

    public int getPmoney() {
        return pmoney;
    }

    public void setPmoney(int pmoney) {
        this.pmoney = pmoney;
    }

    public int getPscore() {
        return pscore;
    }

    public void setPscore(int pscore) {
        this.pscore = pscore;
    }

    public String getPremarks() {
        return premarks;
    }

    public void setPremarks(String premarks) {
        this.premarks = premarks;
    }
}
