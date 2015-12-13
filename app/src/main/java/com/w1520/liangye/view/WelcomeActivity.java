package com.w1520.liangye.view;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import com.w1520.liangye.app.MainActivity;
import com.w1520.liangye.app.R;


/**
 *  欢迎界面Activity
 *
 * Created by puruidong on 8/15/15.
 */
public class WelcomeActivity extends Activity {

    //是否第一次启动应用
    private boolean isFirstIn = false;
    //等待时间
    private static final int SLEEPTIME = 3000;
    //
    private static final int GO_HOME = 1000 ;
    //
    private static final int GO_GUIDE = 1001;
    //
    private static final String SHAREDNAME="com.w1520.liangye";

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GO_HOME:
                    goHome();
                        break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
        }
    };

    private void goHome() {
        Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void goGuide() {
        Intent i = new Intent(WelcomeActivity.this, GuideActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_main);
        init();
    }

    private void init() {
        //通过标记判断应该启动哪个Activity
        SharedPreferences perPreferences = getSharedPreferences(SHAREDNAME,MODE_PRIVATE);
        isFirstIn = perPreferences.getBoolean("isFirstIn",true);
        if(!isFirstIn){
            mHandler.sendEmptyMessageDelayed(GO_HOME, SLEEPTIME);
        }else{
            mHandler.sendEmptyMessageDelayed(GO_GUIDE,SLEEPTIME);
            //记录已经进入APP的标志
            SharedPreferences.Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstIn",false);
            editor.commit();
        }
    }
}
