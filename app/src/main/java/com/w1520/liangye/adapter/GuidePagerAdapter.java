package com.w1520.liangye.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 启动页的PagerAdapter
 *
 *
 * Created by puruidong on 8/23/15.
 */
public class GuidePagerAdapter extends PagerAdapter {

    private List<View> views;

    public GuidePagerAdapter(List<View> views ) {
        this.views = views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }


    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return views.size();
    }

    /**
     * Determines whether a page View is associated with a specific key object
     * as returned by {@link #instantiateItem(ViewGroup, int)}. This method is
     * required for a PagerAdapter to function properly.
     *
     * @param view   Page View to check for association with <code>object</code>
     * @param object Object to check for association with <code>view</code>
     * @return true if <code>view</code> is associated with the key object <code>object</code>
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}
