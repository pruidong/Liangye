package com.w1520.liangye.qqshare;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONObject;

/**
 * Created by puruidong on 9/17/15.
 */
public class QQShareUtils {

    private Context context;
    private Tencent mTencent;
    private Activity activity;
    private static QQShareUtils qqShareUtils = null ;

    private QQShareUtils() {
    }

    private QQShareUtils(Context context,Tencent mTencent,Activity activity) {
        this.context = context;
        this.mTencent = mTencent;
        this.activity = activity;
    }

    public static QQShareUtils getInstance(Context context,Tencent tencent,Activity activity){
        if(qqShareUtils==null){
            qqShareUtils = new QQShareUtils(context,tencent,activity);
        }
        return qqShareUtils;
    }


    public void shareQQ(String message,String imageUrl,int shareType) {
        String title = "";
        if(shareType==0){//wenzi.
            title = message.substring(0,10);
        }else{//tu.
            title = message;
        }
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  message);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "");
        if(shareType!=0) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        }
        mTencent.shareToQQ(activity, params, new BaseUiListener());
    }

    public void shareToQzone (String message,String imageUrl,int shareType) {
        //分享类型
        String title = "";
        if(shareType==0){//wenzi.
            title = message.substring(0,10);
        }else{//tu.
            title = message;
        }
        Bundle params = new Bundle();
        params.putString(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, String.valueOf(QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT));
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,message);//选填
        if(shareType!=0){
        params.putString(QzoneShare.SHARE_TO_QQ_IMAGE_URL,imageUrl);
            }
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "");//必填
        mTencent.shareToQzone(activity, params, new BaseUiListener());
    }


    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object response) {
            //V2.0版本，参数类型由JSONObject 改成了Object,具体类型参考api文档
            //mBaseMessageText.setText("onComplete:");

        }
        protected void doComplete(JSONObject values) {

        }
        @Override
        public void onError(UiError e) {
        }
        @Override
        public void onCancel() {
        }
    }
}
