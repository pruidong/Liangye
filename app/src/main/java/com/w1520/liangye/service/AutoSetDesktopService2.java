package com.w1520.liangye.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import com.w1520.liangye.utils.DateUtils;
import com.w1520.liangye.utils.NetworkUtils;
import main.aidl.com.w1520.liangye.service.AutoSetDesktopService;

import java.util.Calendar;
import java.util.Date;

/**
 * 自动设置桌面Service
 *
 *
 * Created by puruidong on 8/29/15.
 */
public class AutoSetDesktopService2 extends Service {

    private String TAG = getClass().getName();
    private String Process_Name = "com.w1520.liangye.servicetest2:service1";

    private AlarmManager alarmManager;

    /**
     *启动Service1
     */
    private AutoSetDesktopService startS1 = new AutoSetDesktopService.Stub() {

        @Override
        public void stopService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), AutoSetDesktopService1.class);
            getBaseContext().stopService(i);
        }

        /**
         * Demonstrates some basic types that you can use as parameters
         * and return values in AIDL.
         *
         * @param anInt
         * @param aLong
         * @param aBoolean
         * @param aFloat
         * @param aDouble
         * @param aString
         */
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void startService() throws RemoteException {
            Intent i = new Intent(getBaseContext(), AutoSetDesktopService1.class);
            getBaseContext().startService(i);

        }
    };

    @Override
    public void onTrimMemory(int level){
        keepService1();
    }

    public void onCreate() {
        keepService1();
    }

    /**
     * 判断Service1是否还在运行，如果不是则启动Service1
     */
    private  void keepService1(){
        NetworkUtils network = NetworkUtils.getInstance(this.getBaseContext());
        boolean isRun = network.isProessRunning( Process_Name);
        if (isRun == false) {
            try {
                startS1.startService();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(alarmManager==null){
            alarmManager = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        }
        DateUtils date = DateUtils.getInstance();
        date.rollDate(new Date(), Calendar.MINUTE,2);
        int id = (int)(date.getCurrentTime()/60/1000);
        Intent receiver = new Intent(getBaseContext(), AutoSetDesktopAlarmReceiver.class);
        receiver.putExtra("service","Service2");
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                date.getCurrentTime(),
                3*60*1000,
                PendingIntent.getBroadcast(getBaseContext(), id, receiver, 0));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return (IBinder) startS1;
    }


}
