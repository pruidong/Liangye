package com.w1520.liangye.service;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.w1520.liangye.app.R;
import com.w1520.liangye.entity.ConfigConst;
import com.w1520.liangye.utils.DateUtils;
import com.w1520.liangye.utils.NetworkUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * 自动设置桌面.
 *
 * Created by puruidong on 8/29/15.
 */
public class AutoSetDesktopAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
       String service  = "NOMARL";
        if( intent.hasExtra("service")){
            service = intent.getStringExtra("service");
        }
        DateUtils dateutils = DateUtils.getInstance();
        Calendar customdate = dateutils.createCalendar(18, 45);
        long currentdate = dateutils.getCurrentTime();
        if (customdate.getTimeInMillis() == currentdate) {
            Calendar ca = dateutils.rollDate(new Date(), Calendar.DAY_OF_MONTH, (0));
            String month = dateutils.getDate(ca.getTime(), DateUtils.DATE_FORMAT_YYMM);
            String nowdate = dateutils.getDate(ca.getTime(), DateUtils.DATE_FORMAT_YYYYMMDD);
            //********************************   TODO URL为图片
            Log.i(ConfigConst.NOSETURLTAG,"********************:AutoSetDesktopAlarmReceiver[42行]"+ConfigConst.NOSETURL);
            //*******************************************
            String url = "http://localhost/" + month + "/" + nowdate + ".jpg";
           final NetworkUtils network = NetworkUtils.getInstance(context);
            network.getImageByImageRequest(url, new NetworkUtils.onImageLoaderListener() {
                @Override
                public void onSuccessImage(Bitmap bitmap) {
                    bitmap = network.comp(bitmap);
                    try {
                        network.setWallPaper(bitmap);
                        //send .
                        final int SEND_NOTICE = 1 ;
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                        notification.setSmallIcon(R.drawable.icon24);
                        notification.setContentTitle("设置壁纸");
                        notification.setContentText("恭喜主人,小的已经帮您设置好了~~");
                        notification.setAutoCancel(true);
                        nm.notify(SEND_NOTICE,notification.build());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
        }
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(PendingIntent.getBroadcast(context, getResultCode(), new Intent(context, AutoSetDesktopAlarmReceiver.class), 0));
    }
}
