package com.dalong.smallvideorecording;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.dalong.recordlib.RecordVideoActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    String videoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File path=new File(Environment.getExternalStorageDirectory(),
                "dalong");
        if (!path.exists()) {
            path.mkdirs();
        }
        videoPath=path.getAbsolutePath()+File.separator+System.currentTimeMillis()+".mp4";
    }

    /**
     * 录制
     * @param view
     */
    public  void  doRecording(View view){
        Intent intent=new Intent(this, RecordVideoActivity.class);
        intent.putExtra(RecordVideoActivity.RECORD_VIDEO_PATH,videoPath);
        startActivity(intent);
    }
}
