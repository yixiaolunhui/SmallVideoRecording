package com.dalong.smallvideorecording;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.dalong.recordlib.RecordVideoActivity;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    String videoPath;

    public static final  int TAKE_DATA=200;

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
        startActivityForResult(intent,TAKE_DATA);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case TAKE_DATA:
                if(resultCode==RecordVideoActivity.TAKE_VIDEO_CODE){
                    String videoPath=data.getStringExtra(RecordVideoActivity.TAKE_VIDEO_PATH);
                    Toast.makeText(this, "视频路径："+videoPath, Toast.LENGTH_SHORT).show();
                }else if(resultCode==RecordVideoActivity.TAKE_PHOTO_CODE){
                    String photoPath=data.getStringExtra(RecordVideoActivity.TAKE_PHOTO_PATH);
                    Toast.makeText(this, "图片路径："+photoPath, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
