package com.jjyh.it.utiles.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ivvi.moassistant.MoApplication;
import com.ivvi.moassistant.global.Constant;

/**
 * ClassName:SharedPrefsUtils <br/>
 * Function: SharedPreferences工具类. <br/>
 *
 * @author suxinwei
 */
public class SharedPrefsUtils {

    private SharedPrefsUtils() {
    }

    public static boolean getBoolean(String key) {

        return getBoolean(Constant.SharePreference.GAL_PREFERENCE, key);
    }

    public static boolean getBoolean(String spFileName, String key) {

        return getBoolean(spFileName, key, false);
    }

    public static boolean getBoolean(String spFileName, String key, boolean def) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sp.getBoolean(key, def);
    }

    public static void putBoolean(String key, boolean value) {
        putBoolean(Constant.SharePreference.GAL_PREFERENCE, key, value);
    }

    public static void putBoolean(String spFileName, String key, boolean value) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static String getString(String key) {
        return getString(Constant.SharePreference.GAL_PREFERENCE, key);
    }


    public static String getString(String spFileName, String key) {
        return getString(spFileName, key, "");
    }

    private static String getString(String spFileName, String key, String def) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sp.getString(key, def);
    }

    public static void putString(String key, String value) {
        putString(Constant.SharePreference.GAL_PREFERENCE, key, value);
    }

    public static void putString(String spFileName, String key, String value) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static int getInt(String key) {
        return getInt(Constant.SharePreference.GAL_PREFERENCE, key);
    }

    public static int getInt(String spFileName, String key) {
        return getInt(spFileName, key, -1);
    }

    public static int getInt(String spFileName, String key, int def) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        return sp.getInt(key, def);
    }

    public static void putInt(String key, int value) {
        putInt(Constant.SharePreference.GAL_PREFERENCE, key, value);
    }


    public static void putInt(String spFileName, String key, int value) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(spFileName, Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static long getLong(String key, int value) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(Constant.SharePreference.GAL_PREFERENCE, Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }
    public static void putLong(String key,long value) {
        SharedPreferences sp =
                MoApplication.getInstance().getSharedPreferences(Constant.SharePreference.GAL_PREFERENCE, Context.MODE_PRIVATE);
        sp.edit().putLong(key, value).commit();
    }
}
