package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int RIGHT_BUTTOM = 2;
    public static final int LEFT_BUTTOM = 3;

    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    private float[] rids = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};

    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置单个角的圆角半径
     * @param type
     * @param radio
     */
    public void setRadio(int type, float radio) {
        for (int i = 0;i < rids.length; i++) {
            if (i == (type*2) || i == (type*2 + 1)) {
                rids[i] = radio;
            } else {
                rids[i] = 0.0f;
            }
        }
        invalidate();
    }

    /** 
     * 画图
     * by Hankkin at:2015-08-30 21:15:53 
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(new RectF(0,0,w,h),rids,Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }
}
