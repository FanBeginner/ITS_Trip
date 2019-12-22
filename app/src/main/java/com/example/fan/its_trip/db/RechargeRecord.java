package com.example.fan.its_trip.db;

import org.litepal.crud.LitePalSupport;

/**
 * Created by Fan on 2019/10/15.
 */

public class RechargeRecord extends LitePalSupport {
    /**
     * CarId : 1
     * Time : 2017-11-26 04:58:11
     * Cost : 10
     */

    private int CarId;
    private String Time;
    private int Cost;
    private String Operator;

    public int getCarId() {
        return CarId;
    }

    public void setCarId(int CarId) {
        this.CarId = CarId;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public int getCost() {
        return Cost;
    }

    public void setCost(int Cost) {
        this.Cost = Cost;
    }

    public String getOperator() {
        return Operator;
    }

    public void setOperator(String operator) {
        Operator = operator;
    }
}
