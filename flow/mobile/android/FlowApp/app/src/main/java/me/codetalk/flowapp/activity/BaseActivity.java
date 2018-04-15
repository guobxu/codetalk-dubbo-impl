package me.codetalk.flowapp.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import me.codetalk.annotation.EnableProfile;
import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.FlowApplication;
import me.codetalk.flowapp.R;
import me.codetalk.annotation.ActivityFeature;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.auth.model.entity.UserInfo;
import me.codetalk.model.PrefModel;
import me.codetalk.auth.model.entity.AuthInfo;
import me.codetalk.util.StringUtils;

/**
 * Created by guobxu on 24/12/2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements Validator.ValidationListener {

    private static final String TAG = "BaseActivity";

    protected PrefModel prefModel = null;

    protected Disposable subscription;

    protected Validator validator = null;
    protected boolean validated;

    protected FlowApplication flowApp;

    // features
    protected boolean hideActionBar = false;
    protected boolean hideStatusBar = false;
    protected boolean hideBottomNav = false;

    protected boolean isSticky = false;

    // perms
    private static final int REQ_CODE_PERM_WRITE_EXTERNAL = 11;

    // uploads: type ==> UploadSize
    private Map<String, UploadSize> uploads = null;

    // event
    private boolean enableEvent = false;
    private boolean unregisterWhenStop = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context appContext = getApplicationContext();
        prefModel = PrefModel.getInstance(appContext);

        flowApp = (FlowApplication)getApplication();

        // features
        ActivityFeature feature = getClass().getAnnotation(ActivityFeature.class);
        if(feature != null) setupFeature(feature);

        if(hideActionBar) {
            ActionBar actionBar = getSupportActionBar();
            if(actionBar != null) actionBar.hide();
        }

        // event
        EventSupport eventSupport = getClass().getAnnotation(EventSupport.class);
        if(eventSupport != null) {
            enableEvent = true;
            unregisterWhenStop = eventSupport.unregisterWhenStop();
        }

        if(enableEvent) {
            registerEventBus();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO: save profile image at local
        EnableProfile enableProfile = getClass().getAnnotation(EnableProfile.class);
        if(enableProfile != null) {
            showLoginProfile(findViewById(R.id.user_profile_img));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // event
        if(enableEvent && subscription == null) {
            registerEventBus();
        }

        // current activity
        flowApp.setCurrentActivity(this);
    }

    @Override
    public void onPause() {
        // clear current activity
        if(this.equals(flowApp.getCurrentActivity())) {
            flowApp.setCurrentActivity(null);
        }

        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(enableEvent && unregisterWhenStop) {
            unregisterEventBus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // event
        unregisterEventBus();

        // clear current activity
        if(this.equals(flowApp.getCurrentActivity())) {
            flowApp.setCurrentActivity(null);
        }
    }

    private void setupFeature(ActivityFeature feature) {
        int fval = feature.value();
        if( ( fval & ActivityFeature.HIDEACTIONBAR ) > 0) {
            hideActionBar = true;
        }
        if( ( fval & ActivityFeature.HIDESTATUS ) > 0) {
            hideStatusBar = true;
            hideActionBar = true;
        }
        if( ( fval & ActivityFeature.HIDEBOTTOMNAV ) > 0) {
            hideBottomNav = true;
        }

        isSticky = feature.sticky();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if(hideStatusBar) {
                visibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            if(hideBottomNav) {
                visibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if(hideStatusBar || hideBottomNav) {
                visibility |= (isSticky ? View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY : View.SYSTEM_UI_FLAG_IMMERSIVE);
            }

            getWindow().getDecorView().setSystemUiVisibility(visibility);
        }
    }

    /**
     * Hide soft keyboard and clear focus when click outside EditText
     *
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }

        return super.dispatchTouchEvent( event );
    }

    protected int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    // perms
    protected boolean isExtStoragePermGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestExtStoragePerm() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQ_CODE_PERM_WRITE_EXTERNAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case REQ_CODE_PERM_WRITE_EXTERNAL:
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    toast(R.string.warn_storage_perm_denied);
                }
                break;
        }
    }

    // Auth & User Info
    /**
     * Fetch auth info
     *
     * @return
     */
    public AuthInfo getAuthInfo() {
        return flowApp.getAuthInfo();
    }

    /**
     * Fetch user info
     *
     * @return
     */
    public UserInfo getLoginUser() {
        return flowApp.getLoginUser();
    }

    public Long getUserId() {
        AuthInfo authInfo = flowApp.getAuthInfo();

        return authInfo == null ? null : authInfo.getUserId();
    }

    public String getUserProfile() {
        UserInfo userInfo = flowApp.getLoginUser();

        return userInfo == null ? null : userInfo.getProfile();
    }

    /**
     * Whether logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return flowApp.isLoggedIn();
    }

    public void showUserProfile(ImageView profileImg, String profileUrl) {
        if(StringUtils.isNull(profileUrl)) {
            profileImg.setImageResource(AppConstants.USER_DEFAULT_PROFIE);
        } else {
            Glide.with(this)
                    .load(profileUrl)
                    .apply(new RequestOptions().dontAnimate())
                    .into(profileImg);
        }
    }

    /**
     * Load profile image
     *
     */
    public void showLoginProfile(ImageView profileImg) {
        Log.d(TAG, "In showLoginProfile...");

        if(!isLoggedIn()) {
            showLoginIcon(profileImg);
        } else {
            showUserProfile(profileImg, getUserProfile());
        }
    }

    public void showLoginIcon(ImageView profileImg) {
        Glide.with(this)
                .load(R.drawable.ic_account_circle_gray_36dp)
                .apply(new RequestOptions().dontAnimate())
                .into(profileImg);
    }

    public void loadImageUrl(ImageView imgView, String imgUrl) {
        Log.d(TAG, "In loadImageUrl...");

        Glide.with(this)
                .load(imgUrl)
                .apply(new RequestOptions().dontAnimate())
                .into(imgView);
    }

    public void loadImageLocal(ImageView imgView, URI uri) {
        Log.d(TAG, "In loadImageLocal...");

        Glide.with(this)
                .load(uri)
                .apply(new RequestOptions().dontAnimate())
                .into(imgView);
    }

    public void loadImageUrl(ImageView imgView, String imgUrl, int defaultResId) {
        Log.d(TAG, "In loadImageUrl...");

        Glide.with(this)
                .load(imgUrl)
                .apply(new RequestOptions().dontAnimate().placeholder(defaultResId))
                .into(imgView);
    }

    protected void setDefaultLayout(RecyclerView recyclerView, boolean withDivider) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(withDivider) {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
    }

    protected void replaceFragment(int contentViewId, Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction txn = fragmentManager.beginTransaction();
        txn.replace(contentViewId, fragment);
//        txn.addToBackStack(null);

        txn.commit();
    }

    /**
     * Show toast
     *
     * @param msgResId
     */
    public void toast(int msgResId) {
        Toast.makeText(this, getString(msgResId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Show toast
     *
     * @param mesg
     */
    public void toast(String mesg) {
        Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
    }

    // Validator
    @Override
    public void onValidationSucceeded() {
        validated = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validated = false;
    }

    // Event Bus
    protected void registerEventBus() {
        subscription = EventBus.subscribe(event -> {
            handleEvent(event);
        });
    }

    protected void unregisterEventBus() {
        if(subscription != null) {
            subscription.dispose();

            subscription = null;
        }
    }

    /**
     *
     * @param event
     */
    protected void handleEvent(Event event) {
        if(event.success()) {
            handleSuccess(event);
        } else {
            handleFail(event);
        }
    }

    /**
     * Handle fail or error event
     *
     * @param e
     */
    protected void handleFail(Event e) {
        Integer errKey = e.errkey();
        String errMsg = e.errmsg();

        if(errKey != null) {
            toast(errKey);
        } else if(errMsg != null) {
            toast(errMsg);
        }
    }

    protected void handleSuccess(Event event) {
        // empty
    }

    // Upload
    protected void addUpload(String type, long total) {
        if(uploads == null) uploads = new HashMap<>();

        UploadSize u = new UploadSize();
        u.type = type;
        u.total = total;
        uploads.put(type, u);
    }

    protected int incrAndPercent(String type, int bytes) {
        incr(type, bytes);
        return percent(type);
    }

    protected int percent(String type) {
        return uploads.get(type).percent();
    }

    protected void incr(String type, int bytes) {
        uploads.get(type).incr(bytes);
    }

    static class UploadSize {

        String type;
        long total;
        long uploaded;

        public void incr(int bytes) {
            uploaded += bytes;
        }

        public int percent() {
            return (int)(uploaded * 100 / total);
        }

    }

}
