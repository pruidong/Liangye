package com.w1520.liangye.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.*;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.w1520.liangye.app.R;
import com.w1520.liangye.entity.ConfigConst;
import com.w1520.liangye.utils.CustomProgressDialog;
import com.w1520.liangye.utils.NetworkUtils;

/**
 * Created by puruidong on 8/26/15.
 */
public class AboutAppActivity extends AppCompatActivity {


    private WebView webView;
    private CustomProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutapp_main);
        //初始化ActionBar的样式
        ActionBar actionBar = getSupportActionBar();
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        LayoutInflater inflater = LayoutInflater.from(this);
        View views = inflater.inflate(R.layout.custom_actionbar, null);
        final Button backs = (Button) views.findViewById(R.id.btn_actionbar_back);
        backs.setVisibility(View.VISIBLE);
        backs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs.setVisibility(View.GONE);
                AboutAppActivity.this.finish();
            }
        });
        ((TextView) views.findViewById(R.id.action_bar_title)).setText("关于");
        actionBar.setCustomView(views, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        //11111
        if (dialog == null) dialog = new CustomProgressDialog(this, "正在加载中");
        dialog.show();
        webView = (WebView) findViewById(R.id.wv_aboutappinfo);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //结束
                super.onPageFinished(view,url);
                dialog.hide();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //开始
                super.onPageStarted(view,url,favicon);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                NetworkUtils network = NetworkUtils.getInstance(AboutAppActivity.this);
                network.showToast("啊哦!~暂时加载失败了,要不稍候在试试?..呜..", Toast.LENGTH_LONG);
            }
        });
        //******************************** TODO
        Log.i(ConfigConst.NOSETURLTAG,"********************:设置[关于]菜单的打开地址,AboutAppActivity[82行]"+ConfigConst.NOSETURL);
        //*******************************************
        webView.loadUrl("http://www.baidu.com");//这里设置关于.
    }

}
