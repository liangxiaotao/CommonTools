package com.taotao7.commontools.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

/**
 * author：taotao7
 * time:   2018,11,08
 * desc:   获取系统信息的工具类,由于一些值是需要权限的，可能拿不到
 */
public class SystemTools {

    /**
     * 获取手机IMEI
     * IMEI(International Mobile Equipment Identity)是国际移动设备身份码的缩写，
     * 国际移动装备辨识码，是由15位数字组成的”电子串号”，它与每台移动电话机一一对应，
     * 而且该码是全世界唯一的。每一只移动电话机在组装完成后都将被赋予一个全球唯一的一组号码，
     * 这个号码从生产到交付使用都将被制造生产的厂商所记录。
     *
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
        String imei = "";
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMEI
            imei = telephonyManager.getDeviceId();
        } catch (SecurityException e) {
            //todo 没有权限
        }
        return imei;
    }

    /**
     * 获取手机IMSI
     * 国际移动用户识别码（IMSI：International Mobile Subscriber IdentificationNumber）是区别移动用户的标志，
     * 储存在SIM卡中，可用于区别移动用户的有效信息。其总长度不超过15位，同样使用0~9的数字。其中MCC是移动用户所属国家代号，
     * 占3位数字，中国的MCC规定为460；MNC是移动网号码，由两位或者三位数字组成，中国移动的移动网络编码（MNC）为00；
     * 用于识别移动用户所归属的移动通信网；MSIN是移动用户识别码，用以识别某一移动通信网中的移动用户
     *
     * @param context
     * @return
     */
    public static String getIMSI(Context context) {
        String imsi = "";
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI
            imsi = telephonyManager.getSubscriberId();
        } catch (SecurityException e) {
            //todo 没有权限
        }
        return imsi;
    }

    /**
     * 获取手机MAC地址
     * MAC（Media Access Control或者Medium Access Control）地址，意译为媒体访问控制，或称为物理地址、硬件地址，
     * 用来定义网络设备的位置。在OSI模型中，第三层网络层负责 IP地址，第二层数据链路层则负责 MAC地址。
     * 因此一个主机会有一个MAC地址，而每个网络位置会有一个专属于它的IP地址
     *
     * @param context
     * @return
     */
    public static String getMAC(Context context) {
        String mac = "";
        if (Build.VERSION.SDK_INT < 23) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            mac = info.getMacAddress();
        } else {
            try {
                Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface netInterface = interfaces.nextElement();
                    if ("wlan0".equals(netInterface.getName()) || "eth0".equals(netInterface.getName())) {
                        byte[] addr = new byte[0];
                        addr = netInterface.getHardwareAddress();
                        if (addr == null || addr.length == 0) {
                            return "";
                        }
                        StringBuilder buf = new StringBuilder();
                        for (byte b : addr) {
                            buf.append(String.format("%02X:", b));
                        }
                        if (buf.length() > 0) {
                            buf.deleteCharAt(buf.length() - 1);
                        }
                        mac = buf.toString().toLowerCase(Locale.getDefault());
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return mac;
    }

    /**
     * ICCID：Integrate circuit card identity 集成电路卡识别码即SIM卡卡号，相当于手机号码的身份证。
     * ICCID为IC卡的唯一识别号码，共有20位数字组成，其编码格式为：XXXXXX 0MFSS YYGXX XXXX。
     * 前六位运营商代码：中国移动的为：898600；898602；898604；898607 ，中国联通的为：898601、898606、898609，中国电信898603。
     *
     * @param context
     * @return
     */
    public static String getICCID(Context context) {
        String iccId = "";
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI
            iccId = telephonyManager.getSimSerialNumber();
        } catch (SecurityException e) {
            //todo 没有权限
        }
        return iccId;
    }

    /**
     * BASEBAND-VER
     * 基带版本
     *
     * @return version
     */
    public static String getBaseband_Ver() {
        String version = "";
        try {
            Class cl = Class.forName("android.os.SystemProperties");
            Object invoker = cl.newInstance();
            Method m = cl.getMethod("get", new Class[]{String.class, String.class});
            Object result = m.invoke(invoker, new Object[]{"gsm.version.baseband", "no message"});
            version = (String) result;
        } catch (Exception e) {
        }
        return version;
    }

    /**
     * CORE-VER
     * 内核版本
     *
     * @return kernelVersion
     */
    public static String getLinuxCore_Ver() {
        Process process;
        String kernelVersion = "";
        String result = "";
        try {
            process = Runtime.getRuntime().exec("cat /proc/version");
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8 * 1024);
            while ((result = bufferedReader.readLine()) != null) {
                result += result;
            }
            inputStreamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(result)) {
            String Keyword = "version";
            int index = result.indexOf(Keyword);
            kernelVersion = result.substring(index + Keyword.length());
        }
        return kernelVersion;
    }

    /**
     * INNER-VER
     * 内部版本
     *
     * @return ver
     */
    public static String getInner_Ver() {
        String ver = "";
        if (android.os.Build.DISPLAY.contains(android.os.Build.VERSION.INCREMENTAL)) {
            ver = android.os.Build.DISPLAY;
        } else {
            ver = android.os.Build.VERSION.INCREMENTAL;
        }
        return ver;
    }

    /**
     * 获取CPU 最大的频率
     *
     * @return
     */
    public static double getCPUMaxFreq() {
        double cpuMaxFreq = 0;
        try {
            FileReader fileReader = new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            BufferedReader br = new BufferedReader(fileReader);
            String text = "";
            while ((text = br.readLine()) != null) {
                text = text.trim();
                if (!"".equals(text.trim())) {
                    cpuMaxFreq = Double.parseDouble(text.trim()) / 1000;
                }
                break;
            }
            br.close();
        } catch (Exception e) {//MyLog.d(e.toString());}returncpuMaxFreq;}
        }
        return cpuMaxFreq;
    }
}
