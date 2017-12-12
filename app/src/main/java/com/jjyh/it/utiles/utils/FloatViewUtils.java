package com.jjyh.it.utiles.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.ivvi.moassistant.global.Constants;
import com.ivvi.moassistant.service.FloatWindowService;
import com.ivvi.moassistant.widget.FloatView;

import static com.baidu.turbonet.base.ThreadUtils.runOnUiThread;
import static com.ivvi.moassistant.helper.overscroll2.OverScrollBounceEffectDecoratorBase.TAG;

public class FloatViewUtils {
    private static FloatViewUtils instance;
    private Context mContext;
    private static WindowManager manager;
    private FloatView floatView;
    private WindowManager.LayoutParams mParams;
    public boolean mIsShowBigWindow;

    private FloatViewUtils(Context mContext) {
        this.mContext = mContext;
        if (manager==null) {
            manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        if (mParams==null) {
            mParams = new WindowManager.LayoutParams();
        }
    }

    public static FloatViewUtils getInstance(Context mContext) {
        if (null == instance) {
            synchronized (FloatViewUtils.class) {
                if (null == instance) {
                    instance = new FloatViewUtils(mContext);
                }
            }
        }
        return instance;
    }
    public boolean isShowWindow(){
        return floatView !=null;
    }

    public synchronized void addFloatView(int a) {

        if (floatView==null) {
            floatView = new FloatView(mContext);
        }
//        int screenHeight = manager.getDefaultDisplay().getHeight();
        //悬浮窗口大小
        mParams.width = GeekUtils.dip2px(mContext, a);
        //lp.height = floatView.HEIGHT;
        mParams.height = GeekUtils.dip2px(mContext, a);

        // 调整悬浮窗口位置
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值
        //    lp.x = 0;
        //    lp.y = 0;
        //设置悬浮窗口类型
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置悬浮窗口不接受焦点及触摸事件
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        mParams.x = mContext.getSharedPreferences(Constants.MO_GEEK_FLOAT_SP_GEEK,Context.MODE_PRIVATE)
                .getInt(Constants.MO_GEEK_FLOAT_SP_X,0);

        if (mParams.x<(floatView.mScreenWidth/2)){
            mParams.x = 0;
            floatView.mRemindView1.setVisibility(View.VISIBLE);
            floatView.mRemindView2.setVisibility(View.GONE);
        }else {
            floatView.mRemindView2.setVisibility(View.VISIBLE);
            floatView.mRemindView1.setVisibility(View.GONE);
            mParams.x = floatView.mScreenWidth;
        }
        mParams.y = mContext.getSharedPreferences(Constants.MO_GEEK_FLOAT_SP_GEEK,Context.MODE_PRIVATE)
                .getInt(Constants.MO_GEEK_FLOAT_SP_Y,0);
        try {
            manager.addView(floatView, mParams);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"add manager e="+e.getMessage());
        }
        floatView.setParams(mParams);
    }

    public static WindowManager getWindowManager(Context context) {
        return manager;
    }

    public void updateView() {
        removeWindow();
        mIsShowBigWindow = true;
        addFloatView(75);
        if (mParams.x<(floatView.mScreenWidth/2)){
            floatView.mRemindView1.setVisibility(View.VISIBLE);
            floatView.mRemindView2.setVisibility(View.GONE);
            floatView.mWaveView1.setVisibility(View.VISIBLE);
            floatView.floatView1.setVisibility(View.GONE);
        }else {
            floatView.mRemindView2.setVisibility(View.VISIBLE);
            floatView.mRemindView1.setVisibility(View.GONE);
            floatView.mWaveView2.setVisibility(View.VISIBLE);
            floatView.floatView2.setVisibility(View.GONE);
        }

        new Thread(){
            @Override
            public void run() {
                try {
                    this.sleep(5000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            removeWindow();
                            if (FloatWindowService.sGeekShopType ==1) {
                                addFloatView(40);
                            }
                            mIsShowBigWindow = false;
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void removeWindow() {
        Log.d(TAG,"jjyh removeSmallWindow " +floatView );
        if (floatView != null) {
            try {
                manager.removeView(floatView);
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"remov manager e="+e.getMessage());
            }
            floatView = null;
        }
    }
}
