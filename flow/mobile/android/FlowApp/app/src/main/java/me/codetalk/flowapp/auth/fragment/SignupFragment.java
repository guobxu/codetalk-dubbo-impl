package me.codetalk.flowapp.auth.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.codetalk.annotation.EventSupport;
import me.codetalk.auth.AuthUtils;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.auth.api.request.SignupBody;
import me.codetalk.custom.view.SpinnerButton;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.auth.model.entity.SignupSession;
import me.codetalk.auth.model.SignupSessionModel;

import static me.codetalk.flowapp.AppConstants.EVENT_APPLOGIN_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_APPLOGIN_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_LOGIN_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_SIGNUP_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_SIGNUP_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_SSESS_RT;

/**
 * Created by guobxu on 2017/12/25.
 */
@EventSupport
public class SignupFragment extends BaseAuthFragment implements View.OnClickListener {

    private static final String TAG = "SignupFragment";

    // models
    private SignupSessionModel ssModel = null;

    @BindView(R.id.signup_input_userlogin)
    @Pattern(regex = "\\w{5,}", messageResId = R.string.err_invalid_userlogin)
    EditText inputUserLogin;

    @BindView(R.id.signup_input_passwd)
    @Password(min = 6, scheme = Password.Scheme.ALPHA_MIXED_CASE, messageResId = R.string.err_invalid_passwd)
    EditText inputPasswd;

    @BindView(R.id.signup_input_vcode)
    @Pattern(regex = "\\d{5}", messageResId = R.string.err_invalid_vcode)
    EditText inputVcode;

    @BindView(R.id.signup_image_vcode)
    ImageView imageVcode;

    @BindView(R.id.signup_spinner_btn_signup)
    SpinnerButton btnSignup;
    @BindView(R.id.signup_action_login)
    TextView actionLogin;

    private String tmpUserLogin;        // save for temp usage
    private String tmpPasswd;           // save for temp usage

    public SignupFragment() {}

    public static SignupFragment newInstance() {
        return new SignupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ButterKnife.bind(this, view);

        validator = new Validator(this);
        validator.setValidationListener(this);

        btnSignup.setOnClickListener(this);
        actionLogin.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ssModel = SignupSessionModel.getInstance(mContext.getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        authApiModel.signupSession();

        inputUserLogin.requestFocus();
    }

    // Click

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.signup_action_login:
                Event e = Event.builder().name(AppConstants.EVENT_GO_LOGIN).build();
                EventBus.publish(e);
                break;
            case R.id.spinner_btn_action:
                actionSignup();
                break;
        }
    }

    private void actionSignup() {
        validator.validate();
        if(!validated) return;

        btnSignup.setLoading(true);
        String vcode = inputVcode.getText().toString().trim();
        tmpUserLogin = inputUserLogin.getText().toString().trim();
        tmpPasswd = inputPasswd.getText().toString().trim();

        SignupSession ssess = ssModel.loadSignupSession();
        if(ssess.getId() == null) {
            authApiModel.signupSession();
            inputVcode.setText("");
            inputVcode.requestFocus();

            toast(R.string.err_vcode_expired);

            return;
        }

        SignupBody body = new SignupBody();
        body.setUserLogin(tmpUserLogin);
        body.setSignupCode(vcode);
        body.setSignupSessionId(ssess.getId());

        try {
            String pwdCipher = AuthUtils.cipherPwd(tmpPasswd, ssess.getPasswdKey());
            body.setPasswd(pwdCipher);
        } catch(Exception ex) {
            ex.printStackTrace();

            toast(R.string.err_cipher_error);

            return;
        }

        authApiModel.signup(body);
    }

    // Event Bus
    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();
        if(EVENT_SSESS_RT.equals(name)) {    // signup session
            SignupSession ssess = (SignupSession)event.extra1();
            byte[] decodedString = Base64.decode(ssess.getImgdata(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imageVcode.setImageBitmap(decodedByte);
        } else if(EVENT_SIGNUP_RT.equals(name)) {    // signup event
            handleSignupSuccess(event);
        } else if(EVENT_LOGIN_RT.equals(name)) {
            handleAuthSuccess(event);
        } else if(EVENT_APPLOGIN_RT.equals(name)) {
            handleAppLoginSuccess(event);
        }
    }

    @Override
    protected void handleFail(Event event) {
        super.handleFail(event);

        String name = event.name();
        if(EVENT_SIGNUP_ERR.equals(name)) {
            btnSignup.setLoading(false);

            ssModel.clearSignupSession();
            authApiModel.signupSession();
        } else if(EVENT_APPLOGIN_ERR.equals(name)) {
            handleAppLoginFail(event);
        }
    }

    private void handleSignupSuccess(Event e) {
        toast(R.string.msg_signup_success);

        btnSignup.setLoading(false);

        login(tmpUserLogin, tmpPasswd);
    }

}
