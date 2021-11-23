/**
 * FIH add this utils
 */
package com.taiwanmobile.volunteers.v2.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import android.app.Fragment;
import androidx.loader.content.CursorLoader;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URI;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
	private static final String TAG = "CommonUtils";
	/**
	 * ���ĨҤl�GA123456789
	 * �b�u�ˬd���Ghttp://120.105.184.250/peiyuli/lesson-40.htm
	 * �W�h�ѦҡGhttps://web.fg.tp.edu.tw/~anny/idtest.htm
	 *          https://www.cs.pu.edu.tw/~tsay/course/objprog/hw/hw4.html
	 *
	 * �N�X�ѦҡGhttp://www2.lssh.tp.edu.tw/~hlf/class-1/lang-c/id/index.htm
	 */

	public static boolean isValidUIDNumber(String uid) {//ex. A123456789
		//String reg = "^[A-KM-QT-XZ]{1}[0-9]{9}$";
		String reg = "^[A-Z][0-9]{9}$";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(uid);
		if(!matcher.matches()) {//�򥻪��r���Ʀr��Ƥ������A�h�L��
			Log.e(TAG,"isValidUIDNumber() not follow basic ruler");
			return false;
		}
		if(uid.charAt(1) == '1' || uid.charAt(1) == '2') {
			//�k�Τk ok
		} else {
			Log.e(TAG,"isValidUIDNumber() not right gender, should only be 1 or 2");
			return false;
		}

		int letter = charToInt(uid.charAt(0));
		int total=(letter%10)*9+letter/10
				+(uid.charAt(1)-'0')*8+(uid.charAt(2)-'0')*7+(uid.charAt(3)-'0')*6+(uid.charAt(4)-'0')*5+(uid.charAt(5)-'0')*4+(uid.charAt(6)-'0')*3+(uid.charAt(7)-'0')*2+(uid.charAt(8)-'0')*1;
		if((10-total%10) ==(uid.charAt(9)-'0')) {
			return true;
		} else {
			return false;
		}
	}

	private static int charToInt(char c) {
		switch(c)
		{
			case 'A': return 10 ;   case 'P': return 23;
			case 'B': return 11 ;   case 'Q': return 24;
			case 'C': return 12 ;  case 'R': return 25;
			case 'D': return 13 ;  case 'S': return 26;
			case 'E': return 14 ;   case 'T': return 27;
			case 'F': return 15 ;   case 'U': return 28;
			case 'G': return 16 ;   case 'V': return 29;
			case 'H': return 17 ;   case 'W': return 32;
			case 'I': return 34 ;   case 'X': return 30;
			case 'J': return 18 ;   case 'Y': return 31;
			case 'K': return 19 ;   case 'Z': return 33;
			case 'L': return 20 ;
			case 'M': return 21 ;
			case 'N': return 22 ;
			case 'O': return 35 ;
		}
		return -1;
	}


	public static void startQRScanner(Fragment fragment){
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

		try{
			fragment.startActivityForResult(intent, 0);
		}catch (ActivityNotFoundException e){
			launchAppDetail(fragment, "com.google.zxing.client.android", "com.android.vending");
		}
	}

	public static void launchAppDetail(Fragment fragment, String appPkg, String marketPkg) {
		try {
			if (TextUtils.isEmpty(appPkg)) return;

			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!TextUtils.isEmpty(marketPkg)) {
				intent.setPackage(marketPkg);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			fragment.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void launchAppDetail(Context context, String appPkg, String marketPkg) {
		try {
			if (TextUtils.isEmpty(appPkg)) return;

			Uri uri = Uri.parse("market://details?id=" + appPkg);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			if (!TextUtils.isEmpty(marketPkg)) {
				intent.setPackage(marketPkg);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
