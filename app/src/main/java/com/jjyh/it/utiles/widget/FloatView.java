package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ivvi.moassistant.R;
import com.ivvi.moassistant.activity.MainActivity;
import com.ivvi.moassistant.global.Constants;
import com.ivvi.moassistant.service.FloatWindowService;
import com.ivvi.moassistant.utils.FloatViewUtils;

import java.lang.reflect.Field;


/**
 * Created by chenmingying on 2017/10/26.
 */

public class FloatView extends LinearLayout {

    private static final String TAG = "FloatView";
    private Context mContext;
    private View mInflate;

    private float mTouchStartX;
    private float mTouchStartY;
    private float mStartX;
    private float mStartY;
    private long mLastTime;
    private boolean isMove;
    private float mLastX;
    private float mLastY;
    private WindowManager wm;
    public WindowManager.LayoutParams wmParams;
    private long mCurrentTime;
    private float nowX;
    private float nowY;
    private int statusBarHeight;
    public ImageView floatView2,floatView1,floatView;
    public RelativeLayout mFloatView,mRemindView1,mRemindView2,mWaveView1,mWaveView2;
    public int mScreenWidth;

    public FloatView(final Context context) {
        super(context);
        this.mContext = context;
        mInflate = LayoutInflater.from(mContext).inflate(R.layout.mo_geek_float_view, this);
        initView();
        initData();
    }

    private void initView() {
        mFloatView = (RelativeLayout) mInflate.findViewById(R.id.mo_geek_float_view);
        mRemindView1 = (RelativeLayout) mInflate.findViewById(R.id.mo_geek_float_rel1);
        mRemindView2 = (RelativeLayout) mInflate.findViewById(R.id.mo_geek_float_rel2);
        mWaveView1 = (RelativeLayout) mInflate.findViewById(R.id.mo_geek_float_view_rle1);
        mWaveView2 = (RelativeLayout) mInflate.findViewById(R.id.mo_geek_float_view_rle2);
        floatView= (ImageView) mInflate.findViewById(R.id.mo_geek_float_img);
        floatView1 = (ImageView) mInflate.findViewById(R.id.mo_geek_float_img1);
        floatView2 = (ImageView) mInflate.findViewById(R.id.mo_geek_float_img2);
    }

    private void initData() {
        wm = FloatViewUtils.getWindowManager(mContext);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
//        mWaveView1.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startMain();
//            }
//        });
//        mWaveView2.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startMain();
//            }
//        });
    }

    private void startMain() {
        FloatWindowService.sGeekShopMessage = 0;
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra(Constants.MO_GEEK_FLOAT_INTENT_KEY, Constants.MO_GEEK_FLOAT_TO_ACTIVITY);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取相对屏幕的坐标，即以屏幕左上角为原点
        nowX = event.getRawX();
        nowY = event.getRawY() - getStatusBarHeight(); //statusHeight是系统状态栏的高度
        Log.d(TAG, "jjyh currX" + nowX + "====currY" + nowY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //捕获手指触摸按下动作
                //获取相对View的坐标，即以此View左上角为原点
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                mLastTime = System.currentTimeMillis();

                if (wmParams.x<(mScreenWidth/2)){
                    mRemindView1.setVisibility(View.GONE);
                    mWaveView1.setVisibility(View.GONE);
                }else {
                    mRemindView2.setVisibility(View.GONE);
                    mWaveView2.setVisibility(View.GONE);
                }
                floatView.setVisibility(View.VISIBLE);
                Log.i("startP", "startX" + mTouchStartX + "====startY" + mTouchStartY);
                isMove = false;
                break;

            case MotionEvent.ACTION_MOVE: //捕获手指触摸移动动作
                updateViewPosition();
                isMove = true;
                break;

            case MotionEvent.ACTION_UP: //捕获手指触摸离开动作
                mLastX = event.getRawX();
                mLastY = event.getRawY();

                // 抬起手指时让floatView紧贴屏幕左右边缘
                wmParams.x = wmParams.x <= (mScreenWidth / 2) ? 0 : mScreenWidth;
                //wmParams.x = 0;
                wmParams.y = (int) (nowY - mTouchStartY);
                Log.d("adsd","mParams.x 2= "+wmParams.x+ " y="+wmParams.y);
                mContext.getSharedPreferences(Constants.MO_GEEK_FLOAT_SP_GEEK, Context.MODE_PRIVATE)
                        .edit()
                        .putInt(Constants.MO_GEEK_FLOAT_SP_X, wmParams.x)
                        .putInt(Constants.MO_GEEK_FLOAT_SP_Y, wmParams.y)
                        .commit();

                wm.updateViewLayout(this, wmParams);
                if (wmParams.x<(mScreenWidth/2)){
                    mRemindView1.setVisibility(View.VISIBLE);
                    mRemindView2.setVisibility(View.GONE);
                    floatView1.setVisibility(View.VISIBLE);
                }else {
                    mRemindView2.setVisibility(View.VISIBLE);
                    mRemindView1.setVisibility(View.GONE);
                    floatView2.setVisibility(View.VISIBLE);
                }
                floatView.setVisibility(View.GONE);

                mCurrentTime = System.currentTimeMillis();
                if (mCurrentTime - mLastTime < 800) {
                    if (Math.abs(mStartX - mLastX) < 10.0 && Math.abs(mStartY - mLastY) < 10.0) {
                        startMain();
                    }
                }
                break;
        }
        return true;
    }

    private void updateViewPosition() {
        //更新浮动窗口位置参数
        wmParams.x = (int) (nowX - mTouchStartX);
        wmParams.y = (int) (nowY - mTouchStartY);
        wm.updateViewLayout(this, wmParams); //刷新显示
    }

    public void setParams(WindowManager.LayoutParams params) {
        wmParams = params;
    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
