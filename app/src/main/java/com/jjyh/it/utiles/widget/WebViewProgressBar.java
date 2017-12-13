package com.jjyh.it.utiles.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.jjyh.it.utiles.R;


public class WebViewProgressBar extends View {
    /**
     * progress最大值
     **/
    private final float MAX_PROGRESS = 100f;

    /**
     * progress 1% 需要的动画时间
     **/
    private final int DURATION_PROGRESS = 10;

    /**
     * 渐隐临界值（progress大于则alpha值渐渐变小）
     **/
    private final float ALPHA_DIMINISH_VALUE = 50f;
    /**
     * 渐隐alpha最低值(0~1)
     **/
    private final float ALPHA_DIMINISH_MIN_VALUE = 0.75f;
    /**
     * 渐隐alpha最低值系数
     **/
    private final float ALPHA_DIMINISH_MIN_VALUE_FACTOR = (MAX_PROGRESS - ALPHA_DIMINISH_VALUE) / (1 - ALPHA_DIMINISH_MIN_VALUE);

    /**
     * 延时隐藏时间
     **/
    private final int GONE_RUNNABLE_DELAYED_MILLIS = 100;

    /**
     * 当前进度值
     **/
    private float currentProgress;
    /**
     * 进度目标值
     **/
    private int tagProgress;

    /**
     * 当前进度长度
     **/
    private float progressWidth;

    private Paint paint;

    private ValueAnimator animator;
    private DecelerateInterpolator decelerateInterpolator;
    private AccelerateInterpolator accelerateInterpolator;

    public WebViewProgressBar(Context context) {
        super(context);
        init();
    }

    public WebViewProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebViewProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
    }

    private void drawProgress(Canvas canvas) {
        canvas.drawRect(0, 0, progressWidth, getHeight(), paint);
    }

    private void init() {
        setBackgroundColor(Color.parseColor("#cccccc"));
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.colorAccent));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        decelerateInterpolator = new DecelerateInterpolator();
        accelerateInterpolator = new AccelerateInterpolator(2);
    }

    public void setProgress(int progress) {
        if (progress > currentProgress) {
            //设置的进度值与上一次设置的进度值不一样，重新开始动画
            if (progress != tagProgress || animator == null) {
                tagProgress = progress;
                startAnimation();
            }
        } else {
            cancelAnimation();
        }
    }

    private void startAnimation() {
        cancelAnimation();
        animator = ValueAnimator.ofFloat(currentProgress, tagProgress);
        animator.setDuration((long) ((tagProgress - currentProgress) * DURATION_PROGRESS));
        //tagProgress大于等于MAX_PROGRESS，使用减速插值器；否则使用加速插值器
        if (tagProgress >= MAX_PROGRESS) {
            animator.setInterpolator(decelerateInterpolator);
        } else {
            animator.setInterpolator(accelerateInterpolator);
        }
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();
    }

    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            currentProgress = (float) valueAnimator.getAnimatedValue();
            float temp = currentProgress / MAX_PROGRESS;
            progressWidth = (temp * getWidth());
            invalidate();
            //进度条大于等于MAX_PROGRESS后就慢慢渐隐
            if (currentProgress >= ALPHA_DIMINISH_VALUE) {
                float alphaValue = (MAX_PROGRESS - currentProgress) / ALPHA_DIMINISH_MIN_VALUE_FACTOR + ALPHA_DIMINISH_MIN_VALUE;
                setAlpha(alphaValue);
                //进度条大于等于最大值就隐藏控件
                if (currentProgress >= MAX_PROGRESS) {
                    postDelayed(goneRunnable, GONE_RUNNABLE_DELAYED_MILLIS);
                }
            }
        }
    };

    private Runnable goneRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentProgress >= MAX_PROGRESS) {
                setVisibility(GONE);
            }
        }
    };

    private void cancelAnimation() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    public void start() {
        currentProgress = 0;
        tagProgress = 0;
        progressWidth = 0;
        setVisibility(VISIBLE);
        setAlpha(1.0f);
        setProgress(tagProgress);
    }

}
