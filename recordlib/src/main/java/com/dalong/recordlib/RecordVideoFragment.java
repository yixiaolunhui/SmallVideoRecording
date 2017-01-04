package com.dalong.recordlib;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dalong.recordlib.view.RecordStartView;


public class RecordVideoFragment extends Fragment implements RecordVideoInterface, RecordStartView.OnRecordButtonListener {

    private final String TAG="RecordVideoFragment";
    private SizeSurfaceView mRecordView;
    private RecordStartView mRecorderBtn;//录制按钮

    private ImageButton mFacing;//前置后置切换按钮

    private RelativeLayout mBaseLayout;

    private String videoPath;
    private long maxSize;
    private int maxTime;
    private RecordVideoControl mRecordControl;
    private TextView mRecordTV;


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
        mRecordTV=(TextView)view.findViewById(R.id.record_tv);
        mRecorderBtn.setOnRecordButtonListener(this);
        mRecordControl = new RecordVideoControl(getActivity(), videoPath, mRecordView, this);
        mRecordControl.setMaxSize(maxSize);
        mRecordControl.setMaxTime(maxTime);
        mRecordControl.changeCamera(mFacing);
        mRecorderBtn.setMaxTime(maxTime);
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
    }

    @Override
    public void onRecordError() {
        Log.v(TAG,"onRecordError");
    }

    @Override
    public void onStartRecord() {
        mRecordControl.startRecording();
    }

    @Override
    public void onStopRecord() {
        mRecordControl.stopRecording(true);
    }
}
