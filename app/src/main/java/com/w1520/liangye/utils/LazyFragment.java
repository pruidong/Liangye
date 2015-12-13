package com.w1520.liangye.utils;

import android.support.v4.app.Fragment;
import android.view.View;

/**
 *
 * Fragment数据的缓加载.
 *
 * #{link http://blog.csdn.net/maosidiaoxian/article/details/38300627}
 * Created by puruidong on 8/16/15.
 */
public abstract class LazyFragment extends Fragment {
    protected boolean isVisible;


    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (this.getView() != null)
            this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    protected void onVisible() {
        lazyLoad();
    }

    protected abstract void lazyLoad();

    protected void onInvisible() {
    }


}
