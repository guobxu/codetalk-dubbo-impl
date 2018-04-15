package me.codetalk.flowapp.fnd.model;

import android.content.Context;

import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.api.ApiUtils;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.fnd.api.UserTagApi;
import me.codetalk.flowapp.fnd.api.request.UserTagAddBody;
import me.codetalk.flowapp.fnd.api.request.UserTagUpdateBody;
import me.codetalk.model.BaseApiModel;

/**
 * Created by guobxu on 2018/1/17.
 */

public class UserTagApiModel extends BaseApiModel {

    private UserTagApi userTagApi = ApiUtils.getRestApi(UserTagApi.class);

    private static UserTagApiModel INSTANCE = null;

    public static UserTagApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new UserTagApiModel(appContext);
        }

        return INSTANCE;
    }

    private UserTagApiModel(Context context) {
        super(context);
    }

    public void userTag() {
        userTagApi.userTag().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_USERTAG_RT).extra1(rt.getTags()).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_USERTAG_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_USERTAG_ERR);
        });
    }

    public void userTagUpdate(Set<String> tags) {
        UserTagUpdateBody body = new UserTagUpdateBody();
        body.setTags(tags);

        userTagApi.userTagUpdate(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_USERTAG_UPDATE_RT).extra1(tags).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_USERTAG_UPDATE_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_USERTAG_UPDATE_ERR);
        });
    }

    public void userTagAdd(String text) {
        UserTagAddBody body = new UserTagAddBody();
        body.setTag(text);

        userTagApi.userTagAdd(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                EventBus.publish(Event.name(AppConstants.EVENT_USERTAG_ADD_RT));
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_USERTAG_ADD_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_USERTAG_ADD_ERR);
        });
    }



}
