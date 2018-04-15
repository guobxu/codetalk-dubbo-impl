package me.codetalk.flowapp.auth.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.transition.Fade;

import java.io.UnsupportedEncodingException;

import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.R;
import me.codetalk.event.Event;
import me.codetalk.flowapp.activity.BaseActivity;
import me.codetalk.flowapp.auth.fragment.LoginFragment;
import me.codetalk.flowapp.auth.fragment.SignupFragment;

import static me.codetalk.flowapp.AppConstants.EVENT_GO_LOGIN;
import static me.codetalk.flowapp.AppConstants.EVENT_GO_SIGNUP;
import static me.codetalk.flowapp.AppConstants.EVENT_LOGIN_SUCCESS;

@EventSupport(unregisterWhenStop = false)
public class AuthActivity extends BaseActivity {

    private FragmentManager mFragmentManager;

    private static final long FADE_DEFAULT_TIME = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mFragmentManager = getSupportFragmentManager();

        loadInitialFragment();

        // hide actionbar
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
    }

    private void loadInitialFragment() {
        Fragment initialFragment = LoginFragment.newInstance();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.auth_fragment_container, initialFragment);
        fragmentTransaction.commit();
    }

    private void performTransition() {
        Fragment prevFragment = mFragmentManager.findFragmentById(R.id.auth_fragment_container);
        Fragment nextFragment = null;
        if(prevFragment instanceof LoginFragment) {
            nextFragment = SignupFragment.newInstance();
        } else {
            nextFragment = LoginFragment.newInstance();
        }

        // txn
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

        // 1. exit previous
        Fade exitFade = new Fade();
        exitFade.setDuration(FADE_DEFAULT_TIME);
        prevFragment.setExitTransition(exitFade);

        // 2. Enter transition for new fragment
        Fade enterFade = new Fade();
        enterFade.setStartDelay(FADE_DEFAULT_TIME);
        enterFade.setDuration(FADE_DEFAULT_TIME);
        nextFragment.setEnterTransition(enterFade);

        fragmentTransaction.replace(R.id.auth_fragment_container, nextFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    // Event Bus
    @Override
    protected void handleSuccess(Event event) {
        String name = event.name();
        if(EVENT_GO_LOGIN.equals(name) || EVENT_GO_SIGNUP.equals(name)) {
            performTransition();
        } else if(EVENT_LOGIN_SUCCESS.equals(name)) {
            finish();
        }
    }


}
