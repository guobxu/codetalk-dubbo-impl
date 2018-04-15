package me.codetalk.flowapp;

/**
 * Created by guobxu on 24/12/2017.
 */

public final class AppConstants {

    public static final String ENCODING_UTF8 = "UTF-8";

    public static final String APP_ID = "me.codetalk.flowapp";

    public static final String CHAR_MENTION = "@";
    public static final String CHAR_HASHTAG = "#";

    public static final int USER_DEFAULT_PROFIE = R.drawable.blank_user_profile;

    // field file
    public static final String FIELD_FILE = "file";

    // upload types
    public static final String UPLOAD_TYPE_USER_BGCOVER = "1";
    public static final String UPLOAD_TYPE_USER_PROFILE = "2";

    public static final String UPLOAD_TYPE_POST_IMAGE = "10";

    // POST
    public static final int POST_TYPE_BASIC = 1;    // 普通帖子
    public static final int POST_TYPE_POLL = 2;     // 投票


    // Event
    // -- Auth
    public static final String EVENT_GO_LOGIN = "EVENT_GO_LOGIN";
    public static final String EVENT_GO_SIGNUP = "EVENT_GO_SIGNUP";

    public static final String EVENT_SSESS_RT = "EVENT_SSESS_RT";
    public static final String EVENT_SSESS_ERR = "EVENT_SSESS_ERR";

    public static final String EVENT_SIGNUP_RT = "EVENT_SIGNUP_RT";
    public static final String EVENT_SIGNUP_ERR = "EVENT_SIGNUP_ERR";

    public static final String EVENT_LOGIN_RT = "EVENT_LOGIN_RT";
    public static final String EVENT_LOGIN_ERR = "EVENT_LOGIN_ERR";

    public static final String EVENT_LOGIN_SUCCESS = "EVENT_LOGIN_SUCCESS";

    public static final String EVENT_USERINFO_RT = "EVENT_USERINFO_RT";
    public static final String EVENT_USERINFO_ERR = "EVENT_USERINFO_ERR";

    public static final String EVENT_USERINFO_UPDATE_RT = "EVENT_USERINFO_UPDATE_RT";
    public static final String EVENT_USERINFO_UPDATE_ERR = "EVENT_USERINFO_UPDATE_ERR";

    public static final String EVENT_APPLOGIN_RT = "EVENT_APPLOGIN_RT";
    public static final String EVENT_APPLOGIN_ERR = "EVENT_APPLOGIN_ERR";

    // FND
    public static final int ACTION_LIST_LOAD_MORE = 1;
    public static final int ACTION_LIST_REFRESH = 2;

    public static final int ACTION_FOLLOW = 1;
    public static final int ACTION_UNFOLLOW = 2;

    public static final String EVENT_USER_FOLLOW_RT = "EVENT_USER_FOLLOW_RT";
    public static final String EVENT_USER_FOLLOW_ERR = "EVENT_USER_FOLLOW_ERR";

    public static final String EVENT_USER_EXIST_FOLLOW_RT = "EVENT_USER_EXIST_FOLLOW_RT";
    public static final String EVENT_USER_EXIST_FOLLOW_ERR = "EVENT_USER_EXIST_FOLLOW_ERR";

    public static final String EVENT_USER_COUNT_FOLLOW_RT = "EVENT_USER_COUNT_FOLLOW_RT";
    public static final String EVENT_USER_COUNT_FOLLOW_ERR = "EVENT_USER_COUNT_FOLLOW_ERR";

    // UPLOAD
    public static final String EVENT_FILEUPLOAD_RT = "EVENT_FILEUPLOAD_RT";
    public static final String EVENT_FILEUPLOAD_ERR = "EVENT_FILEUPLOAD_ERR";

    public static final String EVENT_FILEUPLOAD_BYTES = "EVENT_FILEUPLOAD_BYTES";

    // POST
    public static final String EVENT_POST_CREATE_RT = "EVENT_POST_CREATE_RT";
    public static final String EVENT_POST_CREATE_ERR = "EVENT_POST_CREATE_ERR";

    public static final String EVENT_POST_IMG_DELETE = "EVENT_POST_IMG_DELETE";

    public static final int POLL_MAX_OPTS = 4;

    public static final int POST_PAGE_SIZE = 20; // 每页帖子数量

    // POST - search
    public static final int MAX_SEARCH_ITEMS = 6;
    public static final String PREF_RECENT_RESEARCH = "PREF_RECENT_RESEARCH";

    public static final String EVENT_POST_SEARCH_RT = "EVENT_POST_SEARCH_RT";
    public static final String EVENT_POST_SEARCH_ERR = "EVENT_POST_SEARCH_ERR";

    public static final String EVENT_POST_USER_LIST_RT = "EVENT_POST_USER_LIST_RT";
    public static final String EVENT_POST_USER_LIST_ERR = "EVENT_POST_USER_LIST_ERR";

    public static final String EVENT_POST_LIKE_LIST_RT = "EVENT_POST_LIKE_LIST_RT";
    public static final String EVENT_POST_LIKE_LIST_ERR = "EVENT_POST_LIKE_LIST_ERR";

    public static final String EVENT_POST_LIST_RELOAD = "EVENT_POST_LIST_RELOAD";

    public static final String EVENT_ACTION_SEARCH = "EVENT_ACTION_SEARCH";

    public static final String EVENT_POST_LIKE_RT = "EVENT_POST_LIKE_RT";
    public static final String EVENT_POST_LIKE_ERR = "EVENT_POST_LIKE_ERR";

    public static final String EVENT_POST_DTL_RT = "EVENT_POST_DTL_RT";
    public static final String EVENT_POST_DTL_ERR = "EVENT_POST_DTL_ERR";

    public static final String EVENT_CMNT_LIKE_RT = "EVENT_CMNT_LIKE_RT";
    public static final String EVENT_CMNT_LIKE_ERR = "EVENT_CMNT_LIKE_ERR";

    public static final String EVENT_CMNT_DETAIL_RT = "EVENT_CMNT_DETAIL_RT";
    public static final String EVENT_CMNT_DETAIL_ERR = "EVENT_CMNT_DETAIL_ERR";

    public static final int ACTION_LIKE = 1;
    public static final int ACTION_UNLIKE = 2;

    // TAG
    public static final int MAX_USER_TAG = 10; // max 10 tag per user

    public static final String EVENT_TAG_LIST_RT = "EVENT_TAG_LIST_RT";
    public static final String EVENT_TAG_LIST_ERR = "EVENT_TAG_LIST_ERR";

    public static final String EVENT_TAG_TOPBYDAY_RT = "EVENT_TAG_TOPBYDAY_RT";
    public static final String EVENT_TAG_TOPBYDAY_ERR = "EVENT_TAG_TOPBYDAY_ERR";

    public static final String EVENT_TAG_SELECT_CHANGE = "EVENT_TAG_SELECT_CHANGE";

    public static final String EVENT_USERTAG_RT = "EVENT_USERTAG_RT";
    public static final String EVENT_USERTAG_ERR = "EVENT_USERTAG_ERR";

    public static final String EVENT_USERTAG_UPDATE_RT = "EVENT_USERTAG_UPDATE_RT";
    public static final String EVENT_USERTAG_UPDATE_ERR = "EVENT_USERTAG_UPDATE_ERR";

    public static final String EVENT_USERTAG_ADD_RT = "EVENT_USERTAG_ADD_RT";
    public static final String EVENT_USERTAG_ADD_ERR = "EVENT_USERTAG_ADD_ERR";

    public static final String EVENT_TAG_CLICK = "EVENT_TAG_CLICK";

    // Comment
    public static final String EVENT_COMMENT_CREATE_RT = "EVENT_COMMENT_CREATE_RT";
    public static final String EVENT_COMMENT_CREATE_ERR = "EVENT_COMMENT_CREATE_ERR";

}



















