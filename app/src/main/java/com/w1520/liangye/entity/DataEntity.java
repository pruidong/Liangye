package com.w1520.liangye.entity;

import java.util.List;

/**
 * 实体父类
 * <p/>
 * Created by puruidong on 9/3/15.
 */
public interface DataEntity<T> {
    /**
     * 各子类实现一个获取JSON的LIST.
     *
     */
    List<T> getJsonData(String response);

    /**
     * 各子类实现一个获取JSON的Entity..
     *
     * @param response
     * @return
     */
    T getJsonDataEntity(String response);
}
