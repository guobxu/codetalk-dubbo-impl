package me.codetalk.auth.model;

import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.api.ApiUtils;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.auth.api.AppAuthApi;
import me.codetalk.flowapp.auth.api.request.AppLoginBody;
import me.codetalk.model.BaseApiModel;

/**
 * Created by guobxu on 2018/1/13.
 */

public class AppAuthApiModel extends BaseApiModel {

    private AppAuthApi appAuthApi = ApiUtils.getRestApi(AppAuthApi.class);

    private static AppAuthApiModel INSTANCE = null;

    private AppAuthApiModel(Context appContext) {
        super(appContext);
    }

    public static AppAuthApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new AppAuthApiModel(appContext);
        }

        return INSTANCE;
    }

    public void appLogin(AppLoginBody appLoginBody) {
        appAuthApi.appLogin(appLoginBody).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_APPLOGIN_RT).extra1(appLoginBody.getUserId()).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_APPLOGIN_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_APPLOGIN_ERR);
        });
    }



}
