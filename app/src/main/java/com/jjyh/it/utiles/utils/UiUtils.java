package com.jjyh.it.utiles.utils;

import com.ivvi.moassistant.MoApplication;

/**
 * Created by suxinwei on 2017/8/24.
 */

public class UiUtils {
    public static String getString(int id) {
        return MoApplication.getInstance().getResources().getString(id);
    }

    public static String getString(int id, Object... formatArgs) {
        return MoApplication.getInstance().getResources().getString(id, formatArgs);
    }

    public static String[] getStringArray(int id) {
        return MoApplication.getInstance().getResources().getStringArray(id);
    }

}
