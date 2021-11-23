package com.taiwanmobile.volunteers.v1.util;

public class WebServiceParams {
    public static class Headers {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String X_REQUEST_ID = "X-Request-ID";
        public static final String X_ACCESS_TOKEN = "X-Access-Token";

        public static final String VERSION = "Version";
        public static final String ACCESS_KEY_ID = "AccessKeyId";
        public static final String SIGNATURE_METHOD = "SignatureMethod";
        public static final String TIME_STAMP = "Timestamp";
        public static final String SIGNATURE_VERSION = "SignatureVersion";
        public static final String SIGNATURE_NONCE = "SignatureNonce";
        public static final String SIGNATURE = "Signature";

        public static final String USER_AGENT = "user-agent";
        public static final String PACKAGE_NAME = "PKG";
        public static final String PACKAGE_VERSION = "PVN";
        public static final String BRAND_TYPE = "BT";
        public static final String SOFTWARE_VERSION = "SW";
        public static final String LANGUAGE = "LAN";
        public static final String NETWORK = "NET";
        public static final String ANDROID_API_LEVEL = "AAL";
        public static final String SKUID = "SKU";
        public static final String MOBILE_CONUTRY = "MC";
        public static final String SECRET_DATA = "SD";

    }

    public static class CommonValues {
        public static final String Headers_Content_Type_APPLICATION_JSON = "application/json; charset=utf-8";
        public static final String Headers_Content_Type_FORM_DATA = "multipart/form-data";
        //        public static final String Body_Scope = "public_profile,picture,email,phone,password,question,address";
        public static final String Body_Scope = "public_profile,picture,email,phone";
    }

}

