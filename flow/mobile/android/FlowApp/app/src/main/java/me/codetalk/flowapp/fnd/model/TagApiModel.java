package me.codetalk.flowapp.fnd.model;

import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.api.ApiUtils;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.fnd.api.TagApi;
import me.codetalk.model.BaseApiModel;

/**
 * Created by guobxu on 2018/1/17.
 */

public class TagApiModel extends BaseApiModel {

    private TagApi tagApi = ApiUtils.getRestApi(TagApi.class);

    private static TagApiModel INSTANCE = null;

    public static TagApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new TagApiModel(appContext);
        }

        return INSTANCE;
    }

    private TagApiModel(Context context) {
        super(context);
    }

    public void tagList(String q, int count) {
        tagApi.tagList(q, count).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_TAG_LIST_RT).extra1(rt.getTags()).extra2(q).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_TAG_LIST_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_TAG_LIST_ERR);
        });
    }

    public void topByDay(int count) {
        tagApi.topByDay(count).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Event e = Event.builder().name(AppConstants.EVENT_TAG_TOPBYDAY_RT).extra1(rt.getTags()).build();
                EventBus.publish(e);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_TAG_TOPBYDAY_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_TAG_TOPBYDAY_ERR);
        });
    }



}
