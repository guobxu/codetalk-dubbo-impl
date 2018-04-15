package me.codetalk.flowapp;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.reactivex.disposables.Disposable;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.auth.model.AuthApiModel;
import me.codetalk.auth.model.entity.AuthInfo;
import me.codetalk.auth.model.entity.UserInfo;
import me.codetalk.auth.model.AuthPrefModel;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.fnd.model.entity.Tag;
import me.codetalk.model.PrefModel;

/**
 * Created by guobxu on 2017/12/30.
 */

public class FlowApplication extends Application {

    private static final String TAG = "FlowApplication";

    private AuthInfo authInfo;                      // Auth Info

    private List<Tag> userTags;

    private Disposable subscription = null;

    private BaseActivity currentActivity;

    // caches
    private LruCache<Long, UserInfo> userCache;
    private static final int CACHE_USER_MAX_ENTRY = 1000;

    // models
    private AuthPrefModel authModel = null;
    private AuthApiModel authApiModel = null;
    private PrefModel prefModel = null;

    public static FlowApplication INSTANCE = null;
    public static FlowApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;

        // Models
        Context appContext = getApplicationContext();
        authModel = AuthPrefModel.getInstance(appContext);
        authApiModel = AuthApiModel.getInstance(appContext);
        prefModel = PrefModel.getInstance(appContext);

        // Auth info
        authInfo = authModel.loadAuthInfo();

        // Cache
        userCache = new LruCache<>(CACHE_USER_MAX_ENTRY);

        // Event Bus
//        registerEventBus();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        unregisterEventBus();
    }

    public void setCurrentActivity(BaseActivity activity) {
        this.currentActivity = activity;
    }

    public BaseActivity getCurrentActivity() {
        return currentActivity;
    }

    public boolean isLoggedIn() {
        return authInfo.getUserId() != -1;
    }

    public void clearAuthInfo() {
        authModel.clearAuthInfo();

        authInfo = new AuthInfo();
    }

    public AuthInfo getAuthInfo() {
        return authInfo;
    }

    public void setAuthInfo(AuthInfo authInfo) {
        this.authInfo = authInfo;

        authModel.setAuthInfo(authInfo);
    }

    public List<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<Tag> userTags) {
        this.userTags = userTags;
    }

    public Set<String> getTagSet()  {
        Set<String> tags = new HashSet<>();
        if(userTags != null && userTags.size() > 0) {
            for(Tag tag : userTags) {
                tags.add(tag.getText());
            }
        }

        return tags;
    }

    public Set<String> getTagSetForSearch()  {
        Set<String> tags = new HashSet<>();
        if(userTags != null && userTags.size() > 0) {
            for(Tag tag : userTags) {
                tags.add(tag.getText().toLowerCase());
            }
        }

        return tags;
    }

    // Cache
    // Cache - UserInfo
    public UserInfo getLoginUser() {
        Long userId = authInfo.getUserId();

        return userId == null ? null : userCache.get(userId);
    }

    public UserInfo getUserInfo(Long userId) {
        return userCache.get(userId);
    }

    public void putUserInfo(UserInfo userInfo) {
        userCache.put(userInfo.getId(), userInfo);
    }

    // Event Bus
    protected void registerEventBus() {
        subscription = EventBus.subscribe(event -> {
            handleEvent(event);
        });
    }

    protected void unregisterEventBus() {
        if (subscription != null) {
            subscription.dispose();
        }
    }

    private void handleEvent(Event event) {
        if(!event.success()) {
            if(event.errmsg() != null) {
                Log.e(TAG, event.errmsg());
            }
            if(event.errkey() != null) {
                Log.e(TAG, getApplicationContext().getString(event.errkey()));
            }
        } else {
            if(AppConstants.EVENT_USERINFO_RT.equals(event.name())) {
                UserInfo userInfo = (UserInfo)event.extra1();
                putUserInfo(userInfo);
            }
        }
    }

}
