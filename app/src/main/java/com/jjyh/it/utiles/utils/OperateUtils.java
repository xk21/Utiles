package com.jjyh.it.utiles.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ivvi.moassistant.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by suxinwei on 2017/8/23.
 */

public class OperateUtils {
    public static boolean callPhone(Context context, String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber) && context instanceof Activity) {
            Uri uri = Uri.parse("tel:" + phoneNumber);
            Intent intent = new Intent("android.intent.action.CALL", uri);

            try {
                context.startActivity(intent);
                return true;
            } catch (Exception var6) {
                var6.printStackTrace();
            }
        }

        return false;
    }

    public static boolean sendSms(Context context, String phoneNumber, String msgContent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSer = tm.getSimSerialNumber();
        SmsManager smsManager = SmsManager.getDefault();
        if (!TextUtils.isEmpty(simSer)) {
            if (msgContent.length() >= 70) {
                ArrayList ms = smsManager.divideMessage(msgContent);
                Iterator var8 = ms.iterator();

                while (var8.hasNext()) {
                    String str = (String) var8.next();
                    if (!TextUtils.isEmpty(phoneNumber)) {
                        smsManager.sendTextMessage(phoneNumber, (String) null, str, (PendingIntent) null, (PendingIntent) null);
                    }
                }
            } else if (!TextUtils.isEmpty(phoneNumber)) {
                smsManager.sendTextMessage(phoneNumber, (String) null, msgContent, (PendingIntent) null, (PendingIntent) null);
            }
        } else {
            ToastUtils.showToast(UiUtils.getString(R.string.sim_error_msg));
        }

        return false;
    }

    public static void launchAppByDeepLink(Context context, String deepLink) {
        try {
            Uri e = Uri.parse(deepLink);
            Intent intent = new Intent("android.intent.action.VIEW", e);
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static boolean launchAppByName(Context context, String appName) {
        PackageManager pManager = context.getApplicationContext().getPackageManager();
        List paklist = pManager.getInstalledPackages(0);
        boolean isLaunched = false;
        if (paklist != null) {
            try {
                for (int e = 0; e < paklist.size(); ++e) {
                    PackageInfo packageInfo = (PackageInfo) paklist.get(e);
                    String appNameTmp = packageInfo.applicationInfo.loadLabel(pManager).toString().toLowerCase();
                    if (appName.contains(appNameTmp) || appNameTmp.contains(appName)) {
                        String packageName = packageInfo.applicationInfo.packageName;
                        isLaunched = true;
                        launchAppByPackageName(context, packageName);
                    }
                }
            } catch (Exception var10) {
                var10.printStackTrace();
            }
        }

        return isLaunched;
    }

    public static boolean launchAppByPackageName(Context context, String packageName) {
        PackageInfo packageinfo = null;

        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException var11) {
            var11.printStackTrace();
        }

        if (packageinfo != null) {
            Intent resolveIntent = new Intent("android.intent.action.MAIN", (Uri) null);
            resolveIntent.addCategory("android.intent.category.LAUNCHER");
            resolveIntent.setPackage(packageinfo.packageName);
            List resolveinfoList = context.getPackageManager().queryIntentActivities(resolveIntent, 0);
            if (resolveinfoList.size() == 0) {
                LogUtils.e("IAppLauncherImpl", "launch app error!");
                return false;
            } else {
                ResolveInfo resolveinfo = (ResolveInfo) resolveinfoList.iterator().next();
                if (resolveinfo != null) {
                    String name = resolveinfo.activityInfo.packageName;
                    String className = resolveinfo.activityInfo.name;
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.addCategory("android.intent.category.LAUNCHER");
                    ComponentName cn = new ComponentName(name, className);
                    intent.setComponent(cn);
                    intent.addFlags(268435456);
                    context.startActivity(intent);
                }

                return true;
            }
        } else {
            ToastUtils.showToast(UiUtils.getString(R.string.app_error_msg));
            return false;
        }
    }

    public static void openWebsite(Context context, String url) {
        if(TextUtils.isEmpty(url)) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        intent.setClassName("com.ivvi.browser","com.ivvi.browser.BrowserActivity");
        context.startActivity(intent);
    }
    public static void openWebActivity(Context context, String url, String title) {
        if(TextUtils.isEmpty(url)) return;
        Intent intent = new Intent("com.ivvi.moassistant.action.WEB_VIEW");
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }
}
