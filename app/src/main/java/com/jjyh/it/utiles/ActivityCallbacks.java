package com.jjyh.it.utiles;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.jjyh.it.utiles.utils.ActivityUtils;


/**
 * Created by suxinwei on 2017/5/11.
 */

public class ActivityCallbacks implements Application.ActivityLifecycleCallbacks {

    private BroadcastReceiver mHomekeyReceiver;

    private void initHomeKeyReceiver(Activity activity) {
        if (mHomekeyReceiver == null) {
//            mHomekeyReceiver = new HomeKeyReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            activity.registerReceiver(mHomekeyReceiver, filter);
        }
    }

    private void removeHomeKeyReceiver(Activity activity) {
        if (mHomekeyReceiver != null) {
            activity.unregisterReceiver(mHomekeyReceiver);
            mHomekeyReceiver = null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityUtils.addActivity(activity);
        Log.d("Test1111", "onActivityCreated: "+activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
//        initHomeKeyReceiver(activity);
        Log.d("Test1111", "onActivityStarted: "+activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d("Test1111", "onActivityResumed: "+activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d("Test1111", "onActivityPaused: "+activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityStopped(Activity activity) {

        Log.d("Test1111", "onActivityStopped: "+activity.getComponentName().getClassName());
        removeHomeKeyReceiver(activity);
    }


    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d("Test1111", "onActivitySaveInstanceState: "+activity.getComponentName().getClassName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityUtils.removeActivity(activity);
        Log.d("Test1111", "onActivityDestroyed: "+activity.getComponentName().getClassName());
    }
}
