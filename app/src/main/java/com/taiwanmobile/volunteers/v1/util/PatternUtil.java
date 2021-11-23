package com.taiwanmobile.volunteers.v1.util;

import android.util.Log;

public class PatternUtil {
    public static boolean checkPwd(String pwd) {
        String regExp = "^(?![A-Za-z]+$)(?![0-9]+$)[a-zA-Z0-9]{7,12}$";

        boolean result = pwd.matches(regExp);
        Log.d("CheckPwd", "CheckPwd:"+pwd + "->"+result);
        return result;
    }

}
