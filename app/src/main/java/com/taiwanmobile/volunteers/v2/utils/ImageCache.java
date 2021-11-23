package com.taiwanmobile.volunteers.v2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import com.taiwanmobile.volunteers.v2.BackendContract;

public class ImageCache {

	private static final String THUMB_PREFIX = "thumb_";
	private static final String TAG = "ImageCache";
	private static String CACHE_DIR_SUFFIX = ImageCache.class
			.getCanonicalName();

	public static File getBaseDir(Context ctx) {
		File dir = new File(ctx.getExternalFilesDir(null), CACHE_DIR_SUFFIX);
		if (!dir.exists()) {
			dir.mkdir();
		}
		if (!dir.isDirectory()) {
			throw new RuntimeException(dir.getAbsolutePath()
					+ " is not a directory");
		}
		return dir;
	}

	public interface StreamProvider {
		public InputStream getStream(Uri u) throws Exception;
	}

	public static File getCacheFile(Context c, Uri u) {
		return new File(getBaseDir(c), u.getLastPathSegment().toString());
	}

	public static Uri getCacheUri(Context c, Uri u) {
		return Uri.fromFile(getCacheFile(c, u));
	}

	public static File getThumbFile(Context c, Uri u) {
		return new File(getBaseDir(c), THUMB_PREFIX
				+ u.getLastPathSegment().toString());
	}

	public static Uri getThumbUri(Context c, Uri u) {
		return Uri.fromFile(getThumbFile(c, u));
	}

	public static Pair<Uri, Uri> getUriPair(Context c, Uri u) {
		return new Pair<Uri, Uri>(getCacheUri(c, u), getThumbUri(c, u));
	}

	public static Pair<Uri, Uri> put(final Context c, Uri u) throws Exception {
		return put(c, u, new StreamProvider() {

			@Override
			public InputStream getStream(Uri u) throws Exception {
				return c.getContentResolver().openInputStream(u);
			}
		});
	}

	public static Pair<Uri, Uri> put(Context c, Uri u, StreamProvider p)
			throws Exception {
		File cache = getCacheFile(c, u);
		Uri cacheUri = Uri.fromFile(cache);
		if (!cache.exists()) {
            InputStream in = p.getStream(u);
            OutputStream out = new FileOutputStream(cache);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
		}
		File thumb = getThumbFile(c, u);
		Uri thumbUri = Uri.fromFile(thumb);
		if (!thumb.exists()) {
			ImageUtils.createThumbnail(c, cacheUri,
					BackendContract.ATTACHMENT_TUMBNAIL_SIZE).compress(
					CompressFormat.JPEG, 60, new FileOutputStream(thumb));
		}
		debug("cached", cache, thumb);
		return new Pair<Uri, Uri>(cacheUri, thumbUri);
	}

	private static final Function<File, String> NAME_FUNC = new Function<File, String>() {
		@Override
		public String apply(File f) {
			return f.getPath();
		}
	};

	private static final boolean DEBUG = false;

	private static void debug(String msg, File... files) {
		if (!DEBUG) {
			return;
		}

		Log.e("imgcache",
				msg
						+ Joiner.on(',').join(
								Collections2.transform(Arrays.asList(files),
										NAME_FUNC)));
	}

	public static void delete(Context ctx, Uri u) {
		delete(ctx, UriUtils.toValidFilename(u));
	}

	public static void delete(Context ctx, String filename) {
		File cache, thumb;
		File baseDir = getBaseDir(ctx);
		if (filename.startsWith(THUMB_PREFIX)) {
			thumb = new File(baseDir, filename);
			cache = new File(baseDir, filename.substring(THUMB_PREFIX.length()));
		} else {
			thumb = new File(baseDir, THUMB_PREFIX + filename);
			cache = new File(baseDir, filename);
		}

		cache.delete();
		thumb.delete();

		debug("deleted: ", cache, thumb);
	}

	public static void clear(Context ctx) {
		File base = getBaseDir(ctx);
		if (!base.exists()) {
			return;
		}
		File[] files = base.listFiles();
		for (File f : files) {
			f.delete();
		}
	}

}
