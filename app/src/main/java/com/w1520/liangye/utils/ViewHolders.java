package com.w1520.liangye.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by puruidong on 7/15/15.
 */
public class ViewHolders {
    private SparseArray<View> mViews;

    public int getPosition() {
        return mPosition;
    }

    private int mPosition;
    private View mConvertView;

    public ViewHolders(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);

    }


    public static ViewHolders get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {

            return new ViewHolders(context, parent, layoutId, position);
        } else {
            ViewHolders holder = (ViewHolders) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }


    /**
     * 通过viewId获取控件.
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public View getConvertView() {
        return mConvertView;
    }


    /**
     * 設置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolders setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }

    /**
     * 設置ImageView的值.
     *
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolders setImageResource(int viewId, int resId) {
        ImageView image = getView(viewId);
        image.setImageResource(resId);
        return this;
    }

    /**
     * 設置ImageView的值.
     *
     * @param viewId
     * @param bitmap
     * @return
     */
    public ViewHolders setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView image = getView(viewId);
        image.setImageBitmap(bitmap);
        return this;
    }

    /**
     * 設置ImageView的值.
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolders setImageURL(int viewId, String url) {
        ImageView image = getView(viewId);
        //image.setImageBitmap(bitmap);
        return this;
    }


}
