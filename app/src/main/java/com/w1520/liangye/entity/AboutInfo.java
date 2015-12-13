package com.w1520.liangye.entity;

/**
 * about info
 *
 *
 * Created by puruidong on 8/23/15.
 */
public class AboutInfo {

    private String title ;
    private int icon;

    public AboutInfo(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


}
