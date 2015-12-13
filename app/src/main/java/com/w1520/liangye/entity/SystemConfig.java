package com.w1520.liangye.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 系统配置
 * <p>
 * <p>
 * Created by puruidong on 10/13/15.
 */
@DatabaseTable(tableName = "systemconfig")
public class SystemConfig {

    @DatabaseField(generatedId = true, useGetSet = true)
    private int id;
    //
    @DatabaseField(columnName = "name", useGetSet = true,unique = true)
    private String name;//名
    //
    @DatabaseField(columnName = "value", useGetSet = true)
    private String value;//值
    //
    @DatabaseField(columnName = "desc", useGetSet = true)
    private String desc;//描述
    @DatabaseField(columnName = "remark", useGetSet = true)
    private String remark;//备注


    public SystemConfig(){}


    public SystemConfig(int id, String name, String value, String desc, String remark) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.desc = desc;
        this.remark = remark;
    }

    public SystemConfig(String name, String value, String desc, String remark) {
        this.name = name;
        this.value = value;
        this.desc = desc;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
