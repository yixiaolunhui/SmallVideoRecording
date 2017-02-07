package com.dalong.recordlib;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.support.annotation.ColorInt;
import android.widget.Toast;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by zwl on 16/7/7.
 */
public class RecordVideoUtils {

	private RecordVideoUtils() {};

	public static List<Size> getResolutionList(Camera camera) {
		Parameters parameters = camera.getParameters();
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
		return previewSizes;
	}

	public static boolean isSdcardExist() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static class ResolutionComparator implements Comparator<Size> {

		@Override
		public int compare(Size lhs, Size rhs) {
			if(lhs.height!=rhs.height)
				return lhs.height-rhs.height;
			else
				return lhs.width-rhs.width;
		}

	}
	public static String getDurationString(long durationMs) {
		return String.format(Locale.getDefault(), "%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(durationMs),
				TimeUnit.MILLISECONDS.toSeconds(durationMs) -
						TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMs))
		);
	}

	@ColorInt
	public static int darkenColor(@ColorInt int color) {
		float[] hsv = new float[3];
		Color.colorToHSV(color, hsv);
		hsv[2] *= 0.8f; // value component
		color = Color.HSVToColor(hsv);
		return color;
	}

	/**
	 * 获取视频第一帧图片
	 * @param path
	 * @return
	 */
	public static Bitmap getVideoFristImage(String path) {
		MediaMetadataRetriever media = new MediaMetadataRetriever();
		media.setDataSource(path);
		Bitmap bitmap = media.getFrameAtTime();
		return bitmap;
	}


	/**
	 *
	 * 解决录像时清晰度问题
	 *
	 * 视频清晰度顺序 High 1080 720 480 cif qvga gcif 详情请查看 CamcorderProfile.java
	 * 在12秒mp4格式视频大小维持在1M左右时,以下四个选择效果最佳
	 *
	 * 不同的CamcorderProfile.QUALITY_ 代表每帧画面的清晰度,
	 * 变换 profile.videoBitRate 可减少每秒钟帧数
	 *
	 * @param cameraID 前摄 Camera.CameraInfo.CAMERA_FACING_FRONT /后摄 Camera.CameraInfo.CAMERA_FACING_BACK
	 * @return
	 */
	public static CamcorderProfile getBestCamcorderProfile(int cameraID){
		CamcorderProfile profile = CamcorderProfile.get(cameraID,CamcorderProfile.QUALITY_LOW);
		if(CamcorderProfile.hasProfile(cameraID,CamcorderProfile.QUALITY_480P)){
			//对比下面720 这个选择 每帧不是很清晰
			profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_480P);
			profile.videoBitRate = profile.videoBitRate/5;
			return profile;
		}
		if(CamcorderProfile.hasProfile(cameraID,CamcorderProfile.QUALITY_720P)){
			//对比上面480 这个选择 动作大时马赛克!!
			profile = CamcorderProfile.get(cameraID,CamcorderProfile.QUALITY_720P);
			profile.videoBitRate = profile.videoBitRate/35;
			return profile;
		}
		if(CamcorderProfile.hasProfile(cameraID,CamcorderProfile.QUALITY_CIF)){
			profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_CIF);
			return profile;
		}
		if(CamcorderProfile.hasProfile(cameraID,CamcorderProfile.QUALITY_QVGA)){
			profile = CamcorderProfile.get(cameraID, CamcorderProfile.QUALITY_QVGA);
			return profile;
		}
		return profile;
	}


	/**
	 * 检测当前设备是否配置闪光灯
 	 * @param mContext
	 * @return
     */
	public boolean checkFlashlight(Context mContext) {
		if (!mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			Toast.makeText(mContext.getApplicationContext(), "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}


	/**
	 * 读取图片属性：旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
     * 旋转图片
     *
     * @param angle
     *
     * @param bitmap
     *
     * @return Bitmap
     */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

}
