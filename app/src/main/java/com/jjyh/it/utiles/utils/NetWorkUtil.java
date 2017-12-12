package com.jjyh.it.utiles.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by suxinwei on 2017/8/23.
 */

public class NetWorkUtil {
    private NetWorkUtil() {
    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }

        return false;
    }

    public static NetWorkUtil.NetState getNetStatus(Context context) {
        NetWorkUtil.NetState stateCode = NetWorkUtil.NetState.NET_NO;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            switch (ni.getType()) {
                case 0:
                    switch (ni.getSubtype()) {
                        case 1:
                        case 2:
                        case 4:
                        case 7:
                        case 11:
                            stateCode = NetWorkUtil.NetState.NET_2G;
                            return stateCode;
                        case 3:
                        case 5:
                        case 6:
                        case 8:
                        case 9:
                        case 10:
                        case 12:
                        case 14:
                        case 15:
                            stateCode = NetWorkUtil.NetState.NET_3G;
                            return stateCode;
                        case 13:
                            stateCode = NetWorkUtil.NetState.NET_4G;
                            return stateCode;
                        default:
                            stateCode = NetWorkUtil.NetState.NET_UNKNOWN;
                            return stateCode;
                    }
                case 1:
                    stateCode = NetWorkUtil.NetState.NET_WIFI;
                    break;
                default:
                    stateCode = NetWorkUtil.NetState.NET_UNKNOWN;
            }
        }

        return stateCode;
    }

    public static enum NetState {
        NET_NO,
        NET_2G,
        NET_3G,
        NET_4G,
        NET_WIFI,
        NetState,
        NET_UNKNOWN;

        private NetState() {
        }
    }

    /**
     * make true current connect service is wifi
     * @param mContext
     * @return
     */
    public static boolean isWifiOpened(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }
}
