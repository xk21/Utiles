/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import com.jjyh.it.utiles.Constants;
import com.jjyh.it.utiles.R;
import com.jjyh.it.utiles.WeekBean;
import com.jjyh.it.utiles.utils.SPSaveList;

import java.util.ArrayList;
import java.util.List;


public class AutoStartupRepeatPreference extends Preference implements OnClickListener {
    /** Log Tag */
    private static final String TAG = "YST.AutoStartupRepeat";
    /** 0 */
    private CheckBox mMondayButton;
    private CheckBox mTuesdayButton;
    private CheckBox mWednesdayButton;
    private CheckBox mThursdayButton;
    private CheckBox mTridayButton;
    private CheckBox mSaturdayButton;
    private CheckBox mSundayButton;
    private boolean[] selectedDays;
    CheckBox[] weekDays = { mMondayButton, mTuesdayButton, mWednesdayButton, mThursdayButton, mTridayButton,
            mSaturdayButton, mSundayButton };
    private List<WeekBean> mWeekBeans = new ArrayList<>();
    private SPSaveList mSpSaveList;

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public AutoStartupRepeatPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * @param context
     * @param attrs
     */
    public AutoStartupRepeatPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * @param context
     */
    public AutoStartupRepeatPreference(Context context) {
        super(context);
        init();
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);

        mMondayButton = (CheckBox) view.findViewById(R.id.monday);
        mTuesdayButton = (CheckBox) view.findViewById(R.id.tuesday);
        mWednesdayButton = (CheckBox) view.findViewById(R.id.wednesday);
        mThursdayButton = (CheckBox) view.findViewById(R.id.thursday);
        mTridayButton = (CheckBox) view.findViewById(R.id.friday);
        mSaturdayButton = (CheckBox) view.findViewById(R.id.saturday);
        mSundayButton = (CheckBox) view.findViewById(R.id.sunday);

        weekDays[0] = mMondayButton;
        weekDays[1] = mTuesdayButton;
        weekDays[2] = mWednesdayButton;
        weekDays[3] = mThursdayButton;
        weekDays[4] = mTridayButton;
        weekDays[5] = mSaturdayButton;
        weekDays[6] = mSundayButton;
        mSpSaveList = new SPSaveList(getContext(), Constants.MO_SETTING_SP_WEATHER_FESTIVAL);
        if (null!=mSpSaveList.getWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY)) {
            mWeekBeans = mSpSaveList.getWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY);
            setCheckBox(mWeekBeans);
        }else {
            WeekBean weekBean = new WeekBean();
            mWeekBeans.add(weekBean);
            mSpSaveList.putWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY,mWeekBeans);
            mWeekBeans = mSpSaveList.getWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY);
            setCheckBox(mWeekBeans);
        }


        mMondayButton.setOnClickListener(this);
        mTuesdayButton.setOnClickListener(this);
        mWednesdayButton.setOnClickListener(this);
        mThursdayButton.setOnClickListener(this);
        mTridayButton.setOnClickListener(this);
        mSaturdayButton.setOnClickListener(this);
        mSundayButton.setOnClickListener(this);
    }

    private void setCheckBox(List<WeekBean> weekBeans) {
        mMondayButton.setChecked(mWeekBeans.get(0).isMonday());
        mTuesdayButton.setChecked(mWeekBeans.get(0).isTuesday());
        mWednesdayButton.setChecked(mWeekBeans.get(0).isWednesday());
        mThursdayButton.setChecked(mWeekBeans.get(0).isThursday());
        mTridayButton.setChecked(mWeekBeans.get(0).isFriday());
        mSaturdayButton.setChecked(mWeekBeans.get(0).isSaturday());
        mSundayButton.setChecked(mWeekBeans.get(0).isSunday());
    }

    private void init() {
        setLayoutResource(R.layout.preference_auto_shutdown_startup_repeat);
    }

    @Override
    public void onClick(View v) {

        if (null!=mWeekBeans) {
            mWeekBeans.clear();
        }
        WeekBean weekBean = new WeekBean();
        weekBean.setMonday(weekDays[0].isChecked());
        weekBean.setTuesday(weekDays[1].isChecked());
        weekBean.setWednesday(weekDays[2].isChecked());
        weekBean.setThursday(weekDays[3].isChecked());
        weekBean.setFriday(weekDays[4].isChecked());
        weekBean.setSaturday(weekDays[5].isChecked());
        weekBean.setSunday(weekDays[6].isChecked());
        mWeekBeans.add(weekBean);
        mSpSaveList.putWeekBean(Constants.MO_SETTING_SP_WEATHER_WEEK_KEY,mWeekBeans);

    }
}
