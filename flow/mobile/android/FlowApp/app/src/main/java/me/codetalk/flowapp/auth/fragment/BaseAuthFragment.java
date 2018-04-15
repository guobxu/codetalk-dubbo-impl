package me.codetalk.flowapp.auth.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.codetalk.auth.AuthUtils;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.R;
import me.codetalk.auth.api.request.LoginBody;
import me.codetalk.auth.api.response.LoginAuthRet;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.auth.model.AuthApiModel;
import me.codetalk.auth.model.entity.AuthInfo;
import me.codetalk.flowapp.auth.api.request.AppLoginBody;
import me.codetalk.auth.model.AppAuthApiModel;
import me.codetalk.flowapp.fragment.BaseFragment;
import me.codetalk.util.StringUtils;

import static me.codetalk.flowapp.AppConstants.EVENT_LOGIN_SUCCESS;

/**
 * Created by guobxu on 2017/12/29.
 */

public class BaseAuthFragment extends BaseFragment {

    protected AuthApiModel authApiModel = null;
    protected AppAuthApiModel appAuthApiModel = null;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Context appContext = mContext.getApplicationContext();
        authApiModel = AuthApiModel.getInstance(appContext);
        appAuthApiModel = AppAuthApiModel.getInstance(appContext);
    }

    protected void login(String userLogin, String passwd) {
        LoginBody loginBody = new LoginBody();
        loginBody.setAppId(AppConstants.APP_ID);
        loginBody.setUserLogin(userLogin);

        String clientKey = StringUtils.randomKey32();
        try {
            loginBody.setLoginAuthStr(AuthUtils.cipherLoginAuth(userLogin, passwd, clientKey));
        } catch(Exception ex) {
            ex.printStackTrace();

            toast(R.string.err_cipher_error);

            return;
        }

        authApiModel.login(loginBody, clientKey);
    }

    protected void handleAuthSuccess(Event e) {
        LoginAuthRet authRet = (LoginAuthRet)e.extra1();
        Long userId = authRet.getUserId();
        String authRetStr = authRet.getAuthRet(), svcTicket = authRet.getSvcTicket(),
                userLogin = (String)e.extra2(), clientKey = (String)e.extra3();

        // Auth Ret
        // Base64(AES("32Byte传输密钥+登录名+Token", KEY("32Byte随机密钥")))
        String authRetClear = null;
        try {
            authRetClear = AuthUtils.decipher(authRetStr, clientKey);
        } catch(Exception ex) {
            ex.printStackTrace();

            toast(R.string.err_login_failed);
            return;
        }

        String transportKey = authRetClear.substring(0, 32),
                userLoginRt = authRetClear.substring(32, userLogin.length() + 32),
                accessToken = authRetClear.substring(userLogin.length() + 32);

        if(!userLogin.equals(userLoginRt) || accessToken.length() != 36) {
            toast(R.string.err_login_failed);

            return;
        }

        // Auth Info
        AuthInfo authInfo = new AuthInfo();
        authInfo.setUserId(userId);
        authInfo.setUserLogin(userLogin);
        authInfo.setAccessToken(accessToken);
        authInfo.setTransportKey(transportKey);

        Long timeMillis = System.currentTimeMillis();
        authInfo.setLoginDate(timeMillis);

        String auth = authInfo.getAuth();
        if(auth == null) {
            toast(R.string.err_login_failed);
            return;
        }

        getApplication().setAuthInfo(authInfo);

        // 服务登录 0113
        AppLoginBody appLoginBody = new AppLoginBody();
        appLoginBody.setUserId(userId);
        appLoginBody.setAuthStr(authInfo.getAuthStr());
        appLoginBody.setSvcTicket(svcTicket);

        appAuthApiModel.appLogin(appLoginBody);
    }

    protected void handleAppLoginSuccess(Event e) {
        Long userId = (Long)e.extra1();
        authApiModel.userInfo(userId, userInfo -> {
            getApplication().putUserInfo(userInfo);
        });

        EventBus.publish(Event.name(EVENT_LOGIN_SUCCESS));
    }

    protected void handleAppLoginFail(Event e) {
        getApplication().clearAuthInfo();
    }

}
