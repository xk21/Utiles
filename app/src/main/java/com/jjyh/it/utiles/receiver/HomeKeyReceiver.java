package com.jjyh.it.utiles.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jjyh.it.utiles.utils.ActivityUtils;


/**
 * Created by suxinwei on 2017/8/11.
 */

public class HomeKeyReceiver extends BroadcastReceiver {

    private String mLastBrocastReason = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra("reason");
                if (null != reason && reason.equals("homekey") && !mLastBrocastReason.equals("recentapps")) {
                    Log.w("HomekeyReceiver", reason);
                    ActivityUtils.finishAll();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
                mLastBrocastReason = reason;
            }

        } catch (Exception e) {
            Log.w("HomekeyReceiver", e);
        }
    }
}
