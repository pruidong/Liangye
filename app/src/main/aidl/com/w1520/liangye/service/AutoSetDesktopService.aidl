// AutoSetDesktopService.aidl
package main.aidl.com.w1520.liangye.service;

// Declare any non-default types here with import statements
/**
自动设置桌面Aidl
*/
interface AutoSetDesktopService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    void startService();
    void stopService();
}
