package com.w1520.liangye.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.umeng.analytics.MobclickAgent;
import com.w1520.liangye.adapter.AboutAdapter;
import com.w1520.liangye.app.R;
import com.w1520.liangye.entity.AboutInfo;
import com.w1520.liangye.entity.AdInfo;
import com.w1520.liangye.entity.ConfigConst;
import com.w1520.liangye.service.AutoSetDesktopService1;
import com.w1520.liangye.service.AutoSetDesktopService2;
import com.w1520.liangye.utils.CustomProgressDialog;
import com.w1520.liangye.utils.LazyFragment;
import com.w1520.liangye.utils.NetworkUtils;
import com.w1520.liangye.view.AboutAppActivity;
import com.w1520.liangye.view.FeedbackActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 关于-Fragment.
 * <p>
 * Created by puruidong on 8/15/15.
 */
@SuppressLint("ValidFragment")
public class AboutFragment extends LazyFragment implements ViewPager.OnPageChangeListener {

    private View view;//当前布局文件对象
    private boolean isPrepared;//是否初始化完成.可进行下面的加载.
    private List<AboutInfo> titleDataList;//存放标题.
    //START.
    //以下两个数组必须保持一致
    private ListView lv_about;//三个子项目的ListView.
    //三个子项目的Adapter
    private AboutAdapter adapter;
    // 自动设置桌面的抽屉
    private DrawerLayout mDrawerLayout;
    //AIDL,此处用于bindService
    private static final String TAG = "AboutFragment";
    private Context context;
    //自动设置桌面的Service.
    private Intent autoService1;
    private Intent autoService2;
    //广告的ViewPager.
    private android.support.v4.view.ViewPager vpabout;
    //AD Viewpager.
    private CustomFragmentPagerAdapter customAdapter;
    //广告图片的布局。
    private LinearLayout ivVpAboutPosition;
    //广告图片数量。
    private final int ADSIZE = 4;//
    //展示图片的数组
    private ImageView[] aboutaddots;
    //小圆点的id
    private int[] aboutadpointids = {R.id.aboutad_iv_1, R.id.aboutad_iv_2, R.id.aboutad_iv_3, R.id.aboutad_iv_4};
    private AdInfo adInfo;//AD总数据
    private List<AdInfo.ChildAdInfo> childAdList = new ArrayList<AdInfo.ChildAdInfo>();//子项目AD的数据.
    private boolean isStop = false; // 是否停止循环的线程: 默认为, 不停止
    private Activity fragmentActivity;//Activity
    //加载提示框
    private CustomProgressDialog dialog;//loading..
    //自动设置桌面发送给手机的通知唯一标志。
    private static final int SEND_NOTICE = 1;
    //是否为自动设置,true->自动,false->手动
    private static final String AUTOSETDESKTOP = "autoSetDekstop";
    //抽屉布局。
    private LinearLayout drawlayout_lmain;
    //选择是否开启自动设置桌面。
    private Switch switchAtuoSetDesktop;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置此属性 Fragment实例不会销毁
        setRetainInstance(true);
        fragmentActivity = this.getActivity();
        NetworkUtils network = NetworkUtils.getInstance(this.getActivity());

        if (network.isOnline()) {
            // 开启一个子线程, 每隔8秒钟切换一个页面
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        SystemClock.sleep(8000); //
                        if (isStop) {
                            break;
                        }
                        // 接收的runnable对象中的run方法, 将要运行在主线程中.
                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // 得到一个新的item的索引
                                int newCurrentItem = vpabout.getCurrentItem() + 1;
                                vpabout.setCurrentItem(newCurrentItem);
                            }
                        });
                    }
                }
            }).start();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.about_fragment, container, false);
        }
        context = this.getActivity();
        autoService1 = new Intent(context, AutoSetDesktopService1.class);
        autoService2 = new Intent(context, AutoSetDesktopService2.class);
        if (null != vpabout) if (vpabout.getVisibility() == View.GONE) {
            if (vpabout == null) {
                vpabout = (ViewPager) view.findViewById(R.id.vp_about_ad);
            }
            if (ivVpAboutPosition == null) {
                ivVpAboutPosition = (LinearLayout) view.findViewById(R.id.iv_vp_about_position);
            }
            initAd();//初始化广告.
        } else {
            if (vpabout == null) {
                vpabout = (android.support.v4.view.ViewPager) view.findViewById(R.id.vp_about_ad);
            }
            if (ivVpAboutPosition == null) {
                ivVpAboutPosition = (LinearLayout) view.findViewById(R.id.iv_vp_about_position);
            }
            initAd();//初始化广告.
        }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (lv_about == null) {
            lv_about = (ListView) view.findViewById(R.id.lv_about);
        }
        if (titleDataList == null) {
            titleDataList = new ArrayList<AboutInfo>();
        }
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) view.findViewById(R.id.about_drawer_layout);
        }
        if (drawlayout_lmain == null) {
            drawlayout_lmain = (LinearLayout) view.findViewById(R.id.drawlayout_lmain);
        }
        if (switchAtuoSetDesktop == null) {
            switchAtuoSetDesktop = (Switch) view.findViewById(R.id.sw_autosetdestop);
        }
        if (vpabout == null) {
            vpabout = (android.support.v4.view.ViewPager) view.findViewById(R.id.vp_about_ad);
        }
        if (ivVpAboutPosition == null) {
            ivVpAboutPosition = (LinearLayout) view.findViewById(R.id.iv_vp_about_position);
        }
        if (dialog == null) {
            dialog = new CustomProgressDialog(context, "正在加载中");
        }
        isPrepared = true;
        lazyLoad();
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initTitleList();//初始化标题Map
        initView();//初始化整体
        initPoints();//初始化AD下面的小圆点
    }


    /**
     * 初始化广告
     */
    private void initAd() {
        final NetworkUtils network = NetworkUtils.getInstance(context);
        //******************************** TODO  URL类型请参考AdInfo.java
        Log.i(ConfigConst.NOSETURLTAG,"********************:AboutFragment[215行]"+ConfigConst.NOSETURL);
        network.showToast("*******************AboutFragment[215行]"+ConfigConst.NOSETURL);
        //*******************************************
        network.getTextData(ConfigConst.NOSETURL, new NetworkUtils.VolleyResultData() {
            @Override
            public void onSuccess(String response) {
                if (TextUtils.isEmpty(response)) {
                    return;
                }
                AdInfo ad = new AdInfo();
                adInfo = ad.getJsonDataEntity(response);
                if (null != adInfo) {
                    childAdList = adInfo.getRow();
                }
                if (customAdapter == null) {
                    customAdapter = new CustomFragmentPagerAdapter(getFragmentManager());
                }
                vpabout.setVisibility(View.VISIBLE);
                ivVpAboutPosition.setVisibility(View.VISIBLE);
                vpabout.setAdapter(customAdapter);
                vpabout.addOnPageChangeListener(AboutFragment.this);
            }
        }, null);
    }

    /**
     * 标题所用List.
     */
    private void initTitleList() {
        AboutInfo autoSetDesktop = new AboutInfo("自动设置桌面", R.drawable.lv_autosetingdesktop);
        titleDataList.add(autoSetDesktop);
        AboutInfo feedBack = new AboutInfo("有话要说", R.drawable.lv_feedback);
        titleDataList.add(feedBack);
        AboutInfo aboutApp = new AboutInfo("关于", R.drawable.lv_aboutapp);
        titleDataList.add(aboutApp);
    }

    /**
     * 初始化圆点.
     */
    private void initPoints() {
        aboutaddots = new ImageView[ADSIZE];
        for (int i = 0; i < ADSIZE; i++) {
            aboutaddots[i] = (ImageView) view.findViewById(aboutadpointids[i]);
        }
    }


    private void initView() {
        switchAtuoSetDesktop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedsp = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedsp.edit();
                //send
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                notification.setSmallIcon(R.drawable.icon24);
                notification.setContentTitle("设置壁纸");
                if (isChecked) {
                    context.startService(autoService1);
                    context.startService(autoService2);
                    notification.setContentText("成功开启自动设置,要等晚上才能更新哦!~~");
                    editor.putBoolean(AUTOSETDESKTOP, true);
                } else {
                    context.stopService(autoService1);
                    context.stopService(autoService2);
                    notification.setContentText("成功停止自动设置,可以手动控制了呢");
                    nm.notify(SEND_NOTICE, notification.build());
                    editor.putBoolean(AUTOSETDESKTOP, false);
                }
                editor.apply();
                notification.setAutoCancel(true);
                nm.notify(SEND_NOTICE, notification.build());
                mDrawerLayout.closeDrawer(drawlayout_lmain);
            }
        });
        if (titleDataList.size() <= 0) {
            //初始化标题的ListView的数据.
            initTitleList();
        }
        if (adapter == null) {
            adapter = new AboutAdapter(this.getActivity(), titleDataList);
        }
        if (lv_about.getCount() <= 0) {
            lv_about.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            final Context context = this.getActivity();
            lv_about.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int icon = titleDataList.get(position).getIcon();
                    switch (icon) {
                        case R.drawable.lv_autosetingdesktop://"自动设置桌面"
                            mDrawerLayout.openDrawer(drawlayout_lmain);
                            SharedPreferences sharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
                            switchAtuoSetDesktop.setChecked(sharedPreferences.getBoolean(AUTOSETDESKTOP, false));
                            break;
                        case R.drawable.lv_feedback://"有话要说"
                            Intent feedback = new Intent(context, FeedbackActivity.class);
                            startActivity(feedback);
                            break;
                        case R.drawable.lv_aboutapp://"关于"
                            Intent aboutapp = new Intent(context, AboutAppActivity.class);
                            startActivity(aboutapp);
                            break;
                    }
                }
            });
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        //改变圆点.
        position = position % ADSIZE;
        for (int i = 0; i < ADSIZE; i++) {
            if (position == i) {
                aboutaddots[i].setImageResource(R.drawable.img_point_selected);
            } else {
                aboutaddots[i].setImageResource(R.drawable.img_point);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    /**
     * 广告ViewPager的PagerAdapter.
     */
    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return the Fragment associated with a specified position.
         */
        @Override
        public Fragment getItem(int position) {
            // 取得在集合中真正的位置.
            int newPosition = position % childAdList.size();
            SImageShowFragment img = new SImageShowFragment(dialog);
            img.setImageUrl(childAdList.get(newPosition).getImgurl());//取得URL地址.
            img.setStype(0);
            img.setOnImageViewClickListener(viewClickListener);//为图片设置点击事件..
            return img;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
    }

    /*
      为图片提供点击事件
     */
    private SImageShowFragment.OnImageViewClickListener viewClickListener = new SImageShowFragment.OnImageViewClickListener() {

        @Override
        public void onClick(View v) {
            int index = vpabout.getCurrentItem() % childAdList.size();//
            String targeturl = childAdList.get(index).getTargeturl();
            Uri uri = Uri.parse(targeturl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    };


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("About"); //友盟统计.
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("About");//友盟统计.
    }

}
