package me.codetalk.flowapp.post.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import io.reactivex.functions.Action;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.flowapp.search.activity.SearchActivity;

/**
 * Created by guobxu on 2018/1/26.
 */

public class TagSearchSpan extends ClickableSpan {

    private Context context;

    private String tag; // with '#'

    private boolean mIsPressed;
    private int mPressedBackgroundColor;
    private int mNormalTextColor;
    private int mPressedTextColor;

    public TagSearchSpan(String tag,
                   int normalTextColor,
                   int pressedTextColor,
                   int pressedBackgroundColor,
                   Context context) {
        this.tag = tag;

        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mPressedBackgroundColor = pressedBackgroundColor;

        this.context = context;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("search_text", AppConstants.CHAR_HASHTAG + tag);

        context.startActivity(intent);
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;

        ds.setUnderlineText(false);
    }
}