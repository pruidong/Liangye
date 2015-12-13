package com.w1520.liangye.entity;

/**
 * bing images.
 *
 * Created by puruidong on 9/17/15.
 */
public class BingImages {

    //url 地址.
    private String url;
    //图片描述
    private String desc;


    public BingImages(String url, String date, String desc) {
        this.url = url;
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
