package com.example.tpandroid_pomiesrizzettotroussier;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
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
    private float mScroll = 0f;
    private Vector2Int mRangeScale;
    private ArrayList<String> mPaths;
    private Vector2Int mFirstLastLoad;
    private int mActualLoad;
    private Handler mHandler;
    private Vector2Int mRangeScroll;

    private float start = 0;


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
        mPaths = Scan.cheminImagePeriph(context);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGesture());

        int max = (int)((mHeight+(int)(mScroll*mImgHeight))/mImgHeight*mNbPicture*1.5);
        int realMax = (max>mPaths.size()-1 ? mPaths.size()-1 : max);
        mFirstLastLoad = new Vector2Int((int)(mScroll*mImgHeight), realMax);

        //mRangeScroll = new Vector2Int(0, (int)(mPaths.size()/mNbPicture-mHeight/mImgHeight*0.8));
        mRangeScroll = new Vector2Int(0, 0);

        //LoadBitmap(mFirstLastLoad.x, mFirstLastLoad.y);
        LoadBitmap(mFirstLastLoad.x, mPaths.size()-1);
    }

    private void LoadBitmap(final int first, final int last) {
        mActualLoad = first;
        mDraw.clear();

        mHandler = new Handler();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(mActualLoad < mPaths.size()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    BitmapFactory.decodeFile(mPaths.get(mActualLoad), options);

                    int rationX = (int) Math.ceil(options.outWidth / mImgWidth);
                    int rationY = (int) Math.ceil(options.outHeight / mImgHeight);

                    options.inSampleSize = (rationX > rationY ? rationX : rationY);
                    options.inJustDecodeBounds = false;

                    Bitmap bm = BitmapFactory.decodeFile(mPaths.get(mActualLoad), options);

                    mDraw.add(bm);
                    invalidate();
                    mActualLoad++;

                    if (mActualLoad <= last)
                        mHandler.post(this);
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = 0;
        int y = 0;

        int first = (int)mScroll*mNbPicture;
        first = (first<0 ? 0 : first);
        int last = (int)(first+(mHeight/mImgHeight*mNbPicture)*1.5);
        last = ((mDraw.size()-1)<last ? mDraw.size()-1 : last);

        for(int i=first; i<last ; i++) {
            Bitmap tmp = Bitmap.createScaledBitmap(mDraw.get(i), mImgWidth, mImgHeight, false);
            canvas.drawBitmap(tmp, mImgWidth*x, mImgHeight*y-mScroll*mImgHeight, mPaintDraw);
            x++;
            if(x>=mNbPicture) {
                x = 0;
                y++;
            }
        }

        int yScrollMax = y*mImgHeight-mHeight/mImgHeight;
        mRangeScroll.y = (yScrollMax<0 ? 0 : yScrollMax);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            start = event.getY();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            mScroll += (start - event.getY())/mImgHeight;
            start = event.getY();
            System.out.println(mScroll);

            if(mScroll<mRangeScroll.x)
                mScroll = mRangeScroll.x;
            else if(mScroll>mRangeScroll.y)
                mScroll = mRangeScroll.y;

            invalidate();
        }
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

                int max = (int)((mHeight+(int)(mScroll*mImgHeight))/mImgHeight*mNbPicture*1.5);
                int realMax = (max>mPaths.size()-1 ? mPaths.size()-1 : max);
                mFirstLastLoad = new Vector2Int(0, realMax);
                mScroll = 0;
                mRangeScroll = new Vector2Int(0, (int)(realMax/mNbPicture-mHeight/mImgHeight*0.8));
                LoadBitmap(mFirstLastLoad.x, mPaths.size()-1);

                invalidate();
            }

            mLastNbPicture = mNbPicture;
            return true;
        }
    }

}
