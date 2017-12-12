package com.jjyh.it.utiles.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.jjyh.it.utiles.Constants;

import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;


public class GeekUtils {

    public static String getAndroidId(Context context) {
        String androidId = Constants.DEFAULT_ANDROID_ID;
        if (context != null) {
            try {
                androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Throwable e) {
            }
        }

        return TextUtils.isEmpty(androidId) ? Constants.DEFAULT_ANDROID_ID : androidId;
    }

    public static String getImei(Context context) {
        String imei = "";
        try {
            if (context != null) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    imei = telephonyManager.getDeviceId();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return imei == null ? "" : imei;
    }


    public static String getDeviceUUID(Context context) {
        if (context == null) {
            return UUID.randomUUID().toString();
        }

        UUID uuid;
        String androidId = getAndroidId(context);
        if (androidId.equals(Constants.DEFAULT_ANDROID_ID)) {
            String deviceId = getImei(context);
            if (TextUtils.isEmpty(deviceId)) {
                uuid = UUID.randomUUID();
            } else {
                uuid = UUID.nameUUIDFromBytes(deviceId.getBytes());
            }
        } else {
            uuid = UUID.nameUUIDFromBytes(androidId.getBytes());
        }

        String uuidString = uuid.toString();

        return uuidString;
    }

    public static Bitmap convert2BorderBitmap(Bitmap bitmap, int strokeWidth, Context context) {
        if (bitmap == null)
            return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int roundPx = dip2px(context, 12);

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, width);
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(GeekUtils.dip2px(context, strokeWidth)); // dp转px
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        return output;
    }
    
    public static Bitmap circleCrop( Bitmap source) {
        if (source == null) return null;
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }
    
    
    
    public static int dip2px(Context context, float dipValue) {
        if (context == null) {
            return 0;
        }

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static String restoreFieldString(Context context, String key, String defaultValue) {
        if (context == null) {
            return defaultValue;
        }

        SharedPreferences spf = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        String value = spf.getString(key, defaultValue);
        return value;
    }

    public static void saveField(Context context, String key, String value) {
        if (context == null) {
            return;
        }

        SharedPreferences spf = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void launchServiceActivity(Context context, String actionUrl, String actionName) {
//        if (context == null) {
//            return;
//        }
//
//        Intent intent = new Intent(context, SceneServiceActivity.class);
//        intent.putExtra(Constants.BUNDLE_KEY_ACTION_URL, actionUrl);
//        intent.putExtra(Constants.BUNDLE_KEY_ACTION_NAME, actionName);
//        if (!(context instanceof Activity)) {
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        }
//        context.startActivity(intent);
    }
}
