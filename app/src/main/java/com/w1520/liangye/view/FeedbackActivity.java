package com.w1520.liangye.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.w1520.liangye.app.R;
import com.w1520.liangye.entity.ConfigConst;
import com.w1520.liangye.utils.CustomProgressDialog;
import com.w1520.liangye.utils.NetworkUtils;

import java.util.Map;

/**
 * feedback.
 * <p/>
 * Created by puruidong on 8/26/15.
 */
public class FeedbackActivity extends AppCompatActivity {

    private EditText usercontactinfos, contextinfos, usernameinfo;
    private Button saveinfos;
    private InputMethodManager imm;
    private CustomProgressDialog dialog;//loading..

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_main);
        if (dialog == null) {
            dialog = new CustomProgressDialog(this, "正在加载中");
        }
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
    }

    private void initView() {
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
                FeedbackActivity.this.finish();
            }
        });
        ((TextView) views.findViewById(R.id.action_bar_title)).setText("有话要说");
        ((TextView) views.findViewById(R.id.action_bar_title)).setGravity(Gravity.CENTER_HORIZONTAL);
        actionBar.setCustomView(views, lp);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        //开启ActionBar上APP ICON的功能
        //END.
        usercontactinfos = (EditText) findViewById(R.id.feedback_user_contactsinfo);
        contextinfos = (EditText) findViewById(R.id.feedback_contextinfo);
        usernameinfo = (EditText) findViewById(R.id.feedback_user_name);
        saveinfos = (Button) findViewById(R.id.feedback_save_infos);
        final Context context = this;
        saveinfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NetworkUtils network = NetworkUtils.getInstance(context);
                /*
                //******************************** TODO  URL为Wordpress评论提交页面.
                // 在Wordpress上必须设置以下参数.具体可从浏览器模拟提交评论.

                *   params.put("comment_post_ID", "2");
                    params.put("akismet_comment_nonce", "ef18ff2a35");
                    params.put("ak_js", "1444460833372");
                * */
                Log.i(ConfigConst.NOSETURLTAG, "********************:FeedbackActivity[88行]" + ConfigConst.NOSETURL);
                network.showToast("*******************FeedbackActivity[88行]" + ConfigConst.NOSETURL);
                //*******************************************
                //TODO 配置后解开注释. String url = "http://localhost/wp-comments-post.php";
                String url = ConfigConst.NOSETURL;
                String context = contextinfos.getText().toString();
                String useremail = usercontactinfos.getText().toString();
                if (!TextUtils.isEmpty(useremail) && !network.isEmail(useremail)) {
                    network.showToast("APP态度是:邮箱要不不输,要不就输对...", Toast.LENGTH_SHORT);
                }
                if (!TextUtils.isEmpty(context)) {
                    dialog.show();
                    String username = usernameinfo.getText().toString();
                    if (TextUtils.isEmpty(username)) {
                        username = "androidsubmit";
                    }
                    if (TextUtils.isEmpty(useremail)) {
                        useremail = "pruidong@qq.com";
                    }
                    context += "androidsubmit";
                    Map<String, String> params = new ArrayMap<String, String>(8);
                    params.put("author", username);
                    params.put("email", useremail);
                    params.put("comment", context);
                    params.put("comment_post_ID", "2");
                    params.put("akismet_comment_nonce", "suibianxiede");
                    params.put("ak_js", "suibianxiede");
                    network.postData(url, params, new NetworkUtils.VolleyResultData() {
                        @Override
                        public void onSuccess(String response) {
                            network.showToast("发送成功,若有回复,看下邮箱......", Toast.LENGTH_SHORT);
                            dialog.hide();
                            imm.hideSoftInputFromWindow(FeedbackActivity.this.getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            backs.setVisibility(View.GONE);
                            FeedbackActivity.this.finish();
                        }
                    }, dialog);
                } else {
                    network.showToast("不填反馈信息,服务器不接收哦..", Toast.LENGTH_SHORT);
                }
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (FeedbackActivity.this.getCurrentFocus() != null) {
                if (FeedbackActivity.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(FeedbackActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
