package com.w1520.liangye.dao;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.w1520.liangye.entity.See;

import java.sql.SQLException;
import java.util.List;

/**
 * see dao.
 *
 * Created by puruidong on 10/13/15.
 */
public class SeeDao {

    private Context context;
    private Dao<See, Integer> seeDao;
    private DBHelper helper;

    public SeeDao(Context context) {
        this.context = context;
        try {
            helper = DBHelper.getHelper(context);
            seeDao = helper.getDao(See.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加
     *
     * @param see
     */
    public void add(See see) {
        try {
            seeDao.create(see);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        try {
            seeDao.queryRaw("delete from see");
            seeDao.queryRaw("update sqlite_sequence SET seq = 0 where name ='see'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return
     */
    public long count() {
        try {
            return seeDao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * @param start
     * @param size
     * @return
     */
    public List<See> getAllData(long start, long size) {
        try {
            return seeDao.queryBuilder().orderBy("createdate", false).offset(start).limit(size).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
