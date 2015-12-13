package com.w1520.liangye.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.umeng.analytics.MobclickAgent;
import com.w1520.liangye.app.R;
import com.w1520.liangye.entity.BingImages;
import com.w1520.liangye.entity.ConfigConst;
import com.w1520.liangye.utils.*;

import java.util.*;


/**
 * 图-Fragment.
 * <p/>
 * Created by puruidong on 8/15/15.
 */
@SuppressLint("ValidFragment")
public class SimgFragment extends LazyFragment implements ViewPager.OnPageChangeListener {

    //默认加载多少张图片.
    private static final int LOAD_IMAGE_SIZE = 6;
    //保存图片的LIST
    private List<BingImages> mImgIds = new ArrayList<BingImages>(LOAD_IMAGE_SIZE);

    private CustomFragmentPagerAdapter customAdapter;
    private android.support.v4.view.ViewPager mViewPager;
    private Context context;
    private DateUtils dateutils;
    private CustomProgressDialog dialog;//loading..
    private TextView tvImgNumber, tvImgDate;
    private String[] imgDates = new String[LOAD_IMAGE_SIZE];

    public SimgFragment(Context context) {
        this.context = context;
    }

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (dialog == null) {
            dialog = new CustomProgressDialog(this.getActivity(), "正在加载中");
        }
        if (view == null) {
            view = inflater.inflate(R.layout.simg_fragment, null);
        }
        // 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        if (customAdapter == null) {
            customAdapter = new CustomFragmentPagerAdapter(getFragmentManager());
        }
        if (mViewPager == null) {
            mViewPager = (android.support.v4.view.ViewPager) view.findViewById(R.id.id_viewPager);
        }
        dateutils = DateUtils.getInstance();
        isPrepared = true;
        lazyLoad();
        return view;
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        initView();
    }


    private void initView() {
        Calendar customdate = dateutils.createCalendar(0, 20);
        long currentdate = dateutils.getCurrentTime();
        int afternums = 0;
        if (customdate.getTimeInMillis() > currentdate) {
            afternums = 1;
        }
        context = this.getActivity();
        //********************************TODO   URL类型:批量生成多个图片URL地址.
        NetworkUtils network = NetworkUtils.getInstance(context);
        Log.i(ConfigConst.NOSETURLTAG,"********************:SimgFragment[111行]"+ConfigConst.NOSETURL);
        network.showToast("*******************SimgFragment[111行]"+ConfigConst.NOSETURL);
        //*******************************************
        if (mImgIds.size() == 0) {
            for (int i = 0; i < LOAD_IMAGE_SIZE; i++) {
                Calendar ca = dateutils.rollDate(new Date(), Calendar.DAY_OF_MONTH, (0 - (i + afternums)));
                String month = dateutils.getDate(ca.getTime(), DateUtils.DATE_FORMAT_YYMM);
                String nowdate = dateutils.getDate(ca.getTime(), DateUtils.DATE_FORMAT_YYYYMMDD);
                String url = "http://localhost/" + month + "/" + nowdate + ".jpg";
                if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(nowdate)) {
                    mImgIds.add(new BingImages(url, nowdate, ""));
                }
                imgDates[i] = dateutils.getDate(ca.getTime(), DateUtils.DATE_FORMAT_X_YYYY_MM_DD);
            }
            mViewPager.setAdapter(customAdapter);
            mViewPager.addOnPageChangeListener(this);
            if (tvImgDate == null) {
                tvImgDate = (TextView) view.findViewById(R.id.tv_simg_date);
                tvImgDate.setText(imgDates[0]);
            }
            if (tvImgNumber == null) {
                tvImgNumber = (TextView) view.findViewById(R.id.tv_simg_number);
                tvImgNumber.setText(1 + "/" + LOAD_IMAGE_SIZE);
            }
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
        tvImgDate.setText(imgDates[position]);
        tvImgNumber.setText((position + 1) + "/" + LOAD_IMAGE_SIZE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    class CustomFragmentPagerAdapter extends FragmentPagerAdapter {

        public CustomFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            if (dialog == null) {
                dialog = new CustomProgressDialog(context, "正在加载中");
            }
            dialog.show();
            SImageShowFragment img = new SImageShowFragment(dialog);
            img.setImageUrl(mImgIds.get(position).getUrl());
            img.setStype(1);
            return img;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return (SImageShowFragment) super.instantiateItem(container, position);
        }

        @Override
        public int getCount() {
            return LOAD_IMAGE_SIZE;
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Simg"); //友盟统计.
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Simg");//友盟统计.
    }

}
