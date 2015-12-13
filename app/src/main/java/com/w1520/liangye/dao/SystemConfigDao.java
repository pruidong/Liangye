package com.w1520.liangye.dao;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.w1520.liangye.entity.SystemConfig;

import java.sql.SQLException;
import java.util.List;

/**
 * 系统配置
 *
 *
 * Created by puruidong on 10/13/15.
 */
public class SystemConfigDao {


    private Context context;
    private Dao<SystemConfig, Integer> configDao;
    private DBHelper helper;

    public SystemConfigDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getHelper(context);
            configDao = helper.getDao(SystemConfig.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加
     *
     * @param SystemConfig
     */
    public void add(SystemConfig SystemConfig) {
        try {
            configDao.create(SystemConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * update
     *
     * @param SystemConfig
     */
    public void update(SystemConfig SystemConfig) {
        try {
            configDao.update(SystemConfig);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过系统配置的名称查询相应的值
     *
     * @param name  名称
     * @return
     */
    public List<SystemConfig> getValue(String name){
        try {
            return configDao.queryBuilder().where().eq("name",name).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
