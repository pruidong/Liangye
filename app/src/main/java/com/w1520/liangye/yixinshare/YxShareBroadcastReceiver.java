package com.w1520.liangye.yixinshare;

import im.yixin.sdk.api.YXAPIBaseBroadcastReceiver;

/**易信广播.
 *
 *
 * Created by puruidong on 9/11/15.
 */
public class YxShareBroadcastReceiver  extends YXAPIBaseBroadcastReceiver {

    @Override
    protected String getAppId() {
        return YxUtils.YX_APPID;
    }
}
