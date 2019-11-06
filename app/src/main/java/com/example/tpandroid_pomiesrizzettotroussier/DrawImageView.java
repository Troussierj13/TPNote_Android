package com.example.tpandroid_pomiesrizzettotroussier;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class DrawImageView extends View {

    private float mScale = 1f;
    private Paint mPaintDraw;
    private ArrayList<Bitmap> mDraw = new ArrayList<>(200);
    private ScaleGestureDetector mScaleGestureDetector;
    private int mWidth;
    private int mHeight;
    private Vector2Int mBasicSizeImg;
    private int mImgWidth;
    private int mImgHeight;
    private int mMaxNbPictureOnLine;
    private int mNbPicture;
    private int mLastNbPicture;
    private Vector2Int mRangeScale;
    private ArrayList<String> mPaths;

    public class Vector2Int {
        public int x;
        public int y;

        Vector2Int(int _x, int _y) {
            x = _x;
            y = _y;
        }
    }

    public DrawImageView(Context context, int width, int height) {
        super(context);
        mBasicSizeImg = new Vector2Int(480, 320);
        mRangeScale = new Vector2Int(1, 3);
        mMaxNbPictureOnLine = 7;
        mHeight = height;
        mWidth = width;

        mNbPicture = 1+mMaxNbPictureOnLine - (int)(mScale*mMaxNbPictureOnLine/mRangeScale.y);
        mLastNbPicture = mNbPicture;
        mImgWidth = (mWidth/mNbPicture);
        mImgHeight = mImgWidth*mBasicSizeImg.y/mBasicSizeImg.x;
        mPaintDraw = new Paint(Paint.DITHER_FLAG);
        mPaths = Scan.getImagePaths(context);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        for(int i=1;i<10;i++) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            BitmapFactory.decodeFile(mPaths.get(mPaths.size()-i), options);

            int rationX = (int)Math.ceil(options.outWidth/mImgWidth);
            int rationY = (int)Math.ceil(options.outHeight/mImgHeight);

            options.inSampleSize = (rationX>rationY ? rationX : rationY);
            options.inJustDecodeBounds = false;

            Bitmap bm = BitmapFactory.decodeFile(mPaths.get(mPaths.size()-i), options);

            mDraw.add(bm);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = 0;
        int y = 0;

        for(int i=0; i<mDraw.size() ; i++) {
            Bitmap tmp = Bitmap.createScaledBitmap(mDraw.get(i), mImgWidth, mImgHeight, false);
            canvas.drawBitmap(tmp, mImgWidth*x, mImgHeight*y, mPaintDraw);
            x++;
            if(x>=mNbPicture) {
                x = 0;
                y++;
            }
        }
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
            if(mScale < (float)mRangeScale.x)
                mScale = (float)mRangeScale.x;
            else if(mScale > (float)mRangeScale.y)
                mScale = (float)mRangeScale.y;


            mNbPicture = 1 + mMaxNbPictureOnLine - (int)(mScale*mMaxNbPictureOnLine/mRangeScale.y);

            if(mNbPicture != mLastNbPicture) {
                mImgWidth = (mWidth / mNbPicture);
                mImgHeight = mImgWidth * mBasicSizeImg.y / mBasicSizeImg.x;
                invalidate();
            }

            mLastNbPicture = mNbPicture;
            return true;
        }
    }

}
