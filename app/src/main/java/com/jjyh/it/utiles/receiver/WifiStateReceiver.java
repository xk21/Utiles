package com.jjyh.it.utiles.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.jjyh.it.utiles.Constants;
import com.jjyh.it.utiles.MainActivity;




public class WifiStateReceiver extends BroadcastReceiver {

    private MainActivity mActivity;

    public WifiStateReceiver(MainActivity mainActivity) {
        mActivity = mainActivity;
    }

    public WifiStateReceiver() {
    }

    private int getWifiRssi(Context context) {
        // Wifi的连接速度及信号强度：
        int strength = 0;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info.getBSSID() != null) {
            // 链接信号强度，5为获取的信号强度值在5以内
            strength = info.getRssi();//WifiManager.calculateSignalLevel(info.getRssi(), 5);
        }
        return strength;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            if (context != null) {
                int rssi = getWifiRssi(context);
                Bundle bundle = new Bundle();
                bundle.putInt("wifirssi", rssi);
                EventBus.getDefault().post(new MainEvent(Constant.EVENT_WIFI_RSSI, bundle));
            }
        }

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            Bundle bundle = new Bundle();
            if (activeNetwork != null) {
                bundle.putInt("network", 1);
            } else {
                bundle.putInt("network", 0);
            }
            EventBus.getDefault().post(new GeekEvent(Constants.MO_GEEK_NETWORK_STATUS, bundle));
            EventBus.getDefault().post(new MainEvent(Constants.MO_GEEK_NETWORK_STATUS, bundle));
        }
    }
}
