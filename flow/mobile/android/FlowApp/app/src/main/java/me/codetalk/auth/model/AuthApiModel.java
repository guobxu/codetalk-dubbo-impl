package me.codetalk.auth.model;

import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.auth.AuthConstants;
import me.codetalk.auth.model.entity.UserInfo;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.api.ApiUtils;
import me.codetalk.auth.api.AuthApi;
import me.codetalk.auth.api.request.LoginBody;
import me.codetalk.auth.api.request.SignupBody;
import me.codetalk.auth.api.request.UserInfoBody;
import me.codetalk.auth.api.response.LoginAuthRet;
import me.codetalk.model.BaseApiModel;
import me.codetalk.auth.model.entity.SignupSession;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;

/**
 * Created by guobxu on 2017/12/28.
 */

public class AuthApiModel extends BaseApiModel {

    private AuthApi authApi = ApiUtils.getRestApi(AuthApi.class);

    private SignupSessionModel ssModel = null;

    private static AuthApiModel INSTANCE = null;

    private AuthApiModel(Context appContext) {
        super(appContext);
    }

    public static AuthApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new AuthApiModel(appContext);
            INSTANCE.ssModel = SignupSessionModel.getInstance(appContext);
        }

        return INSTANCE;
    }

    public void signupSession() {

        SignupSession ssess = ssModel.loadSignupSession();
        if(ssess.getId() != null && ssess.getCreateDate() > System.currentTimeMillis() - AuthConstants.SSESS_TIMEOUT) {
            Event e = Event.builder().name(AppConstants.EVENT_SSESS_RT).extra1(ssess).build();
            EventBus.publish(e);

            return;
        }

        authApi.signupSession().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(rt -> {
                    if(ApiUtils.isSuccess(rt)) {
                        SignupSession newSsess = rt.getSignupSession();
                        ssModel.setSignupSession(newSsess);

                        Event e = Event.builder().name(AppConstants.EVENT_SSESS_RT).extra1(newSsess).build();
                        EventBus.publish(e);
                    } else {
                        Event e = Event.builder().name(AppConstants.EVENT_SSESS_ERR).errmsg(rt.getRetMsg()).build();
                        EventBus.publish(e);
                    }
                }, throwable -> {
                    handleApiErr(throwable, AppConstants.EVENT_SSESS_ERR);
                });

    }

    public void signup(SignupBody body) {
        authApi.signup(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(rt -> {
                    if(ApiUtils.isSuccess(rt)) {
                        Event e = Event.builder().name(AppConstants.EVENT_SIGNUP_RT).extra1(body).build();
                        EventBus.publish(e);
                    } else {
                        Event e = Event.builder().name(AppConstants.EVENT_SIGNUP_ERR).errmsg(rt.getRetMsg()).build();
                        EventBus.publish(e);
                    }
                }, throwable -> {
                    handleApiErr(throwable, AppConstants.EVENT_SIGNUP_ERR);
                });
    }

    public void login(LoginBody body, String clientKey) {
        authApi.login(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(rt -> {
                    if(ApiUtils.isSuccess(rt)) {
                        LoginAuthRet loginRt = rt.getLoginAuthRet();

                        Event e = Event.builder().name(AppConstants.EVENT_LOGIN_RT).extra1(loginRt)
                                .extra2(body.getUserLogin()).extra3(clientKey).build();
                        EventBus.publish(e);
                    } else {
                        Event e = Event.builder().name(AppConstants.EVENT_LOGIN_ERR).errmsg(rt.getRetMsg()).build();
                        EventBus.publish(e);
                    }
                }, throwable -> {
                    handleApiErr(throwable, AppConstants.EVENT_LOGIN_ERR);
                });
    }

    public void userInfo(Long userId, Consumer<UserInfo> consumer) {
        authApi.userinfo(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(rt -> {
                    if(ApiUtils.isSuccess(rt)) {
                        consumer.accept(rt.getUserInfo());
                    } else {
                        Event e = Event.builder().name(AppConstants.EVENT_USERINFO_ERR).errmsg(rt.getRetMsg()).build();
                        EventBus.publish(e);
                    }
                }, throwable -> {
                    handleApiErr(throwable, AppConstants.EVENT_USERINFO_ERR);
                });
    }

    public void updateUserInfo(UserInfoBody userInfoBody) {
        authApi.updateUserInfo(userInfoBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(rt -> {
                    if(ApiUtils.isSuccess(rt)) {
                        EventBus.publish(Event.name(AppConstants.EVENT_USERINFO_UPDATE_RT));
                    } else {
                        Event e = Event.builder().name(AppConstants.EVENT_USERINFO_UPDATE_ERR).errmsg(rt.getRetMsg()).build();
                        EventBus.publish(e);
                    }
                }, throwable -> {
                    handleApiErr(throwable, AppConstants.EVENT_USERINFO_UPDATE_ERR);
                });
    }


}
