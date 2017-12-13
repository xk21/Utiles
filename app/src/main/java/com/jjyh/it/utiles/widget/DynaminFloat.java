package com.jjyh.it.utiles.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.jjyh.it.utiles.MainActivity;
import com.jjyh.it.utiles.R;
import com.jjyh.it.utiles.utils.BitmapUtils;
import com.jjyh.it.utiles.utils.LogUtils;
import com.jjyh.it.utiles.utils.NetWorkUtil;
import com.jjyh.it.utiles.utils.SharedPrefsUtils;


/**
 * Created by zhangjunjie on 2017/8/15.
 */
//滑动的控件
public class DynaminFloat extends LinearLayout implements View.OnClickListener {

    private final String TAG = "DFloat";

    private static final int FLOAT_APPICON = 10;
    private static final int INT_APPDOWNLOADPROGRESS = FLOAT_APPICON + 1;
    private static final int INT_APPDOWNLOADSPEED = INT_APPDOWNLOADPROGRESS + 1;
    private static final int INT_APPDOWNLOADSTATE = INT_APPDOWNLOADSPEED + 1;
    private static final int FLOAT_HIDE_SELF = INT_APPDOWNLOADSTATE + 1;
    private static final int INT_APPDOWNLOADINFO = FLOAT_HIDE_SELF + 1;

    private Activity mActivity;
    private ImageView iv_appIcon;
    private RelativeLayout noWifi;
    private RelativeLayout tv_appbar;
    private TextView tv_progress;
    private TextView tv_speed;
    private MainActivity mainContext;
    private String mAppName;
    private String appPackageName;
    private String mCurrentProgress;
    private TextView tv_download;
 /**
     * 正常状态
     */
    public static final int NORMAL = 0;
    /**
     * 滑到右侧
     */
    public static final int RIGHT = 1;
    /**
     * 滑到右侧
     */
    public static final int LEFT = 2;

    //滑动组件
    private Scroller mScroller;
    //数度跟踪者
    private VelocityTracker mVelocityTracker;

    //最后一个动作的位置
    private float mLastTouchX, mLastTouchY;
    //能被拖动的临界值
    private int mTouchSlop;
    //滑动的最大速度
    private int mMaximumVelocity;
    private float angleLastX, angleLastY;
    //拖动锁
    private boolean mDragging = false;
    /**
     * 当前状态
     */
    private int status = NORMAL;

    MoClockFloat mClockFloat = null;
    MoCalendarFloat mCalendarFloat = null;
    MoMusicFloat mMusicFloat = null;

    public DynaminFloat(Context context) {
        this(context, null);
    }

    public DynaminFloat(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public DynaminFloat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mActivity = (Activity) context;
        //处理滑动事件
        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        //获取系统触摸的临界常量值
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private void toLeft() {
        status = LEFT;
        mScroller.startScroll(getScrollX(), 0, -getScrollX() + getWidth(), 0, 500);
        invalidate();
        mHandler.sendEmptyMessageDelayed(FLOAT_HIDE_SELF,1000);
    }

    /**
     * 初始化滚动和开始绘制
     */
    private void toRight() {
        status = RIGHT;
        mScroller.startScroll(getScrollX(), 0, -(getScrollX() + getWidth()), 0, 500);
        invalidate();
    }

    private void toNormal() {
        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
        invalidate();
        status = NORMAL;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getTargetView(this, (int) ev.getRawX(), (int) ev.getRawY());
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 监听向子布局传递的触摸事件和拦截事件
     * 如果子布局是交互式的（如button），将仍然能接收到触摸事件
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtils.d(String.format("onInterceptTouchEvent action = %d, x = %f, y = %f", ev.getAction(), ev.getX(), ev.getY()));
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //判断是否已经完成滚动，如果滚动则停止
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                //重置速度跟踪器
                mVelocityTracker.clear();
                mVelocityTracker.addMovement(ev);

                //保存初始化触摸位置
                mLastTouchX = ev.getX();
                mLastTouchY = ev.getY();
                angleLastX = ev.getX();
                angleLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float x = ev.getX();
                final float y = ev.getY();
                final int xDiff = (int) Math.abs(x - mLastTouchX);
                final int yDiff = (int) Math.abs(y - mLastTouchY);
                LogUtils.d("onInterceptTouchEvent xDiff = " + xDiff);
                LogUtils.d("onInterceptTouchEvent yDiff = " + yDiff);
                //计算角度
                double angle = Math.atan2(Math.abs(ev.getY() - angleLastY), Math.abs(ev.getX() - angleLastX)) * 180 / Math.PI;
                //验证移动距离是否足够成为触发拖动事件
                if (xDiff > mTouchSlop || yDiff > mTouchSlop) {
                    if (angle >= 30) {
                        return super.onInterceptTouchEvent(ev);
                    }

                    mDragging = true;
                    mVelocityTracker.addMovement(ev);
                    LogUtils.d("onInterceptTouchEvent 获取这个动作事件");
                    //获取这个事件
                    return true;
                }

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                mVelocityTracker.clear();
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 处理接收的事件（事件由onInterceptTouchEvent获取）
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d(String.format("onTouchEvent action = %d, x = %f, y = %f", event.getAction(), event.getX(), event.getY()));
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取后续事件
                return true;
            case MotionEvent.ACTION_MOVE:
                move(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                mDragging = false;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_UP:
                mDragging = false;
                //计算当前的速度，如果速度大于最小数度临界值则开启一个滑动
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityX = (int) mVelocityTracker.getXVelocity();
                int velocityY = (int) mVelocityTracker.getYVelocity();
                {
                    if ((Math.abs(velocityX)>1000 && status == NORMAL) && (getScrollX()>=getWidth() / 3)) {
                        toLeft();
                        break;
                    }
                    toNormal();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 处理移动事件
     */
    private void move(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        //水平滚动距离
        float diffX = mLastTouchX - x;
        //垂直方向滑动的距离
        float diffY = mLastTouchY - y;

        //如果可以拖动是否被锁，x与y移动的距离大于可移动的距离
        LogUtils.d("onTouchEvent mDragging = " + mDragging);
        if (!mDragging && (Math.abs(diffX) > mTouchSlop || Math.abs(diffY) > mTouchSlop)) {
            mDragging = true;
        }

        //计算角度
        double angle = Math.toDegrees(Math.atan2(Math.abs(y - angleLastY), Math.abs(x - angleLastX)));
        LogUtils.d("onTouchEvent angle = " + angle);
        if (mDragging) {
            //滑动这个view
            if (angle < 30) {
                scrollBy((int) diffX, 0);
                mLastTouchX = x;
            }
        }
    }

    /**
     * 根据触摸到文字获得具体的子view
     */
    private View getTargetView(View view, int x, int y) {
        View target = null;
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0, len = viewGroup.getChildCount(); i < len; i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof RecyclerView) {
                target = isTouchPointInView(child, x, y) ? child : null;
                if (target != null) {
                    break;
                }
            } else if (child instanceof ViewGroup) {
                View v = getTargetView(child, x, y);
                if (v != null) {
                    return v;
                }
            }
        }
        return target;
    }


    /**
     * 计算(x, y)坐标是否在child view的范围内
     *
     * @param child 子布局
     * @param x     x坐标
     * @param y     y坐标
     * @return 子布局是否在点击范围内
     */
    private boolean isTouchPointInView(View child, int x, int y) {
        int[] location = new int[2];
        child.getLocationOnScreen(location);
        int top = location[1];
        int left = location[0];
        int right = left + child.getMeasuredWidth();
        int bottom = top + child.getMeasuredHeight();
        return y >= top && y <= bottom && x >= left && x <= right;
    }

    private int getStatus() {
        return status;
    }

    public void removeViews() {
        removeAllViews();
    }

    public void loadAlarmBar(String clockInfo) {
        removeViews();
        View view = mActivity.getLayoutInflater().inflate(R.layout.mo_clock_bar, null, false);
        if ( null == view || TextUtils.isEmpty(clockInfo) ) {
            return;
        }

        if (null == mClockFloat) {
            mClockFloat = new MoClockFloat();
        }

        mClockFloat.showClockFloat(clockInfo, view, true);

        this.addView(view);
        showDynamicFloat();
        playAnimation(true);
    }

    public void loadCalendarBar(MoCalendarBean bean) {
        removeViews();
        View view = mActivity.getLayoutInflater().inflate(R.layout.mo_calendar_item, null, false);

        if (null == mCalendarFloat) {
            mCalendarFloat = new MoCalendarFloat();
        }
        mCalendarFloat.showCalendarFloat(bean.toString(), view, true);

        this.addView(view);
        showDynamicFloat();
        playAnimation(true);
    }

    public void refreshClockBar(MoClockBean bean) {
        if (null == bean || null == mClockFloat) {
            return;
        }

        mClockFloat.refreshSingleFloat(bean);
    }

    public void refreshCalendarBar(MoCalendarBean bean) {
        if (null == bean || null == mCalendarFloat) {
            return;
        }

        mCalendarFloat.refreshSingleFloat(bean);
    }

    public void refreshMusicBar(String musicInfo, boolean musicSwitchFlag) {
        if ( null == mMusicFloat || TextUtils.isEmpty(musicInfo) ) {
            return;
        }

        View view = mActivity.getLayoutInflater().inflate(R.layout.mo_music_item, null, false);
        if ( null == view ) {
            return;
        }

        mMusicFloat.refreshSingleMusicBar(musicInfo, view, musicSwitchFlag);

    }

    public void refreshMusicBar(String elapsed) {
        mMusicFloat.refreshSingleMusicElapsed(elapsed);
    }

    public void loadMusicBar(String musicInfo) {
        removeViews();
        View view = mActivity.getLayoutInflater().inflate(R.layout.mo_music_item, null, false);
        if ( null == view ) {
            return;
        }

        if ( null == mMusicFloat ) {
            mMusicFloat = new MoMusicFloat();
        }

        mMusicFloat.showMusicFloat(musicInfo, view, true);

        this.addView(view);
        showDynamicFloat();
        playAnimation(true);
    }

    //pengbin
    /*
    * @StartDownload : 初始化下载，从多列表切换到单列表时，处理安装状态即可
     */
    public void loadAppDownloadBar(String appID, String appName, String iconUrl, String size, boolean StartDownload) {
        mainContext = (MainActivity) mActivity;
        appPackageName = appID;
        mAppName = appName;
        removeViews();
        //float appSize = (float) (Math.round(((float) (Integer.parseInt(size)) / 1024) * 100)) / 100;
        View view = mActivity.getLayoutInflater().inflate(R.layout.mo_chat_downloadapp_bar, null, false);
        noWifi = (RelativeLayout) view.findViewById(R.id.tv_nowifi);
        tv_appbar = (RelativeLayout) view.findViewById(R.id.tv_appdownloadbar);
        tv_download = (TextView) view.findViewById(R.id.tv_begin_download);
        tv_progress = (TextView) view.findViewById(R.id.tv_downloader_progess);
        tv_speed = (TextView) view.findViewById(R.id.tv_downloadspeed);
        ((TextView) view.findViewById(R.id.tv_appname)).setText(appName);
        ((TextView) view.findViewById(R.id.tv_appsize)).setText(size);

        iv_appIcon = (ImageView) view.findViewById(R.id.iv_appicon);

        BitmapUtils.getInstance().display(iv_appIcon, iconUrl);
        tv_progress.setOnClickListener(this);
        tv_appbar.setOnClickListener(this);
        tv_download.setOnClickListener(this);

        /*view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*String detail_url = "alimarket://details?id=" + appPackageName + "&from_out=ivvi";
                Uri uri = Uri.parse(detail_url);
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                mActivity.startActivity(it);*//*
            }
        });*/

        this.addView(view);
        showDynamicFloat();
        playAnimation(true);

        if (!NetWorkUtil.isWifiOpened(mActivity)) {
            tv_appbar.setClickable(false);
            tv_progress.setClickable(false);
            noWifi.setVisibility(View.VISIBLE);
            noWifi.setBackgroundColor(Color.parseColor("#B2000000"));
            return;
        }
    }

    /*
    根据showorhide 播放特定动画
    // */
    public void playAnimation(boolean showorhide) {
        /*Animation anim;
        if (showorhide) {
            anim = AnimationUtils.loadAnimation(mActivity, R.anim.appear_bottom_right_in);
        } else {
            anim = AnimationUtils.loadAnimation(mActivity, R.anim.disappear_top_left_out);
        }
        this.startAnimation(anim);*/
        toNormal();
    }

    public void showDynamicFloat() {
        if (this.getVisibility() != View.VISIBLE) {
            this.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(mActivity, R.anim.single_float_out);
            startAnimation(anim);
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            this.requestFocus();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_begin_download:
                startDownload();
                break;
            case R.id.tv_downloader_progess:
                downloadControler();
                break;
            case R.id.tv_appdownloadbar:
                openDetailView();
                break;
            default:
                break;
        }
    }

    private void openDetailView() {
        String detail_url = "alimarket://details?id=" + appPackageName + "&from_out=ivvi";
        Uri uri = Uri.parse(detail_url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        mActivity.startActivity(it);
    }

    private void downloadControler() {
        /*
        INSTALLING:安装中
        DOWNLOADING:下载中，此时广播会有。
        DOWNLOAD_PAUSED:暂停中
        WAIT_DOWNLOAD:等待下载。
        WAIT_UPDATE:可更新
        INSTALLED:已安装
        FREE:未安装
        */
        String status = mainContext.getDownloadAction().getStatus(appPackageName);
        if(status != null && (status.equals(DownloadAction.MARK_DOWNLOADING ) || status.equals(DownloadAction.MARK_WAIT_DOWNLOAD))){
            //暂停下载
            mainContext.getDownloadAction().stopDownload(appPackageName);
        }else if(status != null && status.equals(DownloadAction.MARK_DOWNLOAD_PAUSED )){
            //继续下载
            tv_progress.setText(SharedPrefsUtils.getString(appPackageName));
            mainContext.getDownloadAction().continueDownload(appPackageName);
        }else if(status != null && status.equals(DownloadAction.MARK_INSTALLED)){
            //已经安装（打开应用）
            Intent intent = mActivity.getPackageManager().getLaunchIntentForPackage(appPackageName);
            mActivity.startActivity(intent);
        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case FLOAT_HIDE_SELF:
                    EventBus.getDefault().post(new MainEvent(MainView.FLOAT_LAYER_FOLD));
                    break;
                case INT_APPDOWNLOADINFO:
                    Bundle appInfo = message.getData();
                    updateDownloadInfo(appInfo);
                    break;
            }
            return false;
        }
    });

    private void updateDownloadInfo(Bundle appInfo) {
        final String action = appInfo.getString(DownloadAction.MARK_DOWNLOADACTION);
        final String speed = appInfo.getString(DownloadAction.MARK_DOWNLOADSPEED);
        final int progress = appInfo.getInt(DownloadAction.MARK_DOWNLOADPROGRESS);
        tv_speed.setText(speed);
        if(action.equals(DownloadAction.MARK_DOWNLOADRUNNING)){
            tv_progress.setText(progress + "%");
        }else if(action.equals(DownloadAction.MARK_DOWNLOADFAIL)){
            tv_progress.setText(R.string.app_download_stoped);
        }else if(action.equals(DownloadAction.MARK_DOWNLOADCANCEL)){
            tv_progress.setText(R.string.app_download_canceled);
        }else if(action.equals(DownloadAction.MARK_DOWNLOADINSTALLED)){
            tv_progress.setText(R.string.app_download_open);
        }else if(action.equals(DownloadAction.MARK_DOWNLOADSTART)){
            tv_progress.setText(SharedPrefsUtils.getString(appPackageName));
        }
        if(progress == 100){
            tv_progress.setText(R.string.app_download_installing);
            mainContext.getDownloadAction().regestAppInstallReceiver();
        }
    }

    public synchronized void refreshDynaminFloatOfDownloadApp(String action, String packageName, String speed, int progress) {
        if (!packageName.trim().equals(appPackageName.trim())) return;
        Message msg = Message.obtain(mHandler, INT_APPDOWNLOADINFO);
        Bundle bundle = new Bundle();
        bundle.putString(DownloadAction.MARK_DOWNLOADACTION,action);
        bundle.putString(DownloadAction.MARK_DOWNLOADSPEED,speed);
        bundle.putInt(DownloadAction.MARK_DOWNLOADPROGRESS,progress);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }

    public void startDownload(){
        tv_appbar.setClickable(true);
        tv_progress.setClickable(true);
        noWifi.setVisibility(View.GONE);
        mainContext.getDownloadAction().startDownloadApp();
    }
}
