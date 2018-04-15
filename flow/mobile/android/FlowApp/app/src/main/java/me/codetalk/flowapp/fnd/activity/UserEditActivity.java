package me.codetalk.flowapp.fnd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.auth.api.request.UserInfoBody;
import me.codetalk.event.Event;
import me.codetalk.auth.model.AuthApiModel;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.model.FileUploadApiModel;
import me.codetalk.auth.model.entity.UserInfo;
import me.codetalk.util.FileUtils;
import me.codetalk.util.StringUtils;

import static me.codetalk.flowapp.AppConstants.UPLOAD_TYPE_USER_BGCOVER;
import static me.codetalk.flowapp.AppConstants.UPLOAD_TYPE_USER_PROFILE;
import static me.codetalk.flowapp.AppConstants.EVENT_FILEUPLOAD_RT;

@EventSupport
public class UserEditActivity extends BaseActivity {

    private static final String TAG = "UserEditActivity";

    private AuthApiModel authApiModel = null;
    private FileUploadApiModel fileUploadApiModel = null;

    @BindView(R.id.toolbar_default)
    Toolbar toolbarUserEdit;

    @BindView(R.id.useredit_input_username)
    EditText inputUsername;

    @BindView(R.id.useredit_input_signature)
    EditText inputSig;

    @BindView(R.id.useredit_input_location)
    EditText inputLocation;

    @BindView(R.id.useredit_input_site)
    EditText inputSite;

    // Button - upload image
    @BindView(R.id.useredit_btn_bgcover)
    ImageButton btnBgcover;

    @BindView(R.id.useredit_btn_profile)
    ImageButton btnProfile;

    // Toolbar
    @BindView(R.id.toolbar_action_left)
    TextView actionCanel;

    @BindView(R.id.toolbar_action_right)
    TextView actionSave;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    // ImageView
    @BindView(R.id.useredit_bgcover)
    ImageView imgBgcover;

    @BindView(R.id.useredit_profile_img)
    CircleImageView imgProfile;

    // original values
    private String initialNameTrim;
    private String initialSigTrim;
    private String initialLocTrim;
    private String initialSiteTrim;

    // request codes
    private static final int REQ_CODE_BGCOVER = 1;  // bgcover
    private static final int REQ_CODE_PROFILE = 2;  // profile

    @BindView(R.id.useredit_progress_bgcover)
    ProgressBar progressBgcover;

    @BindView(R.id.useredit_progress_profile)
    ProgressBar progressProfile;

    // images
    private String bgcoverUrl;
    private String profileUrl;

    // form status
    private boolean textChanged = false;
    private boolean bgcoverReady = true;
    private boolean profileReady = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        ButterKnife.bind(this);

        // toobar
        setupToolbar();

        // show
        showUserInfo();

        // original values
        initialNameTrim = inputUsername.getText().toString().trim();
        initialSigTrim = inputSig.getText().toString().trim();
        initialLocTrim = inputLocation.getText().toString().trim();
        initialSiteTrim = inputSite.getText().toString().trim();

        // text change
        inputUsername.addTextChangedListener(new UserTextWatcher());
        inputSig.addTextChangedListener(new UserTextWatcher());
        inputLocation.addTextChangedListener(new UserTextWatcher());
        inputSite.addTextChangedListener(new UserTextWatcher());

        // buttons
        btnBgcover.setOnClickListener(view -> {
            if(!isExtStoragePermGranted()) {
                requestExtStoragePerm();
            } else {
                Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                photoPickIntent.setType("image/*");
                startActivityForResult(photoPickIntent, REQ_CODE_BGCOVER);
            }
        });

        btnProfile.setOnClickListener(view -> {
            if(!isExtStoragePermGranted()) {
                toast(R.string.warn_storage_perm_denied);
            } else {
                Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
                photoPickIntent.setType("image/*");
                startActivityForResult(photoPickIntent, REQ_CODE_PROFILE);
            }
        });

        // cancel
        actionCanel.setOnClickListener(view -> {
            onBackPressed();
        });

        // save
        actionSave.setOnClickListener(view -> {
            UserInfoBody userInfoBody = new UserInfoBody();
            if(bgcoverUrl != null) userInfoBody.setBgcover(bgcoverUrl);
            if(profileUrl != null) userInfoBody.setProfile(profileUrl);
            userInfoBody.setName(inputUsername.getText().toString().trim());
            userInfoBody.setSignature(inputSig.getText().toString().trim());
            userInfoBody.setLocation(inputLocation.getText().toString().trim());
            userInfoBody.setSite(inputSite.getText().toString().trim());

            authApiModel.updateUserInfo(userInfoBody);
        });

        // models
        Context appContext = getApplicationContext();
        authApiModel = AuthApiModel.getInstance(appContext);
        fileUploadApiModel = FileUploadApiModel.getInstance(appContext);

        // request perms
        requestExtStoragePerm();
    }

    private void setupToolbar() {
        actionCanel.setText(R.string.cancel);
        actionSave.setText(R.string.save);
        toolbarTitle.setText(R.string.edit_profile);

        setSupportActionBar(toolbarUserEdit);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        String filePath = FileUtils.getRealPathFromURI(data.getData(), this);
        File file = new File(filePath);
        if(requestCode == REQ_CODE_BGCOVER) {
            addUpload(UPLOAD_TYPE_USER_BGCOVER, file.length());
            progressBgcover.setProgress(0);
            progressBgcover.setVisibility(View.VISIBLE);
            setBgcoverReady(false);

            fileUploadApiModel.upload(file, UPLOAD_TYPE_USER_BGCOVER);
        } else if(requestCode == REQ_CODE_PROFILE) {
            addUpload(UPLOAD_TYPE_USER_PROFILE, file.length());
            progressProfile.setProgress(0);
            progressProfile.setVisibility(View.VISIBLE);
            setProfileReady(false);

            fileUploadApiModel.upload(file, UPLOAD_TYPE_USER_PROFILE);
        }
    }

    private void showUserInfo() {
        UserInfo userInfo = flowApp.getLoginUser();
        if(userInfo == null) return;

        inputUsername.setText(userInfo.getName());
        inputSig.setText(userInfo.getSignature());
        inputLocation.setText(userInfo.getLocation());
        inputSite.setText(userInfo.getSite());

        // images
        String bgcoverUrl = userInfo.getBgcover();
        if(StringUtils.isNotNull(bgcoverUrl)) {
            loadImageUrl(imgBgcover, bgcoverUrl, R.drawable.blank_user_bgcover);
        }

        showUserProfile(imgProfile, getUserProfile());
    }

    public void setTextChanged(boolean textChanged) {
        this.textChanged = textChanged;

        enableSaveButton();
    }

    public void setBgcoverReady(boolean bgcoverReady) {
        this.bgcoverReady = bgcoverReady;

        enableSaveButton();
    }

    public void setProfileReady(boolean profileReady) {
        this.profileReady = profileReady;

        enableSaveButton();
    }

    private void enableSaveButton() {
        String usernameText = inputUsername.getText().toString();

        if(!bgcoverReady || !profileReady) {
            actionSave.setEnabled(false);
        } else if( StringUtils.isBlank(usernameText) ) {
            actionSave.setEnabled(false);
        } else if( bgcoverUrl != null || profileUrl != null || textChanged) {
            actionSave.setEnabled(true);
        } else {
            actionSave.setEnabled(false);
        }
    }

    private class UserTextWatcher implements TextWatcher {

        // Note:
        // Original: guobxu, delete back
        // beforeTextChanged = guobxu
        // onTextChanged = guobx
        // afterTextChanged = guobx

        @Override
        public void afterTextChanged(Editable editable) {
            setTextChanged(true);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        }

    }

    // Event
    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();
        if(AppConstants.EVENT_USERINFO_UPDATE_RT.equals(name)) {
            finish();

            authApiModel.userInfo(getUserId(), userInfo -> {
                flowApp.putUserInfo(userInfo);
            });
        } else if(EVENT_FILEUPLOAD_RT.equals(name)) {
            String type = (String)event.extra1(), fileUrl = ( (List<String>)event.extra2() ).get(0);

            if(UPLOAD_TYPE_USER_BGCOVER.equals(type)) {
                bgcoverUrl = fileUrl;
                loadImageUrl(imgBgcover, bgcoverUrl);

                progressBgcover.setVisibility(View.GONE);
                setBgcoverReady(true);
            } else if(UPLOAD_TYPE_USER_PROFILE.equals(type)) {
                profileUrl = fileUrl;
                loadImageUrl(imgProfile, profileUrl);

                progressProfile.setVisibility(View.GONE);
                setProfileReady(true);
            }
        } else if(AppConstants.EVENT_FILEUPLOAD_BYTES.equals(name)) {
            String type = (String)event.extra1();
            Integer bytes = (Integer)event.extra2();

            int percent = incrAndPercent(type, bytes);
            if(UPLOAD_TYPE_USER_BGCOVER.equals(type)) {
                progressBgcover.setProgress(percent);
            } else if(UPLOAD_TYPE_USER_PROFILE.equals(type)) {
                progressProfile.setProgress(percent);
            }
        }
    }

    @Override
    protected void handleFail(Event event) {
        String name = event.name();
        if(EVENT_FILEUPLOAD_RT.equals(name)) {
            String type = (String)event.extra1();
            if(UPLOAD_TYPE_USER_BGCOVER.equals(type)) {
                setBgcoverReady(true);
            } else if(UPLOAD_TYPE_USER_PROFILE.equals(type)) {
                setProfileReady(true);
            }
        }
    }

}


