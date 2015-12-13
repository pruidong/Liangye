package com.w1520.liangye.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.w1520.liangye.app.R;

/**
 * @author http://blog.csdn.net/finddreams
 *         <p>
 *             demo:
 *              CustomProgressDialog dialog =new CustomProgressDialog(this, "正在加载中",R.anim.frame);
        show:
                dialog.show();
        hide:
                dialog.hide();
 *
 *
 *         Created by puruidong on 8/23/15.
 * @Description:自定义对话框
 */
public class CustomProgressDialog extends ProgressDialog {


    private Context mContext;
    private ImageView mImageView;
    private String mLoadingTip;
    private TextView mLoadingTv;


    public CustomProgressDialog(Context context, String content) {
        super(context);
        this.mContext = context.getApplicationContext();
        this.mLoadingTip = content;
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.alpha=0.3f;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);
        initView();
        initData();
    }

    private void initData() {
        mLoadingTv.setText(mLoadingTip);
    }


    public void setContent(String str) {
        mLoadingTv.setText(str);
    }

    private void initView() {

        mLoadingTv = (TextView) findViewById(R.id.loadingTv);
        mImageView = (ImageView) findViewById(R.id.iv_loadimg);
    }
}