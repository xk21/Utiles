package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.ivvi.moassistant.R;

/**
 * Created by zhangjunjie on 2017/8/25.
 */

public class GradientDraw {
    public final static long  FRAME_DURATION   = 1000 / 60;
    private final static float OFFSET_PER_FRAME = 0.01f;
    private final float mProgressiveStartSpeed = 1.2f;
    /**分段颜色*/
    private int[] SECTION_COLORS;
    private int[] mLinearGradientColors;
    /**画笔*/
    private Paint mPaint;
    private int mWidth,mHeight;

    private int mColorIndex = 0;
    private float[]      mLinearGradientPositions;
    private float mCurrentOffset;
    private boolean      mNewTurn;
    private final int mSectionsCount = 3;
    private Interpolator mInterpolator;
    private float        mMaxOffset;

    public GradientDraw(Context context,View view,int w,int h) {
        initView(context, w, h);

    }
    private void initView(Context context,int w,int h) {
        SECTION_COLORS = new int[4];
        SECTION_COLORS[0]=context.getResources().getColor(R.color.download_dark);
        SECTION_COLORS[1]=context.getResources().getColor(R.color.download_middle);
        SECTION_COLORS[2]=context.getResources().getColor(R.color.download_light);
        SECTION_COLORS[3]=context.getResources().getColor(R.color.download_middle);

        mLinearGradientColors = new int[mSectionsCount + 2];
        mLinearGradientPositions = new float[mSectionsCount + 2];
        mInterpolator = new DecelerateInterpolator();

        mMaxOffset = 1f / mSectionsCount;
        mCurrentOffset %= mMaxOffset;
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mWidth = w;
        mHeight = h;
    }

    private int decrementColor(int colorIndex) {
        --colorIndex;
        if (colorIndex < 0) colorIndex = SECTION_COLORS.length - 1;
        return colorIndex;
    }
    public void drawGradient(Canvas canvas,int lpadding,int tpadding) {
        //new turn
        if (mNewTurn) {
            mColorIndex = decrementColor(mColorIndex);
            mNewTurn = false;
        }
        float xSectionWidth = 1f / 3;
        int currentIndexColor = mColorIndex;

        mLinearGradientPositions[0] = 0f;
        mLinearGradientPositions[mLinearGradientPositions.length - 1] = 1f;
        int firstColorIndex = currentIndexColor - 1;
        if (firstColorIndex < 0) firstColorIndex += SECTION_COLORS.length;

        mLinearGradientColors[0] = SECTION_COLORS[firstColorIndex];
        for (int i = 0; i < mSectionsCount; i++) {
            float position = mInterpolator.getInterpolation(i * xSectionWidth + mCurrentOffset);
            mLinearGradientPositions[i + 1] = position;
            mLinearGradientColors[i + 1] = SECTION_COLORS[currentIndexColor];
            currentIndexColor = (currentIndexColor + 1) % SECTION_COLORS.length;
        }

        mLinearGradientColors[mLinearGradientColors.length - 1] = SECTION_COLORS[currentIndexColor];

        float left = 1+lpadding;
        float right = (mWidth-1+lpadding);
        float top = 1+tpadding;
        float bottom = mHeight-1+lpadding;
        LinearGradient linearGradient = new LinearGradient(left, top, right, bottom,
                mLinearGradientColors, mLinearGradientPositions,
                Shader.TileMode.CLAMP);

        mPaint.setShader(linearGradient);
        int round = 48;//mHeight/2;
        RectF rectProgressBg = new RectF(lpadding, tpadding, (mWidth+lpadding), mHeight+tpadding);
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
    }

    /**
     * 开始渐变
     */
    public void startAnimation() {
        mCurrentOffset += (OFFSET_PER_FRAME * mProgressiveStartSpeed);
        if (mCurrentOffset >= mMaxOffset) {
            mNewTurn = true;
            mCurrentOffset -= mMaxOffset;
        }
    }
}
