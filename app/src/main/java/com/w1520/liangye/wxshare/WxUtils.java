package com.w1520.liangye.wxshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.w1520.liangye.utils.NetworkUtils;

/**
 * WEIXIN Tools. - Custom.
 * <p/>
 * Created by puruidong on 9/10/15.
 */
public class WxUtils {

    private static WxUtils wxutils = null;

    //微信APPID
    // TODO
    public static final String WXAPPID = "配置微信APPID";
    //记录分享值的标记.
    private Context context = null;
    private static final int THUMB_SIZE = 150;

    //微信分享对象.
    private IWXAPI wxapi;


    private WxUtils(IWXAPI wxapi, Context context) {
        this.wxapi = wxapi;
        this.context = context;
    }

    /**
     * return WxUtils.
     *
     * @return WxUtils
     */
    public static WxUtils getInstence(IWXAPI wxapi, Context context) {
        return (wxutils == null) ? new WxUtils(wxapi, context) : wxutils;
    }


    //图片分享
    public void shareWxImage(Bitmap bitmap, int scene) {
        if (bitmap == null || wxapi == null) {
            NetworkUtils network = NetworkUtils.getInstance(context);
            network.showToast("分享失败,请稍候重试", Toast.LENGTH_SHORT);
            return;
        }
        WXImageObject imageObject = new WXImageObject(bitmap);

        //
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;

        //
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, THUMB_SIZE, THUMB_SIZE, true);
        bitmap.recycle();
        msg.thumbData = UtilsWxDemo.bmpToByteArray(thumbBmp, true);
        //
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = scene;
        wxapi.sendReq(req);
    }


    /**
     * 分享文字.
     *
     * @param scene 分享的位置定义，可以选择：会话，朋友圈，收藏
     */
    public void shareWxText(String text, int scene) {
        if (TextUtils.isEmpty(text) || wxapi == null) {
            NetworkUtils network = NetworkUtils.getInstance(context);
            network.showToast("分享失败,请稍候重试", Toast.LENGTH_SHORT);
            return;
        }
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;
        //
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        wxapi.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
