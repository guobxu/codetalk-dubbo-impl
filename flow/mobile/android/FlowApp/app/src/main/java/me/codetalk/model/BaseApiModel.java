package me.codetalk.model;

import android.bluetooth.BluetoothHealthAppConfiguration;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.net.SocketTimeoutException;

import me.codetalk.flowapp.R;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import retrofit2.HttpException;

/**
 * Created by guobxu on 2017/12/28.
 */

public abstract class BaseApiModel {

    protected Context appContext;

    public BaseApiModel(Context context) {
        this.appContext = appContext;
    }

    /**
     * Rest Api error handler
     *
     * @param throwable
     * @param event
     */
    protected void handleApiErr(Throwable throwable, String event, Object... extras) {
        throwable.printStackTrace();

        Event.Builder builder = Event.builder().name(event);
        if(throwable instanceof HttpException || throwable instanceof SocketTimeoutException) {
            builder.errkey(R.string.error_network);
        } else {
            builder.errkey(R.string.error_unknow);
        }

        if(extras != null) {
            int len = extras.length;
            if(len == 3) {
                builder.extra1(extras[0]);
                builder.extra2(extras[1]);
                builder.extra3(extras[2]);
            } else if(len == 2) {
                builder.extra1(extras[0]);
                builder.extra2(extras[1]);
            } else if(len == 1) {
                builder.extra1(extras[0]);
            }
        }

        EventBus.publish(builder.build());
    }

    protected void handleApiErr(Throwable throwable, String event) {
        handleApiErr(throwable, event, (Object)null);
    }

    /**
     * Rest Api failure
     *
     * @param retMsg
     * @param event
     */
    protected void handleApiFailure(String retMsg, String event, Object... extras) {
        Event.Builder builder = Event.builder().name(event).errmsg(retMsg);

        if(extras != null) {
            int len = extras.length;
            if(len == 3) {
                builder.extra1(extras[0]);
                builder.extra2(extras[1]);
                builder.extra3(extras[2]);
            } else if(len == 2) {
                builder.extra1(extras[0]);
                builder.extra2(extras[1]);
            } else if(len == 1) {
                builder.extra1(extras[0]);
            }
        }

        EventBus.publish(builder.build());
    }

    protected void handleApiFailure(String retMsg, String event) {
        handleApiFailure(retMsg, event, (Object)null);
    }

}
