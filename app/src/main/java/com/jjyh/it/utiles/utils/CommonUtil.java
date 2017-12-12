package com.jjyh.it.utiles.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;

import com.ivvi.moassistant.MoApplication;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by suxinwei on 2017/8/23.
 */

public class CommonUtil {
    private static final int JSON_INDENT = 4;
    private static long lastClickTime;

    private CommonUtil() {
    }

    public static String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        return format.format(new Date());
    }

    public static String formatToDataTime(long milliSeconds) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sd.format(Long.valueOf(milliSeconds));
    }

    public static HashMap<String, String> decodeUrl(String query) {
        HashMap map = new HashMap();
        if(query != null) {
            String[] pairs = query.split("&");
            String[] var3 = pairs;
            int var4 = pairs.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String pair = var3[var5];
                String[] keyAndValues = pair.split("=");
                if(keyAndValues != null && keyAndValues.length == 2) {
                    String key = keyAndValues[0];
                    String value = keyAndValues[1];
                    if(!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                        map.put(URLDecoder.decode(key), URLDecoder.decode(value));
                    }
                }
            }
        }

        return map;
    }

    public static String encodeUrl(Bundle params) {
        if(params != null && !params.isEmpty()) {
            boolean first = true;
            StringBuilder sb = new StringBuilder();
            Iterator var3 = params.keySet().iterator();

            while(var3.hasNext()) {
                String key = (String)var3.next();
                String paramValue = params.getString(key);
                if(paramValue != null) {
                    if(first) {
                        first = false;
                    } else {
                        sb.append("&");
                    }

                    sb.append(URLEncoder.encode(key)).append("=").append(URLEncoder.encode(paramValue));
                }
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    public static void showAlert(Context context, String title, String text) {
        AlertDialog alertDialog = (new AlertDialog.Builder(context)).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    public static boolean isFastDoubleClick() {
        return isFastDoubleClick(500L);
    }

    public static boolean isFastDoubleClick(long offset) {
        long time = System.currentTimeMillis();
        if(time - lastClickTime < offset) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    public static String formatJson(String json) {
        String formatted = "";
        if(json != null && json.length() != 0) {
            try {
                if(json.startsWith("{")) {
                    JSONObject e = new JSONObject(json);
                    formatted = e.toString(4);
                } else if(json.startsWith("[")) {
                    JSONArray e1 = new JSONArray(json);
                    formatted = e1.toString(4);
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            return formatted;
        } else {
            return formatted;
        }
    }

    public static void closeQuietly(Closeable... closeables) {
        Closeable[] var1 = closeables;
        int var2 = closeables.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Closeable c = var1[var3];

            try {
                if(c != null) {
                    c.close();
                }
            } catch (IOException var6) {
                var6.printStackTrace();
            }
        }

    }

    public static String getDeviceUniqueID() {
        String devIDShort = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10;
        if(Build.VERSION.SDK_INT >= 21) {
            devIDShort = devIDShort + Build.SUPPORTED_ABIS[0].length() % 10;
        } else {
            devIDShort = devIDShort + Build.CPU_ABI.length() % 10;
        }

        devIDShort = devIDShort + (Build.DEVICE.length() % 10 + Build.MANUFACTURER.length() % 10 + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10);

        String serial;
        try {
            serial = Build.class.getField("SERIAL").get((Object)null).toString();
            return (new UUID((long)devIDShort.hashCode(), (long)serial.hashCode())).toString();
        } catch (Exception var3) {
            serial = "Dueros000";
            return (new UUID((long)devIDShort.hashCode(), (long)serial.hashCode())).toString();
        }
    }

    //add by jay 20170828
    public static int dp2px(Context context, float dipValue) {
        int rs = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, context.getResources().getDisplayMetrics());
        return rs;
    }


    //add by pengbin 2017-09-02
    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        String output = "";

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output += temp[0];
                } else
                    output += Character.toString(input[i]);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }
    //add by pengbin 2017-09-02
    /**
     * 获取汉字串拼音首字母，英文字符不变
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat);
                    if (temp != null) {
                        pybf.append(temp[0].charAt(0));
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }
    //add by pengbin 2017-09-02
    /**
     * 获取汉字串拼音，英文字符不变
     * @param chinese 汉字串
     * @return 汉语拼音
     */
    public static String getFullSpell(String chinese) {
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 128) {
                try {
                    pybf.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pybf.append(arr[i]);
            }
        }
        return pybf.toString();
    }


    //pengbin md5加密
    public static String getStringMD5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    //下载速度转换  k/s
    public static String transformDownloadSpeed (int size) {

        if (size < 1024) {
            return String.valueOf(size) + "k/s";
        } else {
            size = size / 1024;
        }

        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "M/s";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "G/s";
        } else {
            //否则如果要以T为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "T/s";
        }
    }

    public static String getFileMD5(File file) {
        if (file == null || !file.isFile() || !file.exists()) {
            return "";
        }
        FileInputStream in = null;
        String result = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            result = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(null!=in){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getVersionCode() {
        String versionName = "";
        PackageManager pm = MoApplication.getInstance().getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(MoApplication.getInstance().getPackageName(), 0);
            versionName = packageInfo.versionCode+"";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
    /**
     * geek相关的界面显示及功能的确定
     */
    public static boolean isOpenGeek = true;
    /**
     * 天气推送开关
     */
    public static boolean isOpenWeatherPush = true;
    /**
     * 节日推送开关
     */
    public static boolean isOpenFestivalPush = true;

    /**
     * 节日天气推送Feature开关
     */
    public static boolean isOpenFeaturePush = true;

    public static String ensureStringNotNull(String value) {
        if (value == null) {
            return "";
        }
        return "null".equals(value) ? "" : value;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
