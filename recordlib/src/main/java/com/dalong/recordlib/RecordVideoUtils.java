package com.dalong.recordlib;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaMetadataRetriever;
import android.support.annotation.ColorInt;

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
}
