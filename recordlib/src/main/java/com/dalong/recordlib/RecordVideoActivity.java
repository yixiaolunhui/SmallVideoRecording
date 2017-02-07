package com.dalong.recordlib;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class RecordVideoActivity extends AppCompatActivity {
    public final static String RECORD_VIDEO_PATH="video_path";
    public final static String RECORD_MAX_SIZE="max_size";
    public final static String RECORD_MAX_TIME="max_time";

    public static final int TAKE_VIDEO_CODE = 1000;
    public static final int TAKE_PHOTO_CODE = 1001;

    public static final String TAKE_VIDEO_PATH = "TAKE_VIDEO_PATH";
    public static final String TAKE_PHOTO_PATH = "TAKE_PHOTO_PATH";

    private String videoPath;
    private long maxSize;
    private int maxTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            window.setStatusBarColor(RecordVideoUtils.darkenColor(0));
            window.setNavigationBarColor(0);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getIntent()!=null) {
            videoPath = getIntent().getStringExtra(RECORD_VIDEO_PATH);
            maxSize = getIntent().getLongExtra(RECORD_MAX_SIZE, 1024 * 1024 * 30L);
            maxTime = getIntent().getIntExtra(RECORD_MAX_TIME, 15000);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new RecordVideoFragment(videoPath,maxSize,maxTime))
                    .commit();
        }
    }

    /**
     * 返回上一个fragment
     */
    public void popBackStack() {
        getSupportFragmentManager().popBackStack();
    }


    /**
     * 返回视频路径
     * @param videoPath
     */
    public void returnVideoPath(String videoPath) {
        Intent data = new Intent();
        data.putExtra(TAKE_VIDEO_PATH,videoPath);
        if (getParent() == null) {
            setResult(TAKE_VIDEO_CODE, data);
        } else {
            getParent().setResult(TAKE_VIDEO_CODE, data);
        }
        finish();
    }

    /**
     * 返回图片路径
     * @param photoPath
     */
    public void returnPhotoPath(String photoPath) {
        Log.v("3333333","returnPhotoPath");
        Intent data = new Intent();
        data.putExtra(TAKE_PHOTO_PATH,photoPath);
        if (getParent() == null) {
            setResult(TAKE_PHOTO_CODE, data);
        } else {
            getParent().setResult(TAKE_PHOTO_CODE, data);
        }
        finish();
    }
}
