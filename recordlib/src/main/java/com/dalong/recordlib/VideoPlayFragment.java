package com.dalong.recordlib;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

/**
 * 视频播放
 */
public class VideoPlayFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = VideoPlayFragment.class.getSimpleName();
    public static final int FILE_TYPE_VIDEO=0;
    public static final int FILE_TYPE_PHOTO = 1;
    private VideoView videoView;

    private String filePath;
    private int fileType;
    private int direction;
    private ImageView photoPlay,videoUse,videoCancel;

    @SuppressLint("ValidFragment")
    public VideoPlayFragment(String filePath,int type) {
        this.filePath = filePath;
        this.fileType = type;
    }
    @SuppressLint("ValidFragment")
    public VideoPlayFragment(String filePath,int type,int direction) {
        this.filePath = filePath;
        this.fileType = type;
        this.direction = direction;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_video_play, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        videoView = (VideoView)view.findViewById(R.id.video_play);
        photoPlay = (ImageView)view.findViewById(R.id.photo_play);
        videoCancel = (ImageView)view.findViewById(R.id.video_cancel);
        videoUse = (ImageView)view.findViewById(R.id.video_use);
        videoCancel.setOnClickListener(this);
        videoUse.setOnClickListener(this);
        if(fileType==FILE_TYPE_VIDEO){
            videoView.setVisibility(View.VISIBLE);
            photoPlay.setVisibility(View.GONE);
            videoView.setVideoURI(Uri.parse(filePath));
            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    videoView.setVideoPath(filePath);
                    videoView.start();
                }
            });
        }else if(fileType==FILE_TYPE_PHOTO){
            videoView.setVisibility(View.GONE);
            photoPlay.setVisibility(View.VISIBLE);
            Bitmap bitmap= BitmapFactory.decodeFile(filePath);
            Matrix m = new Matrix();
            m.setRotate(direction == Camera.CameraInfo.CAMERA_FACING_FRONT?270:90, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap bm1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                photoPlay.setImageBitmap(bm1);
            } catch (OutOfMemoryError ex) {
            }
        }


    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.video_use) {
            useVideo();
        } else if (i == R.id.video_cancel) {
            onCancel();
        }
    }


    /**
     * 取消
     */
    public void onCancel(){
        ((RecordVideoActivity)getActivity()).popBackStack();
    }

    /**
     * 使用
     */
    public void useVideo(){
        RecordVideoActivity activity = (RecordVideoActivity) getActivity();
        //防止点击过快
        if (activity != null && !activity.isFinishing()) {
            if(fileType==FILE_TYPE_VIDEO)
                activity.returnVideoPath(filePath);
            else
                activity.returnPhotoPath(filePath);
        }
    }
}


