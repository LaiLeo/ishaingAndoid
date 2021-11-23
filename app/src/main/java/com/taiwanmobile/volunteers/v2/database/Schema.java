package com.taiwanmobile.volunteers.v2.database;

import java.util.Date;
import java.util.Map;

import android.util.Pair;

import com.google.common.collect.ImmutableMap;

public class Schema {
	public static interface AuthGroup {
		public static final String $NAME = "auth_group";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("name", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String NAME = "name";
	}

	public static interface AuthGroupPermissions {
		public static final String $NAME = "auth_group_permissions";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("group_id", Long.class).put("permission_id", Long.class)
				.build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String GROUP_ID = "group_id";
		public static final String PERMISSION_ID = "permission_id";
	}

	public static interface AuthPermission {
		public static final String $NAME = "auth_permission";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("name", String.class).put("content_type_id", Long.class)
				.put("codename", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String CONTENT_TYPE_ID = "content_type_id";
		public static final String CODENAME = "codename";
	}

	public static interface AuthUser {
		public static final String $NAME = "auth_user";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("password", String.class).put("last_login", Date.class)
				.put("is_superuser", Boolean.class)
				.put("username", String.class).put("first_name", String.class)
				.put("last_name", String.class).put("email", String.class)
				.put("is_staff", Boolean.class).put("is_active", Boolean.class)
				.put("date_joined", Date.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String PASSWORD = "password";
		public static final String LAST_LOGIN = "last_login";
		public static final String IS_SUPERUSER = "is_superuser";
		public static final String USERNAME = "username";
		public static final String FIRST_NAME = "first_name";
		public static final String LAST_NAME = "last_name";
		public static final String EMAIL = "email";
		public static final String IS_STAFF = "is_staff";
		public static final String IS_ACTIVE = "is_active";
		public static final String DATE_JOINED = "date_joined";
	}

	public static interface AuthUserGroups {
		public static final String $NAME = "auth_user_groups";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class).put("group_id", Long.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String GROUP_ID = "group_id";
	}

	public static interface AuthUserUserPermissions {
		public static final String $NAME = "auth_user_user_permissions";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class).put("permission_id", Long.class)
				.build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String PERMISSION_ID = "permission_id";
	}

	public static interface CoreComment {
		public static final String $NAME = "core_comment";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("header_id", Long.class).put("author_id", Long.class)
				.put("content", String.class).put("pub_date", Date.class)
				.put("uid", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String HEADER_ID = "header_id";
		public static final String AUTHOR_ID = "author_id";
		public static final String CONTENT = "content";
		public static final String PUB_DATE = "pub_date";
		public static final String UID = "uid";
	}

	public static interface CoreEvent {
		public static final String $NAME = "core_event";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("owner_NPO_id", Long.class)
				.put("image_link_1", String.class)
				.put("image_link_2", String.class)
				.put("image_link_3", String.class)
				.put("image_link_4", String.class)
				.put("image_link_5", String.class).put("uid", String.class)
				.put("cooperation_npo", Long.class)
				.put("tags", String.class).put("pub_date", Date.class)
				.put("happen_date", Date.class).put("close_date", Date.class)
				.put("register_deadline_date", Date.class)
				.put("subject", String.class).put("description", String.class)
				.put("event_hour", Long.class).put("comment", Long.class)
				.put("address", String.class).put("address_city", String.class)
				.put("insurance", Boolean.class)
				.put("volunteer_training", Boolean.class)
				.put("volunteer_training_description", String.class)
				.put("required_group", Boolean.class)
				.put("insurance_description", String.class)
				.put("current_volunteer_number", Long.class)
				.put("focus_num", Integer.class)
				.put("required_volunteer_number", Long.class)
				.put("skills_description", String.class)
				.put("lat", Double.class).put("lng", Double.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String OWNER_NPO_ID = "owner_NPO_id";
		public static final String IMAGE_LINK_1 = "image_link_1";
		public static final String IMAGE_LINK_2 = "image_link_2";
		public static final String IMAGE_LINK_3 = "image_link_3";
		public static final String IMAGE_LINK_4 = "image_link_4";
		public static final String IMAGE_LINK_5 = "image_link_5";
		public static final String UID = "uid";
		public static final String TAGS = "tags";
		public static final String PUB_DATE = "pub_date";
		public static final String HAPPEN_DATE = "happen_date";
		public static final String CLOSE_DATE = "close_date";
		public static final String REGISTER_DEADLINE_DATE = "register_deadline_date";
		public static final String SUBJECT = "subject";
		public static final String DESCRIPTION = "description";
		public static final String EVENT_HOUR = "event_hour";
		public static final String COMMENT = "comment";
		public static final String ADDRESS = "address";
		public static final String ADDRESS_CITY = "address_city";
		public static final String FOCUS_NUM = "focus_num";
		public static final String ENSURANCE = "insurance";
		public static final String TRAINING = "volunteer_training";
		public static final String TRAINING_DESCRIPTION = "volunteer_training_description";
		public static final String ENSURANCE_DESCRIPTION = "insurance_description";
		public static final String CURRENT_VOLUNTEER_NUMBER = "current_volunteer_number";
		public static final String REQUIRED_VOLUNTEER_NUMBER = "required_volunteer_number";
		public static final String REQUIRED_GROUP = "required_group";
		public static final String SKILLS = "skills_description";
		public static final String LAT = "lat";
		public static final String LNG = "lng";
	}

	public static interface CoreEventRequiredSkills {
		public static final String $NAME = "core_event_required_skills";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("event_id", Long.class).put("skill_id", Long.class)
				.build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String EVENT_ID = "event_id";
		public static final String SKILL_ID = "skill_id";
	}

	public static interface CoreImage {
		public static final String $NAME = "core_image";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("header_id", Long.class).put("image_link", String.class)
				.put("image_has_thumb", Boolean.class)
				.put("ratio", Double.class).put("pub_date", Date.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String HEADER_ID = "header_id";
		public static final String IMAGE_LINK = "image_link";
		public static final String IMAGE_HAS_THUMB = "image_has_thumb";
		public static final String RATIO = "ratio";
		public static final String PUB_DATE = "pub_date";
	}

	public static interface CoreNpo {
		public static final String $NAME = "core_npo";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class).put("name", String.class)
				.put("uid", String.class).put("npo_icon", String.class)
				.put("description", String.class)
				.put("register_number", String.class)
				.put("serial_number", String.class)
				.put("verified_image", String.class)
				.put("service_target", String.class)
				.put("partner", String.class)
				.put("government_register_image", String.class)
				.put("quality", String.class)
				.put("quality_prove_image", String.class)
				.put("member_type", String.class).put("pub_date", Date.class)
				.put("contact_name", String.class)
				.put("contact_phone", String.class)
				.put("contact_email", String.class)
				.put("contact_address", String.class)
				.put("contact_website", String.class)
				.put("contact_site", String.class)
                .put("is_verified", Boolean.class)
                .put("is_enterprise", Boolean.class)
				.build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String NAME = "name";
		public static final String UID = "uid";
		public static final String NPO_ICON = "npo_icon";
		public static final String DESCRIPTION = "description";
		public static final String REGIST_NUMBER = "register_number";
		public static final String SERIAL_NUMBER = "serial_number";
		public static final String VERIFIED_IMAGE = "verified_image";
		public static final String SERVICE_TARGET = "service_target";
		public static final String PARTNER = "partner";
		public static final String GOVERMENT_REGISTER_IMAGE = "government_register_image";
		public static final String QUALITY = "quality";
		public static final String QUALITY_PROVE_IMAGE = "quality_prove_image";
		public static final String MEMBER_TYPE = "member_type";
		public static final String PUB_DATE = "pub_date";
		public static final String CONTACT_NAME = "contact_name";
		public static final String CONTACT_PHONE = "contact_phone";
		public static final String CONTACT_EMAIL = "contact_email";
		public static final String CONTACT_ADDRESS = "contact_address";
		public static final String CONTACT_WEBSITE = "contact_website";
		public static final String CONTACT_SITE = "contact_site";
        public static final String IS_VERIFIED = "is_verified";
        public static final String IS_ENTERPRISE = "is_enterprise";
	}

	//
	public static interface CoreSkillGroup {
		public static final String $NAME = "core_skillgroup";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("owner_event", Long.class).put("name", String.class)
				.put("skills_description", String.class)
				.put("volunteer_number", Long.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String OWNER_EVENT = "owner_event";
		public static final String NAME = "name";
		public static final String SKILLS_DESCRIPTION = "skills_description";
		public static final String VOLUNTEER_NUMBER = "volunteer_number";
	}

	public static interface CoreSkill {
		public static final String $NAME = "core_skill";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("name", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String NAME = "name";
	}

	public static interface CoreUseraccount {
		public static final String $NAME = "core_useraccount";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class).put("uid", String.class)
				.put("security_id", String.class).put("photo", String.class)
				.put("name", String.class).put("phone", String.class)
				.put("email", String.class).put("guardian_name", String.class)
				.put("guardian_phone", String.class)
				.put("skills_description", String.class)
				.put("birthday", Date.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String SECURITY_ID = "security_id";
		public static final String UID = "uid";
		public static final String PHOTO = "photo";
		public static final String NAME = "name";
		public static final String PHONE = "phone";
		public static final String EMAIL = "email";
		public static final String SKILL = "skills_description";
		public static final String BIRTHDAY = "birthday";
		public static final String GUARDIAN_NAME = "guardian_name";
		public static final String GUARDIAN_PHONE = "guardian_phone";
	}

	public static interface CoreUseraccountSkills {
		public static final String $NAME = "core_useraccount_skills";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("useraccount_id", Long.class).put("skill_id", Long.class)
				.build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USERACCOUNT_ID = "useraccount_id";
		public static final String SKILL_ID = "skill_id";
	}

	public static interface CoreUserfocusedevent {
		public static final String $NAME = "core_userfocusedevent";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class).put("focused_event_id", Long.class)
				.build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String FOCUSED_EVENT_ID = "focused_event_id";
	}

	public static interface CoreUserregisteredevent {
		public static final String $NAME = "core_userregisteredevent";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class)
				.put("registered_event_id", Long.class)
				.put("score", Float.class).put("isJoined", Boolean.class)
				.put("name", String.class).put("phone", String.class)
				.put("email", String.class).put("skills", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String REGISTERED_EVENT_ID = "registered_event_id";
		public static final String ISJOINED = "isJoined";
		public static final String NAME = "name";
		public static final String PHONE = "phone";
		public static final String EMAIL = "email";
		public static final String SKILLS = "skills";
		public static final String SCORE = "score";
	}

	public static interface CoreUsersubscribednpo {
		public static final String $NAME = "core_usersubscribednpo";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("user_id", Long.class)
				.put("subscribed_NPO_id", Long.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String USER_ID = "user_id";
		public static final String SUBSCRIBED_NPO_ID = "subscribed_NPO_id";
	}

    public static interface CooperationNpo{
        String $NAME = "cooperation_npo";
        Map<String, Class<?>> $COLS = ImmutableMap
                .<String, Class<?>> builder()
                .put("id", Long.class)
                .put("event_id", Long.class)
                .put("name", String.class)
                .build();
        Pair<String, Map<String, Class<?>>> $META = Pair.create($NAME, $COLS);

        String ID = "id";
        String NAME = "name";
        String EVENT_ID = "event_id";

    }

	public static interface DjangoAdminLog {
		public static final String $NAME = "django_admin_log";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("action_time", Date.class).put("user_id", Long.class)
				.put("content_type_id", Long.class)
				.put("object_id", String.class)
				.put("object_repr", String.class)
				.put("action_flag", Long.class)
				.put("change_message", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String ACTION_TIME = "action_time";
		public static final String USER_ID = "user_id";
		public static final String CONTENT_TYPE_ID = "content_type_id";
		public static final String OBJECT_ID = "object_id";
		public static final String OBJECT_REPR = "object_repr";
		public static final String ACTION_FLAG = "action_flag";
		public static final String CHANGE_MESSAGE = "change_message";
	}

	public static interface DjangoContentType {
		public static final String $NAME = "django_content_type";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("name", String.class).put("app_label", String.class)
				.put("model", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String NAME = "name";
		public static final String APP_LABEL = "app_label";
		public static final String MODEL = "model";
	}

	public static interface DjangoSession {
		public static final String $NAME = "django_session";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("session_key", String.class)
				.put("session_data", String.class)
				.put("expire_date", Date.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String SESSION_KEY = "session_key";
		public static final String SESSION_DATA = "session_data";
		public static final String EXPIRE_DATE = "expire_date";
	}

	public static interface DjangoSite {
		public static final String $NAME = "django_site";
		public static final Map<String, Class<?>> $COLS = ImmutableMap
				.<String, Class<?>> builder().put("id", Long.class)
				.put("domain", String.class).put("name", String.class).build();
		public static final Pair<String, Map<String, Class<?>>> $META = Pair
				.create($NAME, $COLS);
		public static final String ID = "id";
		public static final String DOMAIN = "domain";
		public static final String NAME = "name";
	}
}