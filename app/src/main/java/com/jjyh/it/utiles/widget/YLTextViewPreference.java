package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjyh.it.utiles.R;


public class YLTextViewPreference extends Preference {

    public int color;
    public int userHandle;
    Drawable mIcon = null;
    String mTitle = "";
    String mSummary = "";
    int mArrowNextImageVisiblity = -1;
    private String mSummaryBlow;

    public YLTextViewPreference(Context context) {
        this(context, null);
    }

    public YLTextViewPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YLTextViewPreference(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public YLTextViewPreference(Context context, AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.yl_textview_preference);

        /* yulong begin, add */
        /* Add arrow_next image for android6.0, lunan, 2016.01.07 */
        setWidgetLayoutResource(R.layout.yl_preference_screen_arrow_next);
        /* yulong end */
    }

    /* yulong begin, add */
    /* Add arrow_next image for android6.0, lunan, 2016.01.07 */
    @Override
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        LinearLayout layout = (LinearLayout) view.findViewById(android.R.id.widget_frame);
        if (layout != null) {
            if (mArrowNextImageVisiblity == -1) {
                layout.removeAllViews();
            } else {
                layout.setVisibility(mArrowNextImageVisiblity);
            }
        }

        view.findViewById(android.R.id.summary).setVisibility(View.VISIBLE);
        TextView summaryBlow = (TextView)view.findViewById(R.id.summary_blow);
        if(mSummaryBlow != null){
            if(!mSummaryBlow.isEmpty()) {
                summaryBlow.setVisibility(View.VISIBLE);
                summaryBlow.setText(mSummaryBlow);
            } else {
                summaryBlow.setVisibility(View.GONE);
            }
        }
    }

    public void setArrowNextImageVisiblity(int visiblity) {
        mArrowNextImageVisiblity = visiblity;
    }
    /* yulong end */
    public void setSummaryBlow(String summary) {
        mSummaryBlow = summary;
    }
}
