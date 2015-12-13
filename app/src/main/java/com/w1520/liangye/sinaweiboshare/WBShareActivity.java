/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.w1520.liangye.sinaweiboshare;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.w1520.liangye.app.R;
import com.w1520.liangye.utils.CustomProgressDialog;
import com.w1520.liangye.utils.NetworkUtils;

/**
 * 该类演示了第三方应用如何通过微博客户端分享文字、图片、视频、音乐等。
 * 执行流程： 从本应用->微博->本应用
 * 
 * @author SINA
 * @since 2013-10-22
 */
public class WBShareActivity extends Activity implements IWeiboHandler.Response {
    @SuppressWarnings("unused")
    private static final String TAG = "WBShareActivity";

    public static final String KEY_SHARE_TYPE = "key_share_type";
    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;
    
    /** 界面标题 */
    private String     message;
    /** 分享图片 */
    private String  sharebitmapurl;

    /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI = null;

    private CustomProgressDialog dialog;

    private int mShareType = SHARE_CLIENT;
    
    /**
     * @see {@link Activity#onCreate}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShareType = getIntent().getIntExtra(KEY_SHARE_TYPE, SHARE_CLIENT);
        
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, SinaWeiboConstants.APP_KEY);
        
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();
        Bundle bundle = this.getIntent().getExtras();
       if(bundle.containsKey("sinaweiboimage")) {
           sharebitmapurl =  bundle.getString("sinaweiboimage");
            sendMessage(false, true);//image
        }else if(bundle.containsKey("sinaweibomessage")){
           message =  bundle.getString("sinaweibomessage");;
           sendMessage(true,false);//text
       }
        if (dialog == null) {
            dialog = new CustomProgressDialog(this, "正在加载中");
        }

		// 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }
    
    /**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseResp 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            Toast.makeText(this, 
                    getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResp.errMsg, 
                    Toast.LENGTH_LONG).show();
            break;
        }
    }



    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage(boolean hasText, boolean hasImage) {
        
        if (mShareType == SHARE_CLIENT) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                if (supportApi >= 10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                    sendMultiMessage(hasText, hasImage);
                } else {
                    sendSingleMessage(hasText, hasImage);
                }
            } else {
                Toast.makeText(this, R.string.weibosdk_demo_not_support_api_hint, Toast.LENGTH_SHORT).show();
            }
        }
        else if (mShareType == SHARE_ALL_IN_ONE) {
            sendMultiMessage(hasText, hasImage);
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     */
    private void sendMultiMessage(boolean hasText, boolean hasImage) {
        
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        
        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
        }
        else if (mShareType == SHARE_ALL_IN_ONE) {
            AuthInfo authInfo = new AuthInfo(this, SinaWeiboConstants.APP_KEY, SinaWeiboConstants.REDIRECT_URL, SinaWeiboConstants.SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
                
                @Override
                public void onWeiboException( WeiboException arg0 ) {
                }
                
                @Override
                public void onComplete( Bundle bundle ) {
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
                    Toast.makeText(getApplicationContext(), "onAuthorizeComplete token = " + newToken.getToken(), Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onCancel() {
                }
            });
        }
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     */
    private void sendSingleMessage(boolean hasText, boolean hasImage) {
        
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (hasText) {
            weiboMessage.mediaObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.mediaObject = getImageObj();
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(WBShareActivity.this, request);
    }

    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = message;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        final NetworkUtils network = NetworkUtils.getInstance(this);
        final ImageObject imageObject = new ImageObject();
        //        设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        network.getImageByImageRequest(sharebitmapurl, new NetworkUtils.onImageLoaderListener() {
            @Override
            public void onSuccessImage(Bitmap bitmap) {
                imageObject.setImageObject(bitmap);
            }
        }, dialog);
        return imageObject;
    }

}
