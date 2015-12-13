package com.w1520.liangye.yixinshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;
import com.w1520.liangye.utils.NetworkUtils;
import im.yixin.sdk.api.*;
import im.yixin.sdk.util.BitmapUtil;

/**
 * 易信工具类.
 * <p/>
 * Created by puruidong on 9/11/15.
 */
public class YxUtils {

    /**
     * yixin appid.
     * TODO 配置易信APPID
     */
    public static final String YX_APPID = "配置易信APPID";

    private static YxUtils yxUtils = null;

    private IYXAPI yxapi;
    private Context context;

    private YxUtils(IYXAPI yxapi, Context context) {
        this.yxapi = yxapi;
        this.context = context;
    }

    public static YxUtils getInstance(IYXAPI yxapi, Context context) {
        return (yxUtils == null) ? new YxUtils(yxapi, context) : yxUtils;
    }

    /**
     * 发送文字到易信
     *
     * @param message 文字
     * @param scene   发送位置
     */
    public void shareYxText(String message, int scene) {
        if (TextUtils.isEmpty(message) || yxapi == null) {
            NetworkUtils network = NetworkUtils.getInstance(context);
            network.showToast("分享失败,请稍候重试", Toast.LENGTH_SHORT);
            return;
        }
        // 初始化一个YXTextObject对象
        YXTextMessageData textObj = new YXTextMessageData();
        textObj.text = message;

        // 用YXTextObject对象初始化一个YXMessage对象
        YXMessage msg = new YXMessage();
        msg.messageData = textObj;
        msg.description = message;

        // 构造一个Req对象
        SendMessageToYX.Req req = new SendMessageToYX.Req();
        // transaction字段用于唯一标识一个请求
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = scene;
        //调用api接口发送数据到易信
        yxapi.sendRequest(req);
    }


    /**
     * 发送图片到易信
     *
     * @param bitmap 图片
     * @param scene  发送位置
     */
    public void shareYxImage(Bitmap bitmap, int scene) {
        YXImageMessageData imgObj = new YXImageMessageData();

        YXMessage msg = new YXMessage();
        msg.messageData = imgObj;
        Bitmap thumBmp = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
        bitmap.recycle();
        msg.thumbData = BitmapUtil.bmpToByteArray(thumBmp, true);
        //
        SendMessageToYX.Req req = new SendMessageToYX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = scene;
        //send to Yx.
        yxapi.sendRequest(req);
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


}
