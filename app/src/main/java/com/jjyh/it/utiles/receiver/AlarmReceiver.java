package com.jjyh.it.utiles.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.text.TextUtils;
import android.util.Log;

import com.jjyh.it.utiles.Constants;
import com.jjyh.it.utiles.WeekBean;
import com.jjyh.it.utiles.utils.SPSaveList;

import java.util.List;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    public SPSaveList mSpSaveList;
    public static String alarm_action = "com.ivvi.moassistant.push.alarm.ACTION";
    public static String weatherholiday_action = "com.ivvi.moassistant.push.weatherholiday.ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mSpSaveList = new SPSaveList(context, Constants.MO_SETTING_SP_WEATHER_FESTIVAL);

        if (intent.getAction().equals(weatherholiday_action)){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, AlarmPushttsActivity.class);
            String type = intent.getStringExtra("pushType");
            if ("weather".equals(type)){

                if (null!=mSpSaveList.getWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY)) {
                    Calendar mcalendar = Calendar.getInstance();
                    int DAY_OF_WEEK = mcalendar.get(Calendar.DAY_OF_WEEK) -1;
                    intent.putExtra("PushAlarm","pushWeaTher");
                    List<WeekBean> mWeekBeans = mSpSaveList.getWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY);
                    if (DAY_OF_WEEK == 0 && mWeekBeans.get(0).isSunday()){
                        context.startActivity(intent);
                    }else if (DAY_OF_WEEK == 1 && mWeekBeans.get(0).isMonday()){
                        context.startActivity(intent);
                    }else if (DAY_OF_WEEK == 2 && mWeekBeans.get(0).isTuesday()){
                        context.startActivity(intent);
                    }else if (DAY_OF_WEEK == 3 && mWeekBeans.get(0).isWednesday()){
                        context.startActivity(intent);
                    }else if (DAY_OF_WEEK == 4 && mWeekBeans.get(0).isThursday()){
                        context.startActivity(intent);
                    }else if (DAY_OF_WEEK == 5 && mWeekBeans.get(0).isFriday()){
                        context.startActivity(intent);
                    }else if (DAY_OF_WEEK == 6 && mWeekBeans.get(0).isSaturday()){
                        context.startActivity(intent);
                    }else {
                        Log.e(TAG,"this weekend is not set");
                    }
                }else{
                    intent.putExtra("PushAlarm","pushWeaTher");
                    context.startActivity(intent);
                }
            }else if ("holiday".equals(type)){
                intent.putExtra("PushAlarm","pushHoliday");
                context.startActivity(intent);
            }

        }else if (intent.getAction().equals(alarm_action)){

            Intent newintent = new Intent(weatherholiday_action);
            boolean misOpenWeatherPush = (boolean) mSpSaveList.getValue(Constants.MO_SETTING_SP_WEATHER_REMIND_KEY, true); //天气开关
            boolean misOpenHolidayPush = (boolean) mSpSaveList.getValue(Constants.MO_SETTING_SP_FESTIVAL_REMIND_KEY, true); //节日开关

            if (misOpenHolidayPush){
                //setTime(context);
                newintent.putExtra("pushType","holiday");
                int delay_24hour =1000 * 60 * 60 * 24;   //间隔24h
                int mHolidayInterval = (int) mSpSaveList.getValue(Constants.MO_SETTING_SP_FESTIVAL_INTERVAL_KEY, 1); //节日提前天数
                String mWeatherTime = (String) mSpSaveList.getValue(Constants.MO_SETTING_SP_WEATHER_TIME_KEY, Constants.MO_SETTING_WEATHER_DEFAULT_TIME); //天气时间
                String mHolidayTime = (String) mSpSaveList.getValue(Constants.MO_SETTING_SP_FESTIVAL_TIME_KEY, Constants.MO_SETTING_FESTIVAL_DEFAULT_TIME); //节日提醒时间

                Log.e(TAG, "HolidayTime====" + mHolidayTime);
                if (!TextUtils.isEmpty(mHolidayTime)) {
                    String aa[] = mHolidayTime.split(":");
                    int hour = Integer.valueOf(aa[0]);
                    int min = Integer.valueOf(aa[1]);
                    int alarmTime = 60 * 60 * hour + 60 * min;

                    long nextWouldBeTime = getNextWouldBeTime(System.currentTimeMillis(), alarmTime);
                    Log.e(TAG, "nextWouldBeTime===" + nextWouldBeTime);
                    Log.e(TAG, "next launch mHolidayTime time ===" + (nextWouldBeTime - System.currentTimeMillis()));
                    PendingIntent pi = PendingIntent.getBroadcast(context, 1, newintent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextWouldBeTime, delay_24hour, pi);
                }
            }else {
                PendingIntent pi = PendingIntent.getBroadcast(context, 1, newintent, 0);
                Log.e(TAG, "qu xiao holiday alarm");
                alarmManager.cancel(pi);
            }

            ///////天气
            if (misOpenWeatherPush){
                newintent.putExtra("pushType","weather");
                int delay_24hour =1000 * 60 * 60 * 24;   //间隔24h
                String mWeatherTime = (String) mSpSaveList.getValue(Constants.MO_SETTING_SP_WEATHER_TIME_KEY, Constants.MO_SETTING_WEATHER_DEFAULT_TIME); //天气时间

                Log.e(TAG, "mWeatherTime====" + mWeatherTime);
                if (!TextUtils.isEmpty(mWeatherTime)) {
                    String aa[] = mWeatherTime.split(":");
                    int hour = Integer.valueOf(aa[0]);
                    int min = Integer.valueOf(aa[1]);

                    int alarmTime = 60 * 60 * hour + 60 * min;

                    long nextWouldBeTime = getNextWouldBeTime(System.currentTimeMillis(), alarmTime);
                    Log.e(TAG, "nextWouldBeTime===" + nextWouldBeTime);
                    Log.e(TAG, "next launch mWeatherTime time ===" + (nextWouldBeTime - System.currentTimeMillis()));
                    PendingIntent pi = PendingIntent.getBroadcast(context, 2, newintent, 0);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, nextWouldBeTime, delay_24hour, pi);
                }else {
                    Log.e(TAG,"mWeatherTime is null");
                }
            }else{
                PendingIntent pi = PendingIntent.getBroadcast(context, 2, newintent, 0);
                Log.e(TAG, "qu xiao weather alarm");
                alarmManager.cancel(pi);
            }
        }
    }


    static long getNextWouldBeTime(long curTime, long alarmTime) {
        Calendar c = Calendar.getInstance();
        long nextWouldBeTime = computeCalendarTime(c, curTime, alarmTime);
        return nextWouldBeTime;
    }

    static long computeCalendarTime(Calendar c, long curTime, long alarmTime) {
        c.setTimeInMillis(curTime);
        int val = (int) alarmTime / (60 * 60); //小时
        c.set(Calendar.HOUR_OF_DAY, val);
        alarmTime -= val * (60 * 60);
        val = (int) alarmTime / 60;       //分钟
        c.set(Calendar.MINUTE, val);
        c.set(Calendar.SECOND, (int) alarmTime - (val * 60));   //秒
        c.set(Calendar.MILLISECOND, 0);

        long newTime = c.getTimeInMillis();   //newTime定时时间 ，curTime当前时间
        if (newTime < curTime) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            newTime = c.getTimeInMillis();
        }
        return newTime;
    }
}
