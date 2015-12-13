package com.w1520.liangye.entity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * see
 *
 * Created by puruidong on 8/15/15.
 */
@DatabaseTable(tableName = "see")
public class See implements DataEntity<See> {

    @DatabaseField(generatedId = true, useGetSet = true)
    private int id;//id
    @DatabaseField(columnName = "value", useGetSet = true)
    private String value;//值
    @DatabaseField(columnName = "remark", useGetSet = true)
    private String remark;//备注.
    @DatabaseField(columnName = "createdate",  useGetSet = true)
    private Date createDate;//

    public See() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 各子类实现一个获取JSON的LIST.
     *
     * @param response
     * @return
     */
    @Override
    public List<See> getJsonData(String response) {
        //将String数据转换为实体对象.
        try {
            response = new String(response.getBytes("ISO-8859-1"), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement el = parser.parse(response);
        JsonArray jsonArrays = null;
        if (el.isJsonArray()) {
            jsonArrays = el.getAsJsonArray();
        }
        Iterator<JsonElement> it = jsonArrays.iterator();
        List<See> list = new ArrayList<See>(400);
        while (it.hasNext()) {
            JsonElement e = it.next();
            list.add(gson.fromJson(e, See.class));
        }
        return list;
    }

    /**
     * 各子类实现一个获取JSON的Entity..
     *
     * @param response
     * @return
     */
    @Override
    public See getJsonDataEntity(String response) {
        return null;
    }


    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
