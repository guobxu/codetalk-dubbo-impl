package me.codetalk.flowapp.fnd.model;

import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.api.ApiUtils;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.fnd.api.FollowApi;
import me.codetalk.flowapp.fnd.api.request.FollowBody;
import me.codetalk.flowapp.fnd.model.entity.FollowStat;
import me.codetalk.model.BaseApiModel;

/**
 * Created by guobxu on 2018/1/24.
 */

public class FollowApiModel extends BaseApiModel {

    private FollowApi followApi = ApiUtils.getRestApi(FollowApi.class);

    private static FollowApiModel INSTANCE = null;

    public static FollowApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new FollowApiModel(appContext);
        }

        return INSTANCE;
    }

    private FollowApiModel(Context context) {
        super(context);
    }

    public void follow(Long userFollow, int action, Consumer<FollowStat> consumer) {
        FollowBody body = new FollowBody();
        body.setUserFollow(userFollow);
        body.setAction(action);

        followApi.follow(body).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                consumer.accept(rt.getStat());
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_USER_FOLLOW_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_USER_FOLLOW_ERR);
        });
    }

    public void existFollow(Long userFollow, Consumer<Boolean> consumer) {
        followApi.existFollow(userFollow).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                Integer existFollow = (Integer)rt.getRetData();

                consumer.accept(existFollow == 1);
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_USER_EXIST_FOLLOW_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_USER_EXIST_FOLLOW_ERR);
        });
    }

    public void count(Long userId, Consumer<FollowStat> consumer) {
        followApi.count(userId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(rt -> {
            if(ApiUtils.isSuccess(rt)) {
                consumer.accept(rt.getStat());
            } else {
                handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_USER_COUNT_FOLLOW_ERR);
            }
        }, throwable -> {
            handleApiErr(throwable, AppConstants.EVENT_USER_COUNT_FOLLOW_ERR);
        });
    }

}
