package com.w1520.liangye.entity;

import com.google.gson.Gson;
import java.util.List;

/**
 * data:
 * {
 * "total": "4",
 * "row": [
 * {
 * "code": "001",
 * "imgurl": "http://localhost/bingimages/small/201510/20151025_small.jpg",
 * "targeturl": "http://localhost",
 * "remark": "第一张"
 * },
 * {
 * "code": "002",
 * "imgurl": "http://localhost/bingimages/small/201510/20151002_small.jpg",
 * "targeturl": "http://localhost",
 * "remark": "第二张"
 * },
 * {
 * "code": "003",
 * "imgurl": "http://localhost/bingimages/small/201510/20151006_small.jpg",
 * "targeturl": "http://localhost",
 * "remark": "第三张"
 * },
 * {
 * "code": "004",
 * "imgurl": "http://localhost/bingimages/small/201510/20151005_small.jpg",
 * "targeturl": "http://localhost",
 * "remark": "第四张"
 * }
 * ]
 * }
 * <p>
 * <p>
 * <p>
 *
 * Created by puruidong on 11/30/15.
 */
public class AdInfo implements DataEntity<AdInfo> {

    private String total;//总数.

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private List<ChildAdInfo> row;//shuju

    public List<ChildAdInfo> getRow() {
        return row;
    }

    public void setRow(List<ChildAdInfo> row) {
        this.row = row;
    }

    public class ChildAdInfo {

        private String code;//唯一code
        private String imgurl;//图片地址
        private String targeturl;//跳转地址
        private String remark;//备注

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getTargeturl() {
            return targeturl;
        }

        public void setTargeturl(String targeturl) {
            this.targeturl = targeturl;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }


    /**
     * 各子类实现一个获取JSON的LIST.
     *
     * @param response
     * @return
     */
    @Override
    public List<AdInfo> getJsonData(String response) {
        return null;
    }

    /**
     * 各子类实现一个获取JSON的Entity..
     *
     * @param response
     * @return
     */
    @Override
    public AdInfo getJsonDataEntity(String response) {
        //将String数据转换为实体对象.
        Gson gson = new Gson();
        AdInfo ad = gson.fromJson(response,AdInfo.class);
        return ad;
    }


}
