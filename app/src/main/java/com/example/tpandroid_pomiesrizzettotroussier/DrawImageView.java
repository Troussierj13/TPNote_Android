package com.example.tpandroid_pomiesrizzettotroussier;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;


public class DrawImageView extends View {

    private float mScale = 1f;
    private Paint mPaint;
    private Paint mPaintDraw;
    private float mFontSize;
    private Bitmap mDraw;

    private ScaleGestureDetector mScaleGestureDetector;


    public DrawImageView(Context context) {
        super(context);
        mFontSize = 16 * getResources().getDisplayMetrics().density;
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(mFontSize);

        mPaintDraw = new Paint(Paint.DITHER_FLAG);

        mDraw = BitmapFactory.decodeResource(getResources(), R.drawable.img);

        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawText("texte",10, 20, mPaint);
        //canvas.drawText("texte",10, 60, mPaint);

        Bitmap tmp = Bitmap.createScaledBitmap(mDraw, (int) (480f * mScale), (int) (320f * mScale), false);
        canvas.drawBitmap(tmp, 0, 0, mPaintDraw);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }


    public class ScaleGesture extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScale *= detector.getScaleFactor();
            mPaint.setTextSize(mScale*mFontSize);
            invalidate();
            return true;
        }
    }

}
