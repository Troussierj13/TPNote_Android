package com.example.tpandroid_pomiesrizzettotroussier;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;

/**
 * @author Matthieu
 * @author Julien
 * @author Romain
 */
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

    private float start = 0; // Permet de définir le point initial du touch au scroll

    /**
     * Permet de gérer des points avec un x et y
     */
    public class Vector2Int {
        public int x;
        public int y;

        Vector2Int(int _x, int _y) {
            x = _x;
            y = _y;
        }
    }

    /**
     * Constructor
     * Initialisation des variables et appel des fonctions
     * @param context
     * @param width
     * @param height
     */
    public DrawImageView(Context context, int width, int height) {
        super(context);
        mBasicSizeImg = new Vector2Int(480, 320);
        mRangeScale = new Vector2Int(1, 3);
        mRangeScroll = new Vector2Int(0, 0);
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

        LoadBitmap(0, mPaths.size()-1);
    }

    /**
     * Permet de charger en asynchrone les photos sous forme de bitmap
     * (avec la bonne taille -> option inSampleSize())
     * @param first début de la range sur laquelle on charge les photos
     * @param last fin de la range sur laquelle on charge les photos
     */
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

    /**
     * Gestion de l'affichage des éléments à l'écran
     * @param canvas
     */
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


    /**
     * Permet de gérer les gestes effectués sur l'écran
     *      - Gestion du zoom avec le mScaleGestureDetectore
     *      - Utilisation des Motion event pour détecter le mouvement effectué
     * @param event l'evenement traduit par un geste effectué sur l'écran
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);  // Zoom

        /*
         * Gestion du scrolling
         */
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            start = event.getY();
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            mScroll += (start - event.getY())/mImgHeight;
            start = event.getY();
            if(mScroll<mRangeScroll.x)
                mScroll = mRangeScroll.x;
            else if(mScroll>mRangeScroll.y)
                mScroll = mRangeScroll.y;

            invalidate();
        }
        return true;
    }

    /**
     * Classe permettant de gérer le zoom
     */
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
                LoadBitmap(0, mPaths.size()-1);

                invalidate();
            }

            mLastNbPicture = mNbPicture;
            return true;
        }
    }

}
