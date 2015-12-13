package com.w1520.liangye.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import com.w1520.liangye.app.R;


/**
 * android 从底部弹出框-PopupWindow.
 * <p/>
 * <p/>
 * Created by puruidong on 8/29/15.
 */
public class PopupWindowUtils extends PopupWindow {

    private View mMenuView;

    protected PopupWindowUtils(){}

    /**
     * 构建一个从底部弹出的PopupWindow
     *
     * @param context
     * @param layoutViewId layout布局文件的ID
     * @param viewId       PopupWindow的ID
     * @param btnId        按钮id数组.
     * @param onclick      按钮事件
     */
    public PopupWindowUtils(Context context, int layoutViewId, final int viewId, int[] btnId, View.OnClickListener onclick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(layoutViewId, null);
        //给PopupWindow中的按钮设置监听事件.
        for (int i = 0; i < btnId.length; i++) {
            (mMenuView.findViewById(btnId[i])).setOnClickListener(onclick);
        }
        initEntity(viewId);
    }

    public PopupWindowUtils(Context context, int layoutViewId, final int viewId, int[] btnId, View.OnClickListener onclick,int[] seekbarId,SeekBar.OnSeekBarChangeListener on) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(layoutViewId, null);
        //给PopupWindow中的按钮设置监听事件.
        for (int i = 0; i < btnId.length; i++) {
            (mMenuView.findViewById(btnId[i])).setOnClickListener(onclick);
        }
        for (int i = 0; i < seekbarId.length; i++) {
            ((SeekBar)mMenuView.findViewById(seekbarId[i])).setOnSeekBarChangeListener(on);
        }
        initEntity(viewId);
    }

    private void initEntity(final int viewId){
        //给PopupWindow中的按钮设置监听事件.
        mMenuView.findViewById(R.id.btn_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        //this.setOutsideTouchable(true);
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event){
                int height = mMenuView.findViewById(viewId).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

}
