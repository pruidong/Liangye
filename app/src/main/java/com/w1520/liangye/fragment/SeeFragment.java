package com.w1520.liangye.fragment;


import android.annotation.SuppressLint;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.w1520.liangye.adapter.SeeCustomAdapter;
import com.w1520.liangye.app.MainActivity;

import com.w1520.liangye.app.R;
import com.w1520.liangye.dao.SeeDao;
import com.w1520.liangye.dao.SystemConfigDao;
import com.w1520.liangye.entity.ConfigConst;
import com.w1520.liangye.entity.See;
import com.w1520.liangye.entity.SystemConfig;
import com.w1520.liangye.qqshare.QQShareUtils;
import com.w1520.liangye.utils.*;
import com.w1520.liangye.wxshare.WxUtils;
import com.w1520.liangye.yixinshare.YxUtils;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 看看
 * .
 * <p/>
 * <p/>
 * Created by puruidong on 8/15/15.
 */
@SuppressLint("ValidFragment")
public class SeeFragment extends LazyFragment implements AdapterView.OnItemClickListener, View.OnClickListener, AbsListView.OnScrollListener {
    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private SeeCustomAdapter adapter;
    private List<String> adapterList;
    private View view;
    private View mainView;
    private ListView lvSee;
    private int lvPosition;//ListView滚动高度记录.
    private boolean isScrollSee = false;
    private CustomProgressDialog dialog;//loading..
    private See see;
    private Context context;
    //底部弹出框.
    private static PopupWindowUtils imagePopupWindow;
    //
    private WxUtils wxutils;
    //微信
    private IWXAPI wxapi;
    //
    private YxUtils yxutils;
    private IYXAPI yxapi;
    //
    private Tencent tentcent;
    private QQShareUtils qqShareUtils;
    //
    //
    private SystemConfigDao configDao;
    private SeeDao seeDao;
    private static final long pageSize = 100;
    //auto refresh
    private static final int REFRESH_COMPLETE = 0X110;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //end.

    private Handler mHandler;


    public int getClickPosition() {
        return clickPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }

    private int clickPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        if (dialog == null) dialog = new CustomProgressDialog(this.getActivity(), "正在加载中");
        dialog.show();
        if (view == null) {
            view = inflater.inflate(R.layout.see_fragment, null);
        }
        if (seeDao == null) {
            seeDao = new SeeDao(context);
        }
        if (mainView == null) {
            mainView = View.inflate(context, R.layout.activity_main, null);
        }
        if (adapterList == null) {
            adapterList = new ArrayList<String>(120);
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        if (lvSee == null) {
            lvSee = (ListView) view.findViewById(R.id.lv_see);
        }
        if (configDao == null) {
            configDao = new SystemConfigDao(context);
        }
        if (see == null) {
            see = new See();
        }
        //wx.
        if (wxapi == null) {
            wxapi = MainActivity.wxapi;
        }
        wxutils = WxUtils.getInstence(wxapi, context);
        //yixin.
        if (yxapi == null) {
            yxapi = MainActivity.yxapi;
        }
        yxutils = YxUtils.getInstance(yxapi, context);
        if (tentcent == null) {
            tentcent = MainActivity.tencent;
        }
        qqShareUtils = QQShareUtils.getInstance(this.getActivity(), tentcent, this.getActivity());
        imagePopupWindow = new PopupWindowUtils(context, R.layout.see_bottom_dialog, R.id.see_bottom_pop_layout,
                new int[]{R.id.btn_see_copy, R.id.btn_wxshare_friend, R.id.btn_wxshare_moments, R.id.btn_wxshare_collent,
                        R.id.btn_yixinshare_friend, R.id.btn_yixinshare_collect, R.id.btn_yixinshare_timeline, R.id.btn_qqshare,
                        R.id.btn_qzoneshare
                }, this, new int[]{R.id.skb_see_setfontsize}, new SeekBarCustom());//R.id.btn_sinaweiboshare
        isPrepared = true;
        lazyLoad();
        return view;
    }


    private SystemConfig getLastUpdateData() {
        SystemConfig result = null;
        for (SystemConfig config : configDao.getValue(ConfigConst.LASTUPDATE_DBNAME)) {
            result = config;
        }
        if (null == result) {
            result =
                    new SystemConfig(1, ConfigConst.LASTUPDATE_DBNAME, "", ConfigConst.LASTUPDATE_DBDESC, "");

        }
        return result;
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case REFRESH_COMPLETE:
                        final SystemConfig lastUpdateData = getLastUpdateData();
                        adapterList = new ArrayList<String>(120);
                        loadData(lastUpdateData.getId(), lastUpdateData.getValue(), 10);//下拉刷新.
                        NetworkUtils networkUtils = NetworkUtils.getInstance(context);
                        networkUtils.showToast("刷新成功");
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        };
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.see_refreshlayout);
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.gray);
        mSwipeRefreshLayout.setProgressViewEndTarget(true, 100);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.lightblue
                , R.color.green, R.color.greenyellow, R.color.indianred);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.post(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(true);
                    }
                });
                mHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
            }
        });

        final SystemConfig lastUpdateData = getLastUpdateData();
        loadData(lastUpdateData.getId(), lastUpdateData.getValue(), 0);//正常刷新
    }


    private void loadData(final int lastUpdateId, final String lastUpdateData, int loadtype) {
        final NetworkUtils network = NetworkUtils.getInstance(this.getActivity());
        if (adapterList.size() <= 0) {
            long count = seeDao.count();
            if (loadtype <= 0 && count > 0) {
                long page = 0;
                List<See> seeList = seeDao.getAllData(page, pageSize);
                for (See aSeeList : seeList) {
                    if (aSeeList != null) {
                        adapterList.add(aSeeList.getValue());
                    }
                }
                if (adapter == null) {
                    adapter = new SeeCustomAdapter(context, adapterList, R.layout.see_lv_item);
                }
                lvSee.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                lvSee.setOnItemClickListener(SeeFragment.this);
                dialog.hide();
            } else {
                //填充各控件的数据
                //********************************  TODO URL返回一個數字,会与数据库中的进行比较.若不一致将进行更新.若一致则不更新.
                Log.i(ConfigConst.NOSETURLTAG, "********************:SeeFragment[242行]" + ConfigConst.NOSETURL);
                network.showToast("*******************SeeFragment[242行]" + ConfigConst.NOSETURL);
                //*******************************************TODO
                network.getTextData(ConfigConst.NOSETURL, new NetworkUtils.VolleyResultData() {
                    @Override
                    public void onSuccess(String response) {
                        String lastupdatedateresponse = "1234567890";
                        try {
                            JSONObject objsw = new JSONObject(response);
                            if (objsw.has("lastupdateqsbk")) {
                                lastupdatedateresponse = objsw.getString("lastupdateqsbk");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(lastUpdateData)) {
                            configDao.add(new SystemConfig(ConfigConst.LASTUPDATE_DBNAME, lastupdatedateresponse,
                                    ConfigConst.LASTUPDATE_DBDESC, ""));
                        } else if (!TextUtils.isEmpty(lastUpdateData) && !lastUpdateData.equals(lastupdatedateresponse)) {
                            configDao.update(new SystemConfig(lastUpdateId, ConfigConst.LASTUPDATE_DBNAME, lastupdatedateresponse,
                                    ConfigConst.LASTUPDATE_DBDESC, ""));
                        } else if (lastUpdateData.equals(lastupdatedateresponse)) {
                            dialog = new CustomProgressDialog(context, "数据正在更新中......");
                            dialog.show();
                            return;
                        }
                        //********************************   TODO URL类型请参考See.java
                        Log.i(ConfigConst.NOSETURLTAG, "********************:SeeFragment[269行]" + ConfigConst.NOSETURL);
                        network.showToast("*******************SeeFragment[269行]" + ConfigConst.NOSETURL);
                        //*******************************************
                        network.getTextData(ConfigConst.NOSETURL, new NetworkUtils.VolleyResultData() {
                            @Override
                            public void onSuccess(String response) {
                                if (TextUtils.isEmpty(response)) {
                                    dialog = new CustomProgressDialog(context, "数据加载失败,请下拉重试.");
                                    dialog.show();
                                    return;
                                }
                                adapterList.clear();
                                SeeDao seeDao = new SeeDao(context);
                                //seeDao.clear();//clear db data.
                                List<See> list = see.getJsonData(response);
                                int maxIndex = 0;
                                for (See se : list) {
                                    se.setCreateDate(new Date());
                                    seeDao.add(se);
                                    if (maxIndex <= 100) {
                                        adapterList.add(se.getValue());
                                        maxIndex += 1;
                                    }
                                }
                                if (adapter == null) {
                                    adapter = new SeeCustomAdapter(context, adapterList, R.layout.see_lv_item);
                                }
                                lvSee.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                lvSee.setOnItemClickListener(SeeFragment.this);
                                dialog.hide();
                            }
                        }, dialog);
                    }
                }, dialog);
                lvSee.setOnScrollListener(this);
            }
        }
    }

    /**
     * ListView点击事件.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        setClickPosition(position);
        imagePopupWindow.showAtLocation(mainView.findViewById(R.id.activity_main_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    /**
     * Button点击事件.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        dialog.show();
        //TODO 先配置各种APPKEY或者APPID。
        NetworkUtils network = NetworkUtils.getInstance(context);
        network.showToast("SeeFragment[326行] 先配置各种APPKEY或者APPID。");
        if(v!=null){
            return ;
        }
        //
        String data = adapterList.get(getClickPosition());
        switch (v.getId()) {
            case R.id.btn_see_copy:
                network.copyText("SEE", data);
                break;
            case R.id.btn_wxshare_friend://微信好友
                wxutils.shareWxText(data, SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.btn_wxshare_collent://收藏
                wxutils.shareWxText(data, SendMessageToWX.Req.WXSceneFavorite);
                break;
            case R.id.btn_wxshare_moments://朋友圈
                wxutils.shareWxText(data, SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.btn_yixinshare_friend://易信好友
                yxutils.shareYxText(data, SendMessageToYX.Req.YXSceneSession);
                break;
            case R.id.btn_yixinshare_timeline://易信朋友圈
                yxutils.shareYxText(data, SendMessageToYX.Req.YXSceneTimeline);
                break;
            case R.id.btn_yixinshare_collect://易信收藏
                yxutils.shareYxText(data, SendMessageToYX.Req.YXCollect);
                break;
            case R.id.btn_qqshare:
                qqShareUtils.shareQQ(data, "", 0);
                break;
            case R.id.btn_qzoneshare:
                qqShareUtils.shareToQzone(data, "", 0);
                break;
            /*case R.id.btn_sinaweiboshare:
                Intent intent = new Intent(activity, WBShareActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sinaweibomessage", data);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                break;*/
        }
        dialog.hide();
        imagePopupWindow.dismiss();
    }

    /**
     * ListView滑动改变时记录滚动位置
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            if (!isScrollSee) {
                lvPosition = lvSee.getFirstVisiblePosition();
            }
        }
        isScrollSee = false;
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }


    private class SeekBarCustom implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        /**
         * SeekBar滑动停止时改变字体大小.
         *
         * @param seekBar The SeekBar in which the touch gesture began
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            SeeCustomAdapter.fontsize = seekBar.getProgress();
            lvSee.setAdapter(adapter);
            isScrollSee = true;
            lvSee.setSelection(lvPosition);
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("See"); //友盟统计.
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("See");//友盟统计.
    }


    /**
     * 返回顶部的实现.
     */
    public class SeeLvReturnTop implements View.OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            if (!lvSee.isStackFromBottom()) {
                lvSee.setStackFromBottom(true);
            }
            lvSee.setStackFromBottom(false);
        }
    }


}
