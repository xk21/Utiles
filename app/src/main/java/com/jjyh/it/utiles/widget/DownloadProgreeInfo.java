package com.jjyh.it.utiles.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by zhangjunjie on 2017/8/28.
 */

public class DownloadProgreeInfo {
    private Paint mPaint;
    Rect targetRect;
    private int lpadding, tpadding;
    private int baseline;
    private int BGWidth,BGHeight;
    public DownloadProgreeInfo(int paddingl,int paddingt,int width,int height) {
        lpadding = paddingl;
        tpadding = paddingt;
        BGWidth = width;
        BGHeight = height;
        init();
    }

    private void init() {
        targetRect = new Rect(lpadding, tpadding, lpadding+BGWidth, tpadding+24);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);
        mPaint.setTextSize(40);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        baseline = tpadding+BGHeight/2+20/2;//(targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    public void drawFont(Canvas canvas,int progress) {
        String testString = progress+"%";
        canvas.drawText(testString, targetRect.centerX(), baseline, mPaint);
    }
}
