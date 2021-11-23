package com.taiwanmobile.volunteers.v2.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import android.util.Log;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
//import com.google.common.io.Files;
//import com.google.common.io.InputSupplier;
import com.taiwanmobile.volunteers.R;

public class ImageUtils {

	private static final String TAG = "ImageUtils";

	public static interface OnActivityResultListener {
		public void onImageCaptured();

		public void onImageSelected(Uri u);
	}

	private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 300;
	public static final String INTENT_TYPE_SELECT_IMAGE = "image/*";
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/**
	 * copy a file to internal storage space
	 * 
	 * @param c
	 * @param u
	 * @return
	 */
//	public static Uri copyToPrivateDir(Context c, Uri u) {
//		try {
//
////			IstreamSupplier iss = new IstreamSupplier(c, u);
//		/**
//		 * gallery images start with content:/ and ends with its index in
//		 * content provider instead of a file name, this means no suffix
//		 * when we convert to filename, and will cause problems when the
//		 * server processes it, so append its suffix to the end of uri
//		 * before calling toValidFilename()
//		 */
//		if ("content".equals(u.getScheme())) {
//			u = u.buildUpon().appendPath(getFileSuffix(c, u)).build();
//		}
//		File to = new File(getPrivateDir(c), UriUtils.toValidFilename(u));
//		Log.d(TAG, "not copy file");
////			Files.copy(iss, to);
//		return Uri.fromFile(to);
//	} catch (IOException e) {
//		throw new RuntimeException(e);
//	}
//	}

	private static String getFileSuffix(Context c, Uri u) {
		// TODO: actually parse returned mime stuff
		c.getContentResolver().getType(u);
		return ".jpg";
	}

	/**
	 * 
	 * wrapper class needed for guava's Files, maybe move it to utils?
	 * 
	 */
//	private static class IstreamSupplier implements InputSupplier<InputStream> {
//
//		private final InputStream mInputStream;
//
//		public IstreamSupplier(Context c, Uri u) throws FileNotFoundException {
//			this(c.getContentResolver().openInputStream(u));
//		}
//
//		public IstreamSupplier(InputStream is) {
//			mInputStream = is;
//		}
//
//		@Override
//		public InputStream getInput() throws IOException {
//			return mInputStream;
//
//		}
//
//	}

	private static File ensureExists(File dir) {
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * a private dir that is isolated from other users/processes to store
	 * images, to ensure the images are not tempered with when in queue
	 * 
	 * @param c
	 * 
	 */
	public static File getPrivateDir(Context c) {
		return ensureExists(new File(c.getFilesDir(),
				ImageUtils.class.getCanonicalName()));
	}

	/**
	 * a directory that is writable for the camera app to store the result of
	 * the capture
	 * 
	 * @param c
	 * @return
	 */
	public static File getImagesDir(Context c) {
		return ensureExists(new File(
				c.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
				ImageUtils.class.getCanonicalName()));
	}

	/**
	 * handle result for capture/select image
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @param l
	 */
	public static void onActivityResult(int requestCode, int resultCode,
			Intent data, OnActivityResultListener l) {
		// user cancelled
		if (resultCode == 0) {
			return;
		}
		switch (requestCode) {
		case SELECT_IMAGE_ACTIVITY_REQUEST_CODE:
			l.onImageSelected(data.getData());
			return;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			l.onImageCaptured();
			return;
		}
	}

	/**
	 * select image from gallery app
	 * 
	 * @param f
	 */
	public static void selectImage(Fragment f) {

		// Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		// photoPickerIntent.setType(INTENT_TYPE_SELECT_IMAGE);
		// f.startActivityForResult(photoPickerIntent,
		// SELECT_IMAGE_ACTIVITY_REQUEST_CODE);

		Intent galleryIntent = new Intent(Intent.ACTION_PICK);
		galleryIntent.setType(INTENT_TYPE_SELECT_IMAGE);
		final Intent chooserIntent = Intent.createChooser(galleryIntent,
				f.getString(R.string.image_select_intent));

		f.startActivityForResult(chooserIntent,
				SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	/**
	 * capture image from camera app and save to getImagesDir();
	 * 
	 * @param fragment
	 * @param fileUri
	 */
	public static void captureImage(Fragment fragment, Uri fileUri) {
		final List<Intent> cameraIntents = new ArrayList<Intent>();
		final Intent captureIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = fragment.getActivity()
				.getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
				captureIntent, 0);
		for (ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			cameraIntents.add(intent);
		}
		Intent chooserIntent = Intent.createChooser(cameraIntents.remove(0),
				fragment.getString(R.string.camera_select_intent));

		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				cameraIntents.toArray(new Parcelable[] {}));
		fragment.startActivityForResult(chooserIntent,
				CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	/**
	 * uniq jpeg filename
	 * 
	 * @return
	 */
	private static String uuFilename() {
		return uuFilename("jpg");
	}

	/**
	 * uniq filename with suffix
	 * 
	 * @param suffix
	 * @return
	 */
	private synchronized static String uuFilename(String suffix) {
		return UUID.randomUUID().toString() + "." + suffix;
	}

	public static Uri createCaptureUri(Context c) {
		return Uri.fromFile(new File(getImagesDir(c), uuFilename()));
	}

	public static Uri captureImage(Fragment fragment) {
		Uri ret = createCaptureUri(fragment.getActivity());
		captureImage(fragment, ret);
		return ret;
	}

	public static Uri getThumbnailUri(Context c, Uri orig) {
		return Uri.fromFile(new File(c.getCacheDir(), UriUtils
				.toValidFilename(orig)));
	}

	public static void compressToThumbnail(Bitmap bmp, OutputStream o) {
		bmp.compress(CompressFormat.JPEG, 60, o);
	}

	/**
	 * create a thumbnail image of the given uri
	 * 
	 * @param context
	 * @param uri
	 * @param thumbnailSize
	 * @return
	 */
	public static Bitmap createThumbnail(Context context, Uri uri,
			int thumbnailSize) {
		Bitmap bitmap = null;
		InputStream input;
		try {
			input = context.getContentResolver().openInputStream(uri);

			BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
			onlyBoundsOptions.inJustDecodeBounds = true;
			onlyBoundsOptions.inDither = true;// optional
			onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
			BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
			input.close();
			if ((onlyBoundsOptions.outWidth == -1)
					|| (onlyBoundsOptions.outHeight == -1))
				return null;

			int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
					: onlyBoundsOptions.outWidth;

			double ratio = (originalSize > thumbnailSize) ? (originalSize / thumbnailSize)
					: 1.0;

			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
			bitmapOptions.inDither = true;// optional
			bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional

			input = context.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
			input.close();

			// rotate the image if necessary
			File jpegFile = new File(uri.getPath());
			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
			ExifIFD0Directory exifIFD0Directory = metadata
					.getDirectory(ExifIFD0Directory.class);
			if (exifIFD0Directory != null
					&& exifIFD0Directory
							.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
				int exifOrientation = exifIFD0Directory
						.getInt(ExifIFD0Directory.TAG_ORIENTATION);

				final Matrix bitmapMatrix = new Matrix();
				switch (exifOrientation) {
				case 1:
					break; // top left
				case 2:
					bitmapMatrix.postScale(-1, 1);
					break; // top right
				case 3:
					bitmapMatrix.postRotate(180);
					break; // bottom right
				case 4:
					bitmapMatrix.postRotate(180);
					bitmapMatrix.postScale(-1, 1);
					break; // bottom left
				case 5:
					bitmapMatrix.postRotate(90);
					bitmapMatrix.postScale(-1, 1);
					break; // left top
				case 6:
					bitmapMatrix.postRotate(90);
					break; // right top
				case 7:
					bitmapMatrix.postRotate(270);
					bitmapMatrix.postScale(-1, 1);
					break; // right bottom
				case 8:
					bitmapMatrix.postRotate(270);
					break; // left bottom
				default:
					break; // Unknown
				}

				// Create new bitmap.
				final Bitmap transformedBitmap = Bitmap.createBitmap(bitmap, 0,
						0, bitmap.getWidth(), bitmap.getHeight(), bitmapMatrix,
						false);
				return transformedBitmap;
			}
		} catch (ImageProcessingException e) {
			Log.d(TAG, "image format not supported.");
		} catch (Exception e) {
			Log.e("[createThumbnail]", Log.getStackTraceString(e));
		}
		return bitmap;
	}

	// create MediaStore.ACTION_IMAGE_CAPTURE Intent
	private static Intent createCaptureImageIntent(Uri fileUri) {
		// give the image a name so we can store it in the phone's default
		// location
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		return intent;
	}

	// count power of two for sample ratio
	private static int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}
}