package com.dalong.recordlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by dalong on 2017/1/3.
 */

public class SizeSurfaceView extends SurfaceView{

    private boolean isUserSize = false;

    private int mVideoWidth;
    private int mVideoHeight;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    public SizeSurfaceView(Context context) {
        super(context);
    }

    public SizeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isUserSize) {
            doMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
            setCameraDistance(0.5f);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public boolean isUserSize() {
        return isUserSize;
    }

    public void setUserSize(boolean isUserSize) {
        this.isUserSize = isUserSize;
    }

    /**
     * 设置视频宽高
     * @param width
     * @param height
     */
    public void setVideoDimension(int width, int height) {
        mVideoWidth = width;
        mVideoHeight = height;
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    private void doMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = View.getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = View.getDefaultSize(mVideoHeight, heightMeasureSpec);

        if (mVideoWidth > 0 && mVideoHeight > 0) {
            int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
            float specAspectRatio = (float) widthSpecSize / (float) heightSpecSize;
            float displayAspectRatio = (float) mVideoWidth / (float) mVideoHeight;
            boolean shouldBeWider = displayAspectRatio > specAspectRatio;

            if (shouldBeWider) {
                // not high enough, fix height
                height = heightSpecSize;
                width = (int) (height * displayAspectRatio);
            } else {
                // not wide enough, fix width
                width = widthSpecSize;
                height = (int) (width / displayAspectRatio);
            }
        }
        mMeasuredWidth = width;
        mMeasuredHeight = height;
    }
}
