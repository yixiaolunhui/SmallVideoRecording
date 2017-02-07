package com.dalong.recordlib;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

/**
 * 视频播放
 */
public class VideoPlayFragment extends Fragment {
    public static final String TAG = VideoPlayFragment.class.getSimpleName();
    private VideoView videoView;

    private String videoPath;

    @SuppressLint("ValidFragment")
    public VideoPlayFragment(String videoPath) {
        this.videoPath = videoPath;
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
        videoView.setVideoURI(Uri.parse(videoPath));
//        MediaController mediaController = new MediaController(getActivity());
//        videoView.setMediaController(mediaController);
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
                videoView.setVideoPath(videoPath);
                videoView.start();

            }
        });

    }

}
