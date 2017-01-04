package com.dalong.recordlib;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

public class RecordVideoActivity extends AppCompatActivity {
    public final static String RECORD_VIDEO_PATH="video_path";
    public final static String RECORD_MAX_SIZE="max_size";
    public final static String RECORD_MAX_TIME="max_time";
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
}
