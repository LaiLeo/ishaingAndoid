package com.taiwanmobile.volunteers.v1.util;

import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.taiwanmobile.volunteers.v2.BackendContract;


public class FihWebViewClient extends WebViewClient {




    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//        return super.shouldOverrideUrlLoading(view, request);
//        Log.d("FihWebViewClient", "shouldOverrideUrlLoading:" + request.getUrl().toString());

        String url = request.getUrl().toString();


        if(url.startsWith(BackendContract.FIH_REDIRECT_URL)){

            if(mTWMCodeCallback != null){
                //read code
                String code = request.getUrl().getQueryParameter("code");
                Log.d("FihWebViewClient", "onTWMCodeReturn:" + BackendContract.FIH_REDIRECT_URL + ", code = " + code);
                mTWMCodeCallback.onTWMCodeReturn(code);
            }
            return true;
        }else{
            Log.d("FihWebViewClient", "loading URL:" + request.getUrl());
            return false;
        }
    }

    TWMCodeCallback mTWMCodeCallback;

    public void setTWMCodeCallback(TWMCodeCallback callback){
        this.mTWMCodeCallback = callback;
    }

    public interface TWMCodeCallback {
        void onTWMCodeReturn(String code);
    }
}
