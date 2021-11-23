package com.taiwanmobile.volunteers.v1.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.taiwanmobile.volunteers.R;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class WebServiceUtil {

    public static final String TAG = WebServiceUtil.class.getSimpleName();

    public static Map<String, String> getWebServiceHeaders(Context context, String contentType) {
        //WebLogTool.d(TAG, "getWebServiceHeaders");
        Map<String, String> headers = null;

        headers = getWebServiceCommonObjectHeaders(context);

        // Headers for APIs
        headers.put(WebServiceParams.Headers.CONTENT_TYPE, contentType);

        String uuid = UUID.randomUUID().toString();
        headers.put(WebServiceParams.Headers.X_REQUEST_ID, uuid);
//        WebLogTool.d(TAG, "X_REQUEST_ID:"+ uuid);

        return headers;
    }


    public static Map<String, String> getWebServiceHeaders(Context context, String contentType, String accessToken) {
        //WebLogTool.d(TAG, "getWebServiceHeaders with accessToken");
        Map<String, String> headers = getWebServiceHeaders(context, contentType);

        // Headers for APIs
        if (!TextUtils.isEmpty(accessToken)) {
            headers.put(WebServiceParams.Headers.X_ACCESS_TOKEN, accessToken);
        }
        //WebLogTool.d(TAG, "accessToken : " + accessToken);
        return headers;
    }



    private static Map<String, String> getWebServiceCommonObjectHeaders(Context context) {
        Map<String, String> headers = new HashMap<String, String>();

        // Headers for Common Object Definition
        String version = "v1";
        headers.put(WebServiceParams.Headers.VERSION, version);

        String accessKeyId = "testid";
        headers.put(WebServiceParams.Headers.ACCESS_KEY_ID, accessKeyId);

        String signatureMethod = "HMAC-SHA1";
        headers.put(WebServiceParams.Headers.SIGNATURE_METHOD, signatureMethod);

        String timeStamp = String.valueOf(System.currentTimeMillis());
        headers.put(WebServiceParams.Headers.TIME_STAMP, timeStamp);

        String signatureVersion = "1.0";
        headers.put(WebServiceParams.Headers.SIGNATURE_VERSION, signatureVersion);

        String signatureNonce = UUID.randomUUID().toString();
        headers.put(WebServiceParams.Headers.SIGNATURE_NONCE, signatureNonce);

        String signatureText = version + "\n" + accessKeyId + "\n" + signatureMethod + "\n" + timeStamp + "\n" + signatureVersion + "\n" + signatureNonce;
        String accessKeySecret = context.getResources().getString(R.string.webServiceAccessKeySecret);
        String signature = "";
        try {
            signature = hmacSha1(signatureText, accessKeySecret);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        headers.put(WebServiceParams.Headers.SIGNATURE, signature);
        Log.d(TAG, "getWebServiceCommonObjectHeaders: \nsignatureText=\n" + signatureText + " \n,signature=" + signature);
        return headers;
    }

    private static String hmacSha1(String base, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        byte[] digest = mac.doFinal(base.getBytes());
        return Base64.encodeToString(digest, Base64.NO_WRAP);
    }



//    private static Map<String, String> getHeadersForKong(Context context) {
//        Map<String, String> parameters = new HashMap<String, String>();
//        Date date = new Date();
//
//        String pattern = "E, dd MMM yyyy HH:mm:ss z";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//
//        String dateString = simpleDateFormat.format(date);
//        String signingString = "x-date: " + dateString;
//        String signature = "";
//        String authorization = "";
//        try {
//            String accessKeySecret = context.getResources().getString(R.string.webServiceAccessKeySecret_infraDB);
//            byte[] decodedKey = accessKeySecret.getBytes();
//            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "HmacSHA1");
//            Mac mac = Mac.getInstance("HmacSHA1");
//            mac.init(keySpec);
//            byte[] dataBytes = signingString.getBytes(StandardCharsets.UTF_8);
//            byte[] signatureBytes = mac.doFinal(dataBytes);
//            signature = new String(java.util.Base64.getEncoder().encode(signatureBytes), StandardCharsets.UTF_8);
//
//            String client = "admin";
//            authorization = "hmac username=\"" + client + "\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"" + signature + "\"";
//        } catch (Exception ex) {
//            return null;
//        }
//
//        parameters.put("Authorization", authorization);
//        parameters.put("x-date", dateString);
//
//        //WebLogTool.d(TAG, "getHeadersForKong: \nAuthorization=\n" + authorization + " \n,x-date=" + dateString);
//        return parameters;
//    }
}
