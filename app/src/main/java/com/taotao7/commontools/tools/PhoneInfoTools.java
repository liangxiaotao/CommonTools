package com.taotao7.commontools.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * author：taotao7
 * time:   2018/11/8
 * desc:   获取手机信息的工具类 主要信息在 android.os.Build
 */
public class PhoneInfoTools {

    /**
     * 手机品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 手机系统版本
     */
    public static String getRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 手机ID
     */
    public static String getID() {
        return Build.SERIAL;
    }

    /**
     * 手机CPU信息
     */
    public static String getCPU() {
        return Build.CPU_ABI;
    }

    /**
     * 手机名称
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * 手机语言
     */
    public static String getLang() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 手机分辨率
     */
    public static String getResolution(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels + "*" + metrics.heightPixels;
    }

    /**
     * 手机 AndroidID
     */
    public static String getAndroidID(Context context) {
        String androidId = Settings.System.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
        return androidId;
    }

    /**
     * 获取手机开机时间  单位是毫秒
     * elapsedRealtime()方法之后API大于17的才会有效
     *
     * @return
     */
    public static long getBootTime() {
        long bootTime = 0;
        if (Build.VERSION.SDK_INT > 17) {
            bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
            ;
        } else {
            //todo 通过读取/proc/stat 文件获取系统开机时间
        }
        return bootTime;
    }

    /**
     * 手机连接WIFI名称 即SSID
     *
     * @param context
     * @return
     */
    public static String getSSID(Context context) {
        String ssId = "";
        try {
            //实例化TelephonyManager对象
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //获取IMSI
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            ssId = wifiInfo.getSSID();
        } catch (SecurityException e) {
            //todo 没有权限
        }
        return ssId;
    }

    /**
     * 获取手机数据连接类型
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        String networkType = "";
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                networkType = "wifi";
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int subType = networkInfo.getSubtype();
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        networkType = "2G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        networkType = "3G";
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE: {
                        networkType = "4G";
                        break;
                    }
                    default:
                        networkType = "unKnown";
                }
            }
        }
        return networkType;
    }

    /**
     * 获取手机的运行商
     *
     * @param context
     * @return
     */
    public static String getMobileOperator(Context context) {
        String operator = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            String simOperator = telephonyManager.getSimOperator();
            if ("46000".equalsIgnoreCase(simOperator)
                    || "46002".equalsIgnoreCase(simOperator)
                    || "46007".equalsIgnoreCase(simOperator)) {
                operator = "中国移动";
            } else if ("46001".equalsIgnoreCase(simOperator)
                    || "46003".equalsIgnoreCase(simOperator)
                    || "46006".equalsIgnoreCase(simOperator)) {
                operator = "中国联通";
            } else if ("46005".equalsIgnoreCase(simOperator)) {
                operator = "中国电信";
            }
        }
        return operator;
    }
}
