package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/**
 * @author Vladimir Makeev
 * @see http://developer.android.com/guide/topics/media/camera.html#saving-media
 * @see http://developer.android.com/guide/topics/data/data-storage.html
 * @see http 
 *      ://stackoverflow.com/questions/4989010/android-samsung-camera-app-wont
 *      -return-intent-getdata
 */
public class StorageUtil {

	private static final String TAG = StorageUtil.class.getSimpleName();

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static boolean checkExternalStorage() {
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String externalStorageState = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(Context context, int type) {
		return Uri.fromFile(getOutputMediaFile(context, type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(Context context, int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = context.getExternalFilesDir(null);

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir, "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir, "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}
		try {
			mediaFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mediaFile;
	}

	public static Bitmap cropImageBitmap(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap newBitmap;

		if (width > height) {
			// landscape, crop by height
			newBitmap = ThumbnailUtils.extractThumbnail(bitmap, height, height);
			// newBitmap = Bitmap.createBitmap(bitmap, width / 2 - height / 2,
			// 0, height, height);
		} else if (height > width) {
			// portrait, crop by width
			newBitmap = ThumbnailUtils.extractThumbnail(bitmap, width, width);
			// newBitmap = Bitmap.createBitmap(bitmap, 0, height / 2 - width /
			// 2, width, width);
		} else {
			newBitmap = bitmap;
		}

		return newBitmap;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int newSize, Context context) {
		int width_tmp = bitmap.getWidth();
		int height_tmp = bitmap.getHeight();
		float scale = 1;

		if (width_tmp > height_tmp) {
			if (width_tmp > newSize && newSize != 0)
				scale = (float) width_tmp / newSize;
		} else {
			if (height_tmp > newSize && newSize != 0)
				scale = (float) height_tmp / newSize;
		}

		return Bitmap.createScaledBitmap(bitmap, (int) (width_tmp / scale), (int) (height_tmp / scale), false);
	}

	public static Bitmap scaleBitmap(File file, int newSize, Context context) {
		if (file == null) {
			return null;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null, options);

			int width_tmp = options.outWidth;
			int height_tmp = options.outHeight;
			float scale = 1;

			if (width_tmp > height_tmp) {
				if (width_tmp > newSize && newSize != 0)
					scale = (float) width_tmp / newSize;
			} else {
				if (height_tmp > newSize && newSize != 0)
					scale = (float) height_tmp / newSize;
			}

			BitmapFactory.Options realOptions = new BitmapFactory.Options();
			realOptions.inSampleSize = Math.round(scale);
			return BitmapFactory.decodeStream(new FileInputStream(file), null, realOptions);

		} catch (FileNotFoundException e) {
			
		}
		return null;
	}

	public static File scaleFile(File file, int newSize, int quality, Context context) {
		try {
			Bitmap scaleBitmap = scaleBitmap(file, newSize, context);
			if (scaleBitmap == null)
				return null;

			File mediaStorageDir = context.getExternalFilesDir(null);
			File tempFile = new File(mediaStorageDir, "s_" + file.getName());
			scaleBitmap.compress(CompressFormat.JPEG, quality, new FileOutputStream(tempFile));

			return tempFile;

		} catch (FileNotFoundException e) {
			
		}
		return null;
	}

	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
		// can post image

		String[] proj = { MediaStore.Images.Media.DATA };
		String result = null;

		if (activity != null && proj != null) {
			Cursor cursor = activity.managedQuery(contentUri, proj, // Which
																	// columns
																	// to return
					null, // WHERE clause; which rows to return (all rows)
					null, // WHERE clause selection arguments (none)
					null); // Order-by clause (ascending by name)

			try {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				result = cursor.getString(column_index);
			} catch (Exception e) {
				
			}
		}

		return result;
	}

	public static File getFileFromUri(Uri uri) {
		return uri != null ? new File(uri.getPath()) : null;
	}

	// blur effect
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

		// Stack Blur v1.0 from
		// http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
		//
		// Java Author: Mario Klingemann <mario at quasimondo.com>
		// http://incubator.quasimondo.com
		// created Feburary 29, 2004
		// Android port : Yahel Bouaziz <yahel at kayenko.com>
		// http://www.kayenko.com
		// ported april 5th, 2012

		// This is a compromise between Gaussian Blur and Box blur
		// It creates much better looking blurs than Box Blur, but is
		// 7x faster than my Gaussian Blur implementation.
		//
		// I called it Stack Blur because this describes best how this
		// filter works internally: it creates a kind of moving stack
		// of colors whilst scanning through the image. Thereby it
		// just has to add one new block of color to the right side
		// of the stack and remove the leftmost color. The remaining
		// colors on the topmost layer of the stack are either added on
		// or reduced by one, depending on if they are on the right or
		// on the left side of the stack.
		//
		// If you are using this algorithm in your code please add
		// the following line:
		//
		// Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		Log.e("pix", w + " " + h + " " + pix.length);
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		Log.e("pix", w + " " + h + " " + pix.length);
		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}
}
