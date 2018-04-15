package me.codetalk.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import me.codetalk.flowapp.FlowApplication;

/**
 * Created by guobxu on 2018/1/11.
 */

public final class ViewUtils {

    public static List<View> getViewsByTag(ViewGroup root, String tag){
        List<View> views = new ArrayList<View>();

        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }

        return views;
    }

    public static View getLastViewByTag(ViewGroup root, String tag) {
        List<View> views = getViewsByTag(root, tag);

        int size = views.size();
        return size > 0 ? views.get(size - 1) : null;
    }

    public static View getFirstViewByTag(ViewGroup root, String tag) {
        List<View> views = getViewsByTag(root, tag);

        int size = views.size();
        return size > 0 ? views.get(0) : null;
    }

    public static boolean isEmpty(EditText editText) {
        String s = editText.getText().toString().trim();

        return s.length() == 0;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static int getColor(int colorResId) {
        Context context = FlowApplication.getInstance().getApplicationContext();

        return context.getResources().getColor(colorResId);
    }

    public static int getColor(Context context, int colorResId) {
        return context.getResources().getColor(colorResId);
    }

    public static void loadImageUrl(Context context, ImageView imgView, String imgUrl) {
        Glide.with(context)
                .load(imgUrl)
                .apply(new RequestOptions().dontAnimate())
                .into(imgView);
    }

}
