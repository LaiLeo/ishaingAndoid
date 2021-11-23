package com.taiwanmobile.volunteers.v2.utils;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;

public class UriUtils {
	private static final String[] FILENAME_FROM = new String[] { ":", "/" };
	private static final String[] FILENAME_TO = new String[] { "_", "_" };

	public static String toValidFilename(URI u) {
		return StringUtils
				.replaceEach(u.toString(), FILENAME_FROM, FILENAME_TO);
	}

	public static String toValidFilename(Uri u) {

		return toValidFilename(toURI(u));

	}

	public static URI toURI(Uri u) {
		return URI.create(u.toString());
	}

	public static Uri toUri(Context c, int resId) {
		return Uri.parse(String.format(Locale.US, "android.resource://%s/%d",
				c.getPackageName(), resId));
	}

	public static File toFile(Uri u) {
		return new File(toURI(u));
	}

	public static String getRealPathFromURI(Context c, Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(c, contentUri, proj, null, null,
				null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

}
