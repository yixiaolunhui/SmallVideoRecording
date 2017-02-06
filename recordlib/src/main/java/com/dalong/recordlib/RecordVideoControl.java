package com.dalong.recordlib;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * 录制视频控制类
 * Created by dalong on 2017/1/3.
 */

public class RecordVideoControl implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener,
        MediaRecorder.OnErrorListener,Runnable {

    public final String TAG = RecordVideoControl.class.getSimpleName();
    public static final int FLASH_MODE_OFF = 0;
    public static final int FLASH_MODE_ON = 1;
    public static int flashType=FLASH_MODE_OFF;
    private int previewWidth = 640;//预览宽
    private int previewHeight = 480;//预览高
    private int maxTime=10000;//最大录制时间
    private long maxSize=30*1024*1024;//最大录制大小 默认30m
    public Activity mActivity;
    public String  videoPath;//保存的位置
    public SizeSurfaceView mSizeSurfaceView;
    public RecordVideoInterface mRecordVideoInterface;
    private SurfaceHolder mSurfaceHolder;
    private int mCameraId;//摄像头方向id
    private boolean isRecording;//是否录制中
    private Camera mCamera;//camera对象
    private boolean mIsPreviewing;  //是否预览
    private MediaRecorder mediaRecorder;
    private int defaultVideoFrameRate=10;    //默认的视频帧率
    private int mCountTime;//当前录制时间

    public RecordVideoControl(Activity mActivity, String videoPath, SizeSurfaceView mSizeSurfaceView, RecordVideoInterface mRecordVideoInterface) {
        this.mActivity = mActivity;
        this.videoPath = videoPath;
        this.mSizeSurfaceView = mSizeSurfaceView;
        this.mRecordVideoInterface = mRecordVideoInterface;
        this.mSizeSurfaceView.setUserSize(true);
        mSurfaceHolder=this.mSizeSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);

        //这里设置当摄像头数量大于1的时候就直接设置后摄像头  否则就是前摄像头
        if (Build.VERSION.SDK_INT > 8) {
            if (Camera.getNumberOfCameras() > 1) {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }
    }

    /**
     * 摄像头方向
     * @return
     */
    public  int getCameraFacing(){
        return mCameraId;
    }
    /**
     * 开启摄像头预览
     * @param holder
     */
    private void startCameraPreview(SurfaceHolder holder) {
        mIsPreviewing = false;
        setCameraParameter();
        mCamera.setDisplayOrientation(90);
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            destroyCamera();
            return;
        }
        mCamera.startPreview();
        mIsPreviewing = true;
        mSizeSurfaceView.setVideoDimension(previewHeight, previewWidth);
        mSizeSurfaceView.requestLayout();
    }

    /**
     * 释放 Camera
     */
    public void destroyCamera() {
        if (mCamera != null) {
            if (mIsPreviewing) {
                mCamera.stopPreview();
                mIsPreviewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.setPreviewCallbackWithBuffer(null);
            }
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 切换摄像头
     * @param v 点击切换的view 这里处理了点击事件
     */
    public void changeCamera(final View v) {
        if (v != null)
            v.setEnabled(false);
        changeCamera();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (v != null)
                    v.setEnabled(true);
            }
        }, 1000);
    }

    /**
     * 切换摄像头
     */
    @SuppressWarnings("deprecation")
    private void changeCamera() {
        if (isRecording) {
            Toast.makeText(mActivity, "录制中无法切换", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Build.VERSION.SDK_INT < 9) {
            return;
        }
        int cameraid = 0;
        if (Camera.getNumberOfCameras() > 1) {
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraid = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                cameraid = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        } else {
            cameraid = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        if (mCameraId == cameraid) {
            return;
        } else {
            mCameraId = cameraid;
        }
        destroyCamera();
        try {
            mCamera = Camera.open(mCameraId);
            if (mCamera != null) {
                startCameraPreview(mSurfaceHolder);
            }

        } catch (Exception e) {
            destroyCamera();
        }

    }

    /**
     * 设置camera 的 Parameters
     */
    private void setCameraParameter() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(previewWidth, previewHeight);
        if (Build.VERSION.SDK_INT < 9) {
            return;
        }
        List<String> supportedFocus = parameters.getSupportedFocusModes();
        boolean isHave= supportedFocus == null ? false :
                supportedFocus.indexOf(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) >= 0;
        if (isHave) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        parameters.setFlashMode(flashType==FLASH_MODE_ON ?
                Camera.Parameters.FLASH_MODE_TORCH :
                Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        try {
            mSurfaceHolder = surfaceHolder;
            if (surfaceHolder.getSurface() == null) {
                return;
            }
            if (mCamera == null) {
                if (Build.VERSION.SDK_INT < 9) {
                    mCamera = Camera.open();
                } else {
                    mCamera = Camera.open(mCameraId);
                }
            }
            if (mCamera != null)
                mCamera.stopPreview();
            mIsPreviewing = false;
            startCameraPreview(mSurfaceHolder);
            handleSurfaceChanged(mCamera);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            destroyCamera();
            releaseRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSurfaceChanged(Camera mCamera) {
        boolean hasSupportRate = false;
        List<Integer> supportedPreviewFrameRates = mCamera.getParameters()
                .getSupportedPreviewFrameRates();
        if (supportedPreviewFrameRates != null
                && supportedPreviewFrameRates.size() > 0) {
            Collections.sort(supportedPreviewFrameRates);
            for (int i = 0; i < supportedPreviewFrameRates.size(); i++) {
                int supportRate = supportedPreviewFrameRates.get(i);

                if (supportRate == 10) {
                    hasSupportRate = true;
                }
            }
            if (hasSupportRate) {
                defaultVideoFrameRate = 10;
            } else {
                defaultVideoFrameRate = supportedPreviewFrameRates.get(0);
            }

        }

        // 获取相机提供的所有分辨率
        List<Camera.Size> resolutionList = RecordVideoUtils.getResolutionList(mCamera);
        if (resolutionList != null && resolutionList.size() > 0) {
            Collections.sort(resolutionList, new RecordVideoUtils.ResolutionComparator());
            Camera.Size previewSize = null;
            boolean hasSize = false;
            // 使用 640*480 如果相机支持的话
            for (int i = 0; i < resolutionList.size(); i++) {
                Camera.Size size = resolutionList.get(i);
                Log.v(TAG, "width:" + size.width + "   height:" + size.height);
                if (size != null && size.width == 640 && size.height == 480) {
                    previewSize = size;
                    previewWidth = previewSize.width;
                    previewHeight = previewSize.height;
                    hasSize = true;
                    break;
                }
            }
            //如果相机不支持上述分辨率，使用中分辨率
            if (!hasSize) {
                int mediumResolution = resolutionList.size() / 2;
                if (mediumResolution >= resolutionList.size())
                    mediumResolution = resolutionList.size() - 1;
                previewSize = resolutionList.get(mediumResolution);
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
            }
        }
    }
    /**
     * 开始录制
     *
     * @return
     */
    public boolean startRecording() {
        isRecording = true;
        mCountTime = 0;
        releaseRecorder();
        mCamera.stopPreview();
        mCamera.unlock();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        if (mCameraId == 1) {
            mediaRecorder.setOrientationHint(270);
        } else {
            mediaRecorder.setOrientationHint(90);
        }

        try {
            mediaRecorder.setProfile(RecordVideoUtils.getBestCamcorderProfile(mCameraId));
        } catch (Exception e) {
            Log.e(TAG, "设置质量出错:" + e.getMessage());
            customMediaRecorder();
        }

        // 设置帧速率，应设置在格式和编码器设置
        if (defaultVideoFrameRate != -1) {
            mediaRecorder.setVideoFrameRate(defaultVideoFrameRate);
        }
        mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setOnErrorListener(this);
        // 设置最大录制时间
        mediaRecorder.setMaxFileSize(maxSize);
        mediaRecorder.setMaxDuration(maxTime);
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mediaRecorder.setOutputFile(videoPath);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            if (mRecordVideoInterface != null) {
                mRecordVideoInterface.startRecord();
            }
            new Thread(this).start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 自定义的设置mediaeecorder 这里设置视频质量最低  录制出来的视频体积很小 对质量不是要求不高的可以使用
     */
    public void customMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
            //设置分辨率，应设置在格式和编码器设置之后
            mediaRecorder.setVideoSize(previewWidth, previewHeight);
            mediaRecorder.setVideoEncodingBitRate(800 * 1024);
        }
    }


    /**
     * 停止录制
     */
    public void stopRecording(boolean isSucessed) {
        if (!isRecording) {
            return;
        }
        try {
            if (mediaRecorder != null && isRecording) {
                isRecording = false;
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;
                mCountTime = 0;
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
                if (isSucessed) {
                    if (mRecordVideoInterface != null) {
                        mRecordVideoInterface.onRecordFinish(videoPath);
                    }
                } else {
                    if (mRecordVideoInterface != null) {
                        mRecordVideoInterface.onRecordError();
                    }
                    updateCallBack(0);
                }

            }
        } catch (Exception e) {
            updateCallBack(0);
            Log.e(TAG, "stopRecording error:" + e.getMessage());
        }
    }

    /**
     * 设置闪光灯模式
     * @param flashType
     */
    public  void setFlashMode(int flashType) {
        this.flashType = flashType;
        String flashMode = null;
        switch (flashType) {
            case FLASH_MODE_ON:
                flashMode = Camera.Parameters.FLASH_MODE_TORCH;
                break;
            case FLASH_MODE_OFF:
                flashMode = Camera.Parameters.FLASH_MODE_OFF;
            default:
                break;
        }
        if (flashMode != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(flashMode);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 拍照
     */
    public void  takePhoto(){
        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                camera.setPreviewCallback(null);
                if (mCamera == null)
                    return;
                Camera.Parameters parameters = camera.getParameters();
                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
                byte[] bytes = out.toByteArray();
                if (mRecordVideoInterface != null) {
                    mRecordVideoInterface.onTakePhoto(bytes);
                }
                //设置这个可以达到预览的效果
//                mCamera.setPreviewCallback(this);
            }
        });
    }

    /**
     * 释放mediaRecorder
     */
    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        Log.v(TAG, "onInfo");
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.v(TAG, "最大录制时间已到");
            stopRecording(true);
        }
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int i, int i1) {
        Log.e(TAG, "recording onError:");
        Toast.makeText(mActivity, "录制失败，请重试", Toast.LENGTH_SHORT).show();
        stopRecording(false);
    }

    @Override
    public void run() {
        while (isRecording) {
            updateCallBack(mCountTime);
            try {
                mCountTime += 100;
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 回调录制时间
     * @param recordTime
     */
    private void updateCallBack(final int recordTime) {
        if (mActivity != null && !mActivity.isFinishing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mRecordVideoInterface != null) {
                        mRecordVideoInterface.onRecording(recordTime);
                    }
                }
            });
        }
    }

    /**
     * 获取最大录制时间
     * @return
     */
    public int getMaxTime() {
        return maxTime;
    }

    /**
     * 设置录制时间
     * @param maxTime
     */
    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * 获取最大录制大小
     * @return
     */
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * 设置录制大小
     * @param maxSize
     */
    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    /**
     * 获取录制视频保存路径
     * @return
     */
    public String getVideoPath() {
        return videoPath;
    }

    /**
     * 设置录制保存路径
     * @param videoPath
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    /**
     * 是否录制
     * @return
     */
    public boolean isRecording() {
        return isRecording;
    }

}
