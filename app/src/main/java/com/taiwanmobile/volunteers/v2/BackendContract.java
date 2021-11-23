package com.taiwanmobile.volunteers.v2;

public class BackendContract {
	public static final Boolean DEBUG = true;

	public static String DEPLOYMENT_URL;
	public static String STATIC_RESOURCE_URL_BASE = "https://stage-isharing.fihcloud.com";//"http://isharing.fihcloud.com";
	public static String STATIC_RESOURCE_URL_STATIC = STATIC_RESOURCE_URL_BASE
			+ "/resources/";

	public static String FIH_URL;


	public static final String FIH_REDIRECT_URL = "https://stage-isharing.fihcloud.com/oauth/twmcallback";
	public static final String TWM_LOGIN_URL = "https://stage.oauth.taiwanmobile.com/MemberOAuth/authPageLogin?response_type=code&client_id=fihcloud&redirect_uri=https://stage-isharing.fihcloud.com/oauth/twmcallback&state=/";



	static {
		if (DEBUG) {
//			DEPLOYMENT_URL = "http://60.199.131.49:8000";
//			DEPLOYMENT_URL = "http://www.isharing.tw:8002";
//			DEPLOYMENT_URL = "https://www.isharing.tw";//tea-test this is official
//			DEPLOYMENT_URL = "http://www.isharing.tw:8002";//tea-test this is for debug server
			DEPLOYMENT_URL = "https://stage-isharing.fihcloud.com";//"http://isharing.fihcloud.com/old";
			//DEPLOYMENT_URL = "http://10.57.52.32:8000";
			// DEPLOYMENT_URL = "http://10.0.1.7:8000";
			FIH_URL ="https://stage-isharing.fihcloud.com"; //"http://isharing.fihcloud.com";

		} else {
//			DEPLOYMENT_URL = "https://www.isharing.tw";
			DEPLOYMENT_URL = "http://isharing.fihcloud.com/old";

			FIH_URL = "http://isharing.fihcloud.com";
		}
	}

	public static final int ATTACHMENT_TUMBNAIL_SIZE = 400;

	public static final String DEPLOYMENT_STATIC_URL = DEPLOYMENT_URL
			+ "/uploads/";

	// public static final String DATABASE_URL = DEPLOYMENT_URL + "/api/dump/";
	public static final String DB_DUMP_URL = DEPLOYMENT_URL + "/api/db/dump/";

	public static final String ACCOUNT_VALIDATE_URL = DEPLOYMENT_URL
			+ "/api/user/validate";
	public static final String ACCOUNT_FORGOT_PASSWORD_URL = DEPLOYMENT_URL
			+ "/api/user/forgot_password/";
	public static final String ACCOUNT_REGISTER_URL = DEPLOYMENT_URL
			+ "/api/user/register/";
	public static final String ACCOUNT_DETAIL_URL = DEPLOYMENT_URL
			+ "/api/user/";

	public static final String NPO_SUBSCRIBE_URL = DEPLOYMENT_URL
			+ "/api/npo/subscribe/id/";
	public static final String NPO_UNSUBSCRIBE_URL = DEPLOYMENT_URL
			+ "/api/npo/unsubscribe/id/";

	public static final String EVENT_FOCUS_URL = DEPLOYMENT_URL
			+ "/api/event/focus/id/";
	public static final String EVENT_UNFOCUS_URL = DEPLOYMENT_URL
			+ "/api/event/unfocus/id/";

	public static final String EVENT_UNREGISTER_URL = DEPLOYMENT_URL
			+ "/api/event/unregister/id/";

	public static final String EVENT_JOIN_URL = DEPLOYMENT_URL
			+ "/api/event/join/id/";

	public static final String USER_DATA_UPDATE_URL = DEPLOYMENT_URL
			+ "/api/user/edit/";

	public static final String EVENT_RATING_URL = DEPLOYMENT_URL
			+ "/api/event/rating/id/";

	// for FB and GMail sharing link
	public static final String EVENT_SHARING_URL = DEPLOYMENT_URL + "/event/";

	public static final String setEventRatingURL(long eventId, float score) {
		return DEPLOYMENT_URL + "/api/event/rating/id/"
				+ String.valueOf(eventId) + "/score/" + String.valueOf(score);
	}

	// v2
	public static final String V2_LOGIN_URL = DEPLOYMENT_URL
			+ "/apiv2/login/mobile/";
	// public static final String V2_EVENT_URL = DEPLOYMENT_URL +
	// "/apiv2/events/";
	public static final String  V2_DATA_DUMP_URL = DEPLOYMENT_URL
			+ "/apiv2/dump/";

	public static final String V2_EVENT_REGISTER_URL = DEPLOYMENT_URL
			+ "/apiv2/event/register/";
	public static final String V2_EVENT_UNREGISTER_URL = DEPLOYMENT_URL
			+ "/apiv2/event/unregister/";

	public static final String V2_EVENT_FOCUS_URL = DEPLOYMENT_URL
			+ "/apiv2/event/focus/";
	public static final String V2_EVENT_UNFOCUS_URL = DEPLOYMENT_URL
			+ "/apiv2/event/unfocus/";

	public static final String V2_REPLY_URL = DEPLOYMENT_URL + "/apiv2/reply/";

	public static final String V2_EVENT_JOIN_URL = DEPLOYMENT_URL
			+ "/apiv2/event/join/";
	public static final String V2_EVENT_LEAVE_URL = DEPLOYMENT_URL
			+ "/apiv2/event/leave/";

	public static final String V2_USERPROFILE_UPDATE_URL = DEPLOYMENT_URL
			+ "/apiv2/user/profile/";
	public static final String V2_USER_LICENSE_UPDATE_URL = DEPLOYMENT_URL
			+ "/apiv2/user/license/";
	public static final String V2_ACCOUNT_REGISTER_URL = DEPLOYMENT_URL
			+ "/apiv2/user/register/";

	public static final String DEVICE_URL = DEPLOYMENT_URL
			+ "/apiv2/device/register/";
	public static final String MESSAGE_READ_URL = DEPLOYMENT_URL
			+ "/apiv2/device/read/";

	public static final String V2_NPO_SUBSCRIBE_URL = DEPLOYMENT_URL
			+ "/apiv2/npo/subscribe/";
	public static final String V2_NPO_UNSUBSCRIBE_URL = DEPLOYMENT_URL
			+ "/apiv2/npo/unsubscribe/";

	public static final String V2_TTVOLUNTEER_REGISTER_STATUS_URL = DEPLOYMENT_URL
			+ "/ttvolunteer/register/";


	public static final String V1_AUTH_TOKEN = FIH_URL
			+ "/api/v1/auth/token";

	public static final String V1_AUTH_FUBON = FIH_URL
			+ "/api/v1/auth/fubon";

	public static final String V1_AUTH_TWM = FIH_URL
			+ "/api/v1/auth/twm";

	public static final String V1_USER_PROFILE = FIH_URL
			+ "/api/v1/user/profile";

	public static final String V1_USER_REGISTER = FIH_URL
			+ "/api/v1/users";

	public static final String V1_EVENT_JOIN = FIH_URL
			+ "/api/v1/events/join";
	public static final String V1_EVENT_LEAVE = FIH_URL
			+ "/api/v1/events/leave";

	public static final String V1_USER_FUBON = FIH_URL
			+ "/api/v1/user/fubon";

	public static final String V1_USER_TWM = FIH_URL
			+ "/api/v1/user/twm";
	public static final String V1_RESET_PWD = FIH_URL
			+ "/api/v1/reset/password";
	public static final String V1_APP_CONFIGS = FIH_URL
			+ "/api/v1/appConfigs?search=+id+eq+1";

	public static final String V1_NOTIFICATION_GCM = FIH_URL
			+ "/api/v1/notifications/gcm";

	public static final String setV2EventRatingURL(long eventId, float score) {
		return DEPLOYMENT_URL + "/apiv2/event/rating/"
				+ String.valueOf(eventId) + "/score/" + String.valueOf(score);
	}

	/**
	 * Substitute you own sender ID here. This is the project number you got
	 * from the API Console, as described in "Getting Started."
	 */
	public static final String SENDER_ID = "1048691354799";
	public static final String YOUTUBE_API_KEY = "AIzaSyCa8jFg_CLSFCCZbH5-2GWNlHbI_AP61Ng";
}
