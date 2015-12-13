package com.w1520.liangye.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import com.w1520.liangye.adapter.GuidePagerAdapter;
import com.w1520.liangye.app.MainActivity;
import com.w1520.liangye.app.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 看-Activity
 * <p>
 * Created by puruidong on 8/15/15.
 */
public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {

    private ViewPager guidevp;
    private GuidePagerAdapter guidevpadapter;
    private List<View> views;
    //展示图片的数组
    private ImageView[] guidedots;
    //小圆点的id
    private int[] guidepointids = {R.id.guide_iv_1, R.id.guide_iv_2, R.id.guide_iv_3, R.id.guide_iv_4};
    private Button guide_start;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_main);
        initViews();
        initPoints();
    }

    private void initPoints() {
        guidedots = new ImageView[views.size()];
        for (int i = 0; i < views.size(); i++) {
            guidedots[i] = (ImageView) findViewById(guidepointids[i]);
        }
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>(5);
        //初始化要展示的图片.
        views.add(inflater.inflate(R.layout.guide_img_one, null));
        views.add(inflater.inflate(R.layout.guide_img_two, null));
        views.add(inflater.inflate(R.layout.guide_img_three, null));
        views.add(inflater.inflate(R.layout.guide_img_four, null));
        //
        guidevpadapter = new GuidePagerAdapter(views);
        guidevp = (ViewPager) findViewById(R.id.guide_vp);
        guidevp.setAdapter(guidevpadapter);
        //设置按钮启动主Activity.
        guide_start = (Button) views.get(3).findViewById(R.id.guide_btn_stmainactivity);
        guide_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        guidevp.addOnPageChangeListener(this);

    }

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
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
        for (int i = 0; i < guidepointids.length; i++) {
            if (position == i) {
                guidedots[i].setImageResource(R.drawable.img_point_selected);
            } else {
                guidedots[i].setImageResource(R.drawable.img_point);
            }
        }

    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
