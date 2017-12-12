package com.jjyh.it.utiles.utils;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ivvi.moassistant.MoApplication;


public class ToastUtils {
    private static Toast toast;


    private ToastUtils() {
    }

    public static void showToast(int resID) {
        showToast(MoApplication.getInstance().getString(resID));
    }

    public static void showToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(MoApplication.getInstance(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        try {
            toast.show();
        } catch (Exception e) {
            Log.e("xzw", "showToast exception " + Log.getStackTraceString(e));
        }
    }

    public static void showToast(int resID, boolean isLong) {
        showToast(MoApplication.getInstance().getString(resID), isLong);
    }

    public static void showToast(String msg, boolean isLong) {
        if (toast == null) {
            toast = Toast.makeText(MoApplication.getInstance(), msg, Toast.LENGTH_LONG);
        } else {
            toast.setText(msg);
        }
        try {
            toast.show();
        } catch (Exception e) {
            Log.e("xzw", "showToast exception " + Log.getStackTraceString(e));
        }
    }

    public static void showToastOnMainThread(int resID) {
        showToastOnMainThread(MoApplication.getInstance().getString(resID));
    }

    public static void showToastOnMainThread(final String msg) {
        new Handler(MoApplication.getInstance().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(MoApplication.getInstance(), msg, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(msg);
                }
                try {
                    toast.show();
                } catch (Exception e) {
                    Log.e("xzw", "showToast exception " + Log.getStackTraceString(e));
                }
            }
        });
    }

    public static void showToastOnMainThread(int resID, boolean isLong) {
        showToastOnMainThread(MoApplication.getInstance().getString(resID), isLong);
    }

    public static void showToastOnMainThread(final String msg, boolean isLong) {
        new Handler(MoApplication.getInstance().getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(MoApplication.getInstance(), msg, Toast.LENGTH_LONG);
                } else {
                    toast.setText(msg);
                }
                try {
                    toast.show();
                } catch (Exception e) {
                    Log.e("xzw", "showToast exception " + Log.getStackTraceString(e));
                }
            }
        });
    }
}
