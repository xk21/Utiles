package com.jjyh.it.utiles.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityUtils {
    public static List<Activity> mActivities = new ArrayList<>();

    private ActivityUtils() {
    }

    public static void addActivity(Activity activity) {
        if (!mActivities.contains(activity)) {
            mActivities.add(activity);
        }
    }

    public static Activity getMainActivity() {
        if (mActivities.size() == 0) {
            return null;
        }
        return mActivities.get(0);
    }

    public static Activity getLastActivity() {
        if (mActivities.size() == 0) {
            return null;
        }
        return mActivities.get(mActivities.size() - 1);
    }

    public static void removeActivity(Activity activity) {
        mActivities.remove(activity);
    }

    public static void finishAll() {
        if (mActivities.size() == 0) {
            return;
        }
        for (Activity activity : mActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
