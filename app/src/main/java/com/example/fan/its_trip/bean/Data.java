package com.example.fan.its_trip.bean;

/**
 * java 实体类
 * Created by 南尘 on 16-7-28.
 */
public class Data {

    private String text;
    private String text2;
    private int imageId;
    private String imageUrl;

    public Data(String text) {
        this.text=text;
    }
    public Data(String text, String text2, int imageId) {
        this.text = text;
        this.text2=text2;
        this.imageId = imageId;
    }

    public Data(String text, String text2, String imageUrl) {
        this.imageUrl = imageUrl;
        this.text = text;
        this.text2=text2;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
