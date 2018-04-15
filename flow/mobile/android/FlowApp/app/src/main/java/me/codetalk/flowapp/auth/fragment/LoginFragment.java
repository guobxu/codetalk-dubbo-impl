package me.codetalk.flowapp.auth.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.R;
import me.codetalk.custom.view.SpinnerButton;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;

import static me.codetalk.flowapp.AppConstants.EVENT_APPLOGIN_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_APPLOGIN_RT;
import static me.codetalk.flowapp.AppConstants.EVENT_GO_SIGNUP;
import static me.codetalk.flowapp.AppConstants.EVENT_LOGIN_ERR;
import static me.codetalk.flowapp.AppConstants.EVENT_LOGIN_RT;

/**
 * Created by guobxu on 2017/12/25.
 */

@EventSupport
public class LoginFragment extends BaseAuthFragment implements View.OnClickListener {

    @BindView(R.id.login_input_userlogin)
    @Pattern(regex = "\\w{5,}", messageResId = R.string.err_invalid_userlogin)
    EditText inputUserLogin;
    @BindView(R.id.login_input_passwd)
    @Password(min = 6, scheme = Password.Scheme.ALPHA_MIXED_CASE, messageResId = R.string.err_invalid_passwd)
    EditText inputPasswd;

    @BindView(R.id.login_spinner_btn_login)
    SpinnerButton btnLogin;

    @BindView(R.id.login_action_signup)
    TextView actionSignup;

    private String tmpUserLogin;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, view);

        btnLogin.setOnClickListener(this);

        actionSignup.setOnClickListener(this);

        // validate
        validator = new Validator(this);
        validator.setValidationListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        inputUserLogin.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.login_action_signup:
                Event e = Event.builder().name(EVENT_GO_SIGNUP).build();
                EventBus.publish(e);
                break;
            case R.id.spinner_btn_action:
                actionLogin();
                break;
        }
    }

    private void actionLogin() {
        validator.validate();
        if(!validated) return;

        btnLogin.setLoading(true);
        tmpUserLogin = inputUserLogin.getText().toString().trim();
        String passwd = inputPasswd.getText().toString().trim();

        login(tmpUserLogin, passwd);
    }

    // Event Bus
    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();
        if(EVENT_LOGIN_RT.equals(name)) {
            handleAuthSuccess(event);

            btnLogin.setLoading(false);
        } else if(EVENT_APPLOGIN_RT.equals(name)) {
            handleAppLoginSuccess(event);
        }
    }

    @Override
    protected void handleFail(Event event) {
        super.handleFail(event);

        String name = event.name();
        if(EVENT_LOGIN_ERR.equals(name)) {
            btnLogin.setLoading(false);
        } else if(EVENT_APPLOGIN_ERR.equals(name)) {
            handleAppLoginFail(event);
        }
    }

}
