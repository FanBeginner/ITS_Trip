package com.example.fan.its_trip.bean;

/**
 * Created by Fan on 2019/10/30.
 */

public class NewsInfo {

    /**
     * ctime : 2019-10-30 12:44
     * title : 补贴退坡 市场接力 比亚迪如何“双轮驱动”
     * description : 新浪汽车
     * picUrl : http://k.sinaimg.cn/n/default/transform/116/w550h366/20180509/HiVt-haichqy9477281.png/w360h240l50t1c4d.jpg alt=补贴退坡 市场接力 比亚迪如何“双轮驱动” style=display: block;>
     * url : http://auto.sina.com.cn/zz/2019-10-30/detail-iicezzrr5965524.shtml
     */

    private String ctime;
    private String title;
    private String description;
    private String picUrl;
    private String url;

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
