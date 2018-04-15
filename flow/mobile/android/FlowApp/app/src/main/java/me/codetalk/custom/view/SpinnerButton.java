package me.codetalk.custom.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import me.codetalk.flowapp.R;

/**
 * Created by guobxu on 1/1/2018.
 */

public class SpinnerButton extends RelativeLayout {

    private static final String TAG = "SpinnerButton";

    private String btnText;

    private Button actionButton;
    private ProgressBar spinner;

    private boolean loading = false;

    public SpinnerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public SpinnerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.spinner_button, this);

        actionButton = findViewById(R.id.spinner_btn_action);
        spinner = findViewById(R.id.spinner_loading);

        TypedArray arr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpinnerButton, 0, 0);
        try {
            Drawable background = arr.getDrawable(R.styleable.SpinnerButton_actionButtonBackground);
            if(background != null) actionButton.setBackground(background);

            btnText = arr.getString(R.styleable.SpinnerButton_actionButtonText);
            actionButton.setText(btnText);

            int paddingLeft = arr.getDimensionPixelSize(R.styleable.SpinnerButton_actionButtonPaddingLeft, -1),
                paddingRight = arr.getDimensionPixelSize(R.styleable.SpinnerButton_actionButtonPaddingRight, -1),
                paddingTop = arr.getDimensionPixelSize(R.styleable.SpinnerButton_actionButtonPaddingTop, -1),
                paddingBottom = arr.getDimensionPixelSize(R.styleable.SpinnerButton_actionButtonPaddingBottom, -1);
            actionButton.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

            int textSize = arr.getDimensionPixelSize(R.styleable.SpinnerButton_actionButtonTextSize, -1);
            if(textSize != -1) actionButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

            ColorStateList textColors = arr.getColorStateList(R.styleable.SpinnerButton_actionButtonTextColor);
            if(textColors != null) actionButton.setTextColor(textColors);
        } finally {
            arr.recycle();
        }
    }

    public Button getActionButton() {
        return actionButton;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
//        Log.d(TAG, "Button = " + btnText + (loading ? " loading" : "disabled!"));

        this.loading = loading;

        if(loading) {
//            actionButton.setEnabled(false);
//            actionButton.setText("");
            actionButton.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        } else {
//            actionButton.setEnabled(true);
//            actionButton.setText(btnText);
            actionButton.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        actionButton.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        spinner.setVisibility(View.GONE);
        actionButton.setEnabled(enabled);
    }

}
