package me.codetalk.flowapp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;

import io.reactivex.disposables.Disposable;
import me.codetalk.annotation.ActivityFeature;
import me.codetalk.annotation.EventSupport;
import me.codetalk.flowapp.FlowApplication;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.auth.model.AuthPrefModel;
import me.codetalk.model.PrefModel;

/**
 * Created by guobxu on 2017/12/29.
 */

public class BaseFragment extends Fragment implements Validator.ValidationListener {

    protected Context mContext;
    protected PrefModel prefModel;
    protected AuthPrefModel authModel;

    protected Disposable subscription;

    protected Validator validator = null;
    protected boolean validated;

    // event
    private boolean enableEvent = false;
    private boolean unregisterWhenStop = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // event
        EventSupport eventSupport = getClass().getAnnotation(EventSupport.class);
        if(eventSupport != null) {
            enableEvent = true;
            unregisterWhenStop = eventSupport.unregisterWhenStop();
        }

        if(enableEvent) {
            registerEventBus();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Context context = getContext();
        this.mContext = context;

        Context appContext = context.getApplicationContext();
        prefModel = PrefModel.getInstance(appContext);
        authModel = AuthPrefModel.getInstance(appContext);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(enableEvent && subscription == null) {
            registerEventBus();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if(enableEvent && unregisterWhenStop) {
            unregisterEventBus();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterEventBus();
    }

    protected LinearLayoutManager setDefaultLayout(RecyclerView recyclerView, boolean withDivider) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        if(withDivider) {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        return layoutManager;
    }

    /**
     * @param msg
     */
    protected void toast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param msgResId
     */
    protected void toast(int msgResId) {
        Toast.makeText(mContext, mContext.getString(msgResId), Toast.LENGTH_SHORT).show();
    }

    // Application
    protected FlowApplication getApplication() {
        return (FlowApplication)getActivity().getApplication();
    }

    protected Context getAppContext() {
        return getApplication().getApplicationContext();
    }

    // Validator
    @Override
    public void onValidationSucceeded() {
        validated = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        validated = false;

        for(ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(mContext);
            if(view instanceof EditText) {
                ((EditText)view).setError(message);
            } else {
                toast(message);
            }
        }
    }

    // Event Bus
    /**
     * @param event
     */
    protected void handleEvent(Event event) {
        if (event.success()) {
            handleSuccess(event);
        } else {
            handleFail(event);
        }
    }

    /**
     * Handle fail or error event
     *
     * @param e
     */
    protected void handleFail(Event e) {
        Integer errKey = e.errkey();
        String errMsg = e.errmsg();

        if (errKey != null) {
            toast(errKey);
        } else if (errMsg != null) {
            toast(errMsg);
        }
    }

    protected void handleSuccess(Event event) {
        throw new RuntimeException("Not implemented");
    }

    // Event Bus
    protected void registerEventBus() {
        subscription = EventBus.subscribe(event -> {
            handleEvent(event);
        });
    }

    protected void unregisterEventBus() {
        if (subscription != null) {
            subscription.dispose();

            subscription = null;
        }
    }

}