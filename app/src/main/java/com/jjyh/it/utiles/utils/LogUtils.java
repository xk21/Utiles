package com.jjyh.it.utiles.utils;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class LogUtils {

    public static final int LEVEL_NONE = 0;
    public static final int LEVEL_VERBOSE = 1;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_ERROR = 5;
    private static int mDebuggable = LEVEL_ERROR;
    private static String mTag = "MoAssistant";
    private static long mTimestamp = 0L;

    private LogUtils() {
    }

    public static void v(String msg) {
        v(mTag, msg);
    }

    public static void v(String tag, String msg) {
        if (mDebuggable >= LEVEL_VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        d(mTag, msg);
    }

    public static void d(String tag, String msg) {
        if (mDebuggable >= LEVEL_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        Log.i(mTag, msg);
    }

    public static void i(String tag, String msg) {
        if (mDebuggable >= LEVEL_INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        w(mTag, msg);
    }

    public static void w(String tag, String msg) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, msg);
        }
    }

    public static void w(Throwable tr) {
        w(mTag, tr);
    }

    public static void w(String tag, Throwable tr) {
        w(tag, "", tr);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_WARN) {
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String msg) {
        e(mTag, msg);
    }

    public static void e(String tag, String msg) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg);
        }
    }

    public static void e(Throwable tr) {
        e(mTag, tr);
    }

    public static void e(String tag, Throwable tr) {
        e(tag, "", tr);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (mDebuggable >= LEVEL_ERROR) {
            Log.e(tag, msg, tr);
        }
    }

    public static void msgStartTime(String msg) {
        mTimestamp = System.currentTimeMillis();
        if (!TextUtils.isEmpty(msg)) {
            e("[Started：" + mTimestamp + "]" + msg);
        }
    }

    public static void elapsed(String msg) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - mTimestamp;
        mTimestamp = currentTime;
        e("[Elapsed：" + elapsedTime + "]" + msg);
    }

    public static <T> void printList(List<T> list) {
        if (list != null && list.size() >= 1) {
            int size = list.size();
            i("---begin---");

            for (int i = 0; i < size; ++i) {
                i(i + ":" + list.get(i).toString());
            }

            i("---end---");
        }
    }

    public static <T> void printArray(T[] array) {
        if (array != null && array.length >= 1) {
            int length = array.length;
            i("---begin---");

            for (int i = 0; i < length; ++i) {
                i(i + ":" + array[i].toString());
            }

            i("---end---");
        }
    }
}
