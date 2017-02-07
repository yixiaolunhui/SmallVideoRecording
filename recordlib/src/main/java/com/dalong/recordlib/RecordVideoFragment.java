package com.dalong.recordlib;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dalong.recordlib.view.RecordStartView;


public class RecordVideoFragment extends Fragment implements RecordVideoInterface, RecordStartView.OnRecordButtonListener, View.OnClickListener {

    private final String TAG="RecordVideoFragment";
    private SizeSurfaceView mRecordView;
    private RecordStartView mRecorderBtn;//录制按钮

    private ImageButton mFacing;//前置后置切换按钮

    private ImageButton mFlash;//闪光灯

    private RelativeLayout mBaseLayout;

    private String videoPath;
    private long maxSize;
    private int maxTime;
    private RecordVideoControl mRecordControl;
    private TextView mRecordTV;
    private ImageView mCancel;


    public RecordVideoFragment() {
    }

    @SuppressLint("ValidFragment")
    public RecordVideoFragment(String videoPath, long maxSize, int maxTime) {
        this.videoPath = videoPath;
        this.maxSize = maxSize;
        this.maxTime = maxTime;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_record_video, container, false);
        initView(view);
        return  view;
    }

    private void initView(View view) {
        mRecordView=(SizeSurfaceView)view.findViewById(R.id.recorder_view);
        mBaseLayout=(RelativeLayout)view.findViewById(R.id.activity_recorder_video);
        mRecorderBtn=(RecordStartView)view.findViewById(R.id.recorder_videobtn);
        mFacing=(ImageButton)view.findViewById(R.id.recorder_facing);
        mFlash=(ImageButton)view.findViewById(R.id.recorder_flash);
        mCancel=(ImageView)view.findViewById(R.id.recorder_cancel);
        mRecordTV=(TextView)view.findViewById(R.id.record_tv);
        mRecorderBtn.setOnRecordButtonListener(this);
        mRecordControl = new RecordVideoControl(getActivity(), videoPath, mRecordView, this);
        mRecordControl.setMaxSize(maxSize);
        mRecordControl.setMaxTime(maxTime);
        mRecorderBtn.setMaxTime(maxTime);
        mCancel.setOnClickListener(this);
        mFlash.setOnClickListener(this);
        mFacing.setOnClickListener(this);
        setupFlashMode();
    }

    @Override
    public void startRecord() {
        Log.v(TAG,"startRecord");
    }

    @Override
    public void onRecording(long recordTime) {
        Log.v(TAG,"onRecording:"+recordTime);
        if(recordTime/1000>=1){
            mRecordTV.setText(recordTime/1000+"秒");
        }
    }

    @Override
    public void onRecordFinish(String videoPath) {
        Log.v(TAG,"onRecordFinish:"+videoPath);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new VideoPlayFragment(videoPath),VideoPlayFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onRecordError() {
        Log.v(TAG,"onRecordError");
    }

    @Override
    public void onTakePhoto(byte[] data) {
        Log.v(TAG,"onTakePhoto");
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        //data  拿到图片数据显示
        Toast.makeText(getActivity(), "拍照了", Toast.LENGTH_SHORT).show();
    }

    /**
     * 开始录制
     */
    @Override
    public void onStartRecord() {
        mRecordControl.startRecording();
    }

    /**
     * 结束录制
     */
    @Override
    public void onStopRecord() {
        mRecordControl.stopRecording(true);
    }

    /**
     * 拍照
     */
    @Override
    public void onTakePhoto() {
        mRecordControl.takePhoto();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.recorder_cancel) {
            getActivity().finish();
        }else if(i == R.id.recorder_flash){
            if(mRecordControl.getCameraFacing()== android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK){
                mRecordControl.setFlashMode(RecordVideoControl.flashType==RecordVideoControl.FLASH_MODE_ON
                        ?RecordVideoControl.FLASH_MODE_OFF
                        : RecordVideoControl.FLASH_MODE_ON);
            }
            setupFlashMode();
        }else if(i == R.id.recorder_facing){
            mRecordControl.changeCamera(mFacing);
            setupFlashMode();
        }
    }
    private void setupFlashMode() {
        if (mRecordControl.getCameraFacing()== android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mFlash.setVisibility(View.GONE);
            return;
        } else {
            mFlash.setVisibility(View.VISIBLE);
        }

        final int res;
        switch (RecordVideoControl.flashType) {
            case RecordVideoControl.FLASH_MODE_ON:
                res =R.drawable.pdh;
                break;
            case RecordVideoControl.FLASH_MODE_OFF:
                res =R.drawable.pdg;
                break;
            default:
                res =R.drawable.pdg;
        }
        mFlash.setBackgroundResource(res);
    }
}
