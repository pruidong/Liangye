package com.w1520.liangye.app;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.w1520.liangye.fragment.AboutFragment;
import com.w1520.liangye.fragment.SImageShowFragment;
import com.w1520.liangye.fragment.SeeFragment;
import com.w1520.liangye.fragment.SimgFragment;
import com.w1520.liangye.qqshare.QQShareUtils;
import com.w1520.liangye.utils.CustomProgressDialog;
import com.w1520.liangye.utils.NetworkUtils;
import com.w1520.liangye.utils.PopupWindowUtils;
import com.w1520.liangye.wxshare.WxUtils;
import com.w1520.liangye.yixinshare.YxUtils;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;

import java.io.IOException;


/*
* 今天开始重写之前的"闲聊"，只是现在改名字了--凉夜。
初次的意思是，在每一个凉的夜，都需要一些快乐或者温暖。
刚写还是遇到一些困难，也挺头疼的。
加油！
prd - 2015.8.15 2:20


今天调整了很久ListView的显示，加上泛型也用不了导致解析JSON数据出了很大问题，只能暂时找个办法先解决了。2015.8.16 3:30


今天在一个群的朋友的帮助下，总算解决了Fragment加载图片的问题，真心觉得很开心。也谢谢那个朋友。因为这个问题实在困扰了好几天啊。。。。。。
2015.8.22 1:10

这可能是到目前为止，单日增加功能最多的一次了。
本次共实现以下功能：
		1.为自动设置桌面增加两个Service(保证Service一直存在);
		2.为Image增加一个从底部弹出的框;
		3.将NetworkUtils从new,改为单例.
看起来功能并不多,但是代码量真心不小.
2015.8.29 23:40

=====================================================================================

这次更新了很多东西,已更新如下:
			1.完成ActionBar右侧只在[看图]中显示.
			2.在[我]菜单下,新增一个无限量滚动广告图.
			3.修复在[我->有话要说]里面输入完之后,点击空白处关闭虚拟键盘.
			4.-->重大修改-->修改工具类,获取JSON数据的方法(工具类不在处理,具体的对象转换),代码结构更加清晰.
			5.修复ActionBar的图标大小问题.
			6.修复下载图片的路径,并实时刷新系统图片媒体库.

待优化以下问题:
			1.多次点击ActionBar中的图标按钮,会导致内存溢出.
			2.实现一个留言反馈功能(注意过滤非法字符,可以考虑新增一个图片选择功能..)
			3.实现一个关于界面.
			4.对APP整体颜色进行优化.
			5.对相应代码进行优化或简化.
			6.开源.


2015.9.4 1:40


===================================================================

今天优化了一下界面颜色,有以下改进:
        1.修改[我]界面中的图标颜色.
        2.修正[反馈][关于]中文字不居中的问题.
        3.优化所有颜色,从COLOR文件获取.
        4.优化[看图]底部弹出框颜色.
        5.优化[自动设置桌面]弹窗的颜色.
        6.[看]新增一个复制功能.

       2015.9.8 22:29


2015.9.10  实现了微信的分享,接下来可能需要实现微博，易信的分享功能.
			并且优化了[我]下面的轮播展示.


			2015.9.13 为工具类添加更加详细的注释.并针对代码进行部分优化.

            2015.8.15 2:20 开始开发.
			2015.12.1  0:30 完成开发.

			                      不再开发新的功能,后续几天将进入统一测试.
			                      预计12.5左右发布第一个正式版本.
			                      从8.15开始第一行代码,拖拖拉拉接近3个多月,三大功能模块,最占时间莫过于[分享]部分.
			                      无论如何,这都是一个开始.从刚开始的菜鸟,到如今的菜鸟,虽然不知到进步多少,但只能说,每一次更新
			                      都是一个大的进步.对于下一个版本,将着重优化服务器端,及客户端逻辑代码.
			                      之前的[闲聊]算是一个失败的典型,而当前这个则是完全站立在失败之上.
			                      加油,不为什么!

			                      ->:你的人生永远不会辜负你的。那些转错的弯，那些走错的路，那些流下的泪水，那些滴下的汗水，那些留下的伤痕，全都让你成为独一无二的自己。by 朱学恒﻿


* */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    //主布局文件.
    private View view;
    //底部RadioButton
    private RadioButton rbtnSee, rbtnSimg, rbtnAbout;
    private FrameLayout fragment_container;

    private SeeFragment seeFragment;
    private SimgFragment simgFragment;
    private AboutFragment aboutFragment;

    //弹出框里面的按钮.
    //设置为桌面.
    //保存到本地
    //取消弹窗.
    private Button actionBarTools;
    //底部弹出框.
    private static PopupWindowUtils imagePopupWindow;
    //底部弹出框END.
    private CustomProgressDialog dialog;
    private Context context;
    //微信
    public static IWXAPI wxapi;
    //
    private WxUtils wxutils;
    //微信END.
    //QQ
    public static Tencent tencent;
    private QQShareUtils qqShareUtils;

    //易信
    public static IYXAPI yxapi;
    private YxUtils yxutils;
    //易信END.

    //新浪微博.
    //public static IWeiboShareAPI sinaweiboapi;
    //新浪微博END.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inits();
    }


    /**
     * 初始化.
     */
    private void inits() {
        context = this;
        view = View.inflate(this, R.layout.activity_main, null);

        //初始化ActionBar的样式
        initActionbarByFragment(View.GONE);
        //END.

        //初始化底部的RadioButton.
        rbtnSee = (RadioButton) findViewById(R.id.rbtn_see);
        rbtnSimg = (RadioButton) findViewById(R.id.rbtn_simg);
        rbtnAbout = (RadioButton) findViewById(R.id.rbtn_about);

        rbtnSee.setOnCheckedChangeListener(this);
        rbtnSimg.setOnCheckedChangeListener(this);
        rbtnAbout.setOnCheckedChangeListener(this);
        //END.
        fragment_container = (FrameLayout) findViewById(R.id.fragment_container);

        //底部弹出框.
        imagePopupWindow = new PopupWindowUtils(this, R.layout.simg_bottom_dialog, R.id.simg_bottom_pop_layout,
                new int[]{R.id.btn_simg_setdesktop, R.id.btn_simg_saveimg,
                        R.id.btn_wxshare_friend, R.id.btn_wxshare_moments, R.id.btn_wxshare_collent
                        , R.id.btn_yixinshare_timeline, R.id.btn_yixinshare_friend, R.id.btn_yixinshare_collect,
                        R.id.btn_qqshare, R.id.btn_qzoneshare
                }, this);
        //底部弹出框END. R.id.btn_sinaweiboshare,eSe

        if (dialog == null) {
            dialog = new CustomProgressDialog(this, "正在加载中");
        }

        //将应用注册到微信.
        //TODO 配置微信分享APPID
        /*wxapi = WXAPIFactory.createWXAPI(this, WxUtils.WXAPPID, true);
        wxapi.registerApp(WxUtils.WXAPPID);
        wxutils = WxUtils.getInstence(wxapi, context);*/
        //END.
        //易信.
        /*
        * TODO  配置易信APPID
        *
        yxapi = YXAPIFactory.createYXAPI(this, YxUtils.YX_APPID);
        yxapi.registerApp();
        yxutils = YxUtils.getInstance(yxapi, context);
        */
        //END.
        /*//新浪微博.TODO  配置新浪微博APPID
        sinaweiboapi = WeiboShareSDK.createWeiboAPI(this, SinaWeiboConstants.APP_KEY);
        sinaweiboapi.registerApp();*/
        //END.
        //TODO 配置QQ APPID
        /*tencent = Tencent.createInstance("", this.getApplicationContext());
        qqShareUtils = QQShareUtils.getInstance(this, tencent, MainActivity.this);
        UmengUpdateAgent.update(this);//友盟自动更新
        UmengUpdateAgent.setUpdateCheckConfig(false);*/
        initSee();
    }

    /**
     * 初始化加载看看。
     */
    private void initSee() {
        //初始化加载看看。
        rbtnSee.setChecked(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(fragment_container,
                    buttonView.getId());
            mFragmentPagerAdapter.setPrimaryItem(fragment_container, 0, fragment);
            mFragmentPagerAdapter.finishUpdate(fragment_container);
            switch (buttonView.getId()) {
                case R.id.rbtn_see:
                    initActionbarByFragment(View.GONE);
                    break;
                case R.id.rbtn_simg:
                    initActionbarByFragment(View.VISIBLE);
                    break;
                case R.id.rbtn_about:
                    initActionbarByFragment(View.GONE);
                    break;
            }
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//友盟统计.
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);//友盟统计.
    }


    /**
     * 为Fragment设置ActionBar
     *
     * @param VIEWVISIBILITY visible.
     */
    private void initActionbarByFragment(int VIEWVISIBILITY) {
        View actionBarView = View.inflate(this, R.layout.custom_actionbar, null);
        //初始化ActionBar的样式
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        //END.
        actionBarTools = (Button) actionBarView.findViewById(R.id.btn_actionbar_showtools);
        actionBarTools.setVisibility(VIEWVISIBILITY);
        if (VIEWVISIBILITY == View.VISIBLE) {
            actionBarTools = (Button) actionBarView.findViewById(R.id.btn_actionbar_showtools);
            actionBarTools.setOnClickListener(this);
        }
        if (seeFragment == null) {
            seeFragment = new SeeFragment();
        }
        actionBarView.setOnClickListener(seeFragment.new SeeLvReturnTop());
    }

    private FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case R.id.rbtn_see:
                    if (seeFragment == null) {
                        seeFragment = new SeeFragment();
                    }
                    initActionbarByFragment(View.GONE);
                    return seeFragment;
                case R.id.rbtn_simg:
                    if (simgFragment == null) {
                        simgFragment = new SimgFragment(MainActivity.this);
                    }
                    initActionbarByFragment(View.VISIBLE);
                    return simgFragment;
                case R.id.rbtn_about:
                default:
                    if (aboutFragment == null) {
                        aboutFragment = new AboutFragment();
                    }
                    initActionbarByFragment(View.GONE);
                    return aboutFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };


    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String imgUrl = SImageShowFragment.currentURL;
        final NetworkUtils network = NetworkUtils.getInstance(context);
        //TODO 先配置各种APPKEY或者APPID。
        network.showToast("MainActivity[340行] 先配置各种APPKEY或者APPID。");
        if (v != null) {
            return;
        }
        switch (v.getId()) {
            /* ActionBar Tools   */
            case R.id.btn_actionbar_showtools:
                imagePopupWindow.showAtLocation(view.findViewById(R.id.activity_main_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            /*  底部弹出框按钮  */
            case R.id.btn_simg_setdesktop:
                network.showToast("设置桌面成功", Toast.LENGTH_SHORT);
                network.getImageByImageRequest(imgUrl, new NetworkUtils.onImageLoaderListener() {
                    @Override
                    public void onSuccessImage(Bitmap bitmap) {
                        try {
                            network.setWallPaper(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, dialog);
                break;
            case R.id.btn_simg_saveimg:
                network.getImageByImageRequest(imgUrl, new NetworkUtils.onImageLoaderListener() {
                    @Override
                    public void onSuccessImage(Bitmap bitmap) {
                        network.saveImages(bitmap);
                    }
                }, dialog);
                break;
            case R.id.btn_wxshare_friend://微信好友
                shareWxImage(SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.btn_wxshare_collent://收藏
                shareWxImage(SendMessageToWX.Req.WXSceneFavorite);
                break;
            case R.id.btn_wxshare_moments://朋友圈
                shareWxImage(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.btn_yixinshare_friend://易信好友
                shareYxImage(SendMessageToYX.Req.YXSceneSession);
                break;
            case R.id.btn_yixinshare_timeline://易信朋友圈
                shareYxImage(SendMessageToYX.Req.YXSceneTimeline);
                break;
            case R.id.btn_yixinshare_collect://易信收藏
                shareYxImage(SendMessageToYX.Req.YXCollect);
                break;
            /*case R.id.btn_sinaweiboshare://新浪微博
                Intent intent = new Intent(MainActivity.this, WBShareActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sinaweiboimage", imgUrl);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
                break;*/
            case R.id.btn_qqshare:
                qqShareUtils.shareQQ("图片分享", imgUrl, 1);
                break;
            case R.id.btn_qzoneshare:
                qqShareUtils.shareToQzone("图片分享", imgUrl, 1);
                break;
        }
        if (v.getId() != R.id.btn_actionbar_showtools) {
            imagePopupWindow.dismiss();
        }
    }


    private void shareWxImage(final int wxSence) {
        String imgUrl = SImageShowFragment.currentURL;
        final NetworkUtils network = NetworkUtils.getInstance(context);
        network.getImageByImageRequest(imgUrl, new NetworkUtils.onImageLoaderListener() {
            @Override
            public void onSuccessImage(Bitmap bitmap) {
                wxutils.shareWxImage(bitmap, wxSence);
            }
        }, dialog);
    }


    private void shareYxImage(final int yxSence) {
        String imgUrl = SImageShowFragment.currentURL;
        final NetworkUtils network = NetworkUtils.getInstance(context);
        network.getImageByImageRequest(imgUrl, new NetworkUtils.onImageLoaderListener() {
            @Override
            public void onSuccessImage(Bitmap bitmap) {
                yxutils.shareYxImage(bitmap, yxSence);
            }
        }, dialog);
    }

}
