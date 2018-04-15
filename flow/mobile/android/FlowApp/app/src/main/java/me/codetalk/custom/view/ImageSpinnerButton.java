package me.codetalk.custom.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import me.codetalk.flowapp.R;

/**
 * Created by guobxu on 2018/1/10.
 */

public class ImageSpinnerButton extends FrameLayout {

    private static final String TAG = "ImageSpinnerButton";

    private int width;
    private int height;

    private int btnSrc;
    private Drawable btnBackground;

    private ImageButton imageButton;
    private ProgressBar spinner;

    private boolean loading = false;

    public ImageSpinnerButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public ImageSpinnerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.image_spinner_button, this);

        imageButton = findViewById(R.id.image_btn_action);

        TypedArray arr = context.obtainStyledAttributes(attrs, new int[]{
                android.R.attr.src, android.R.attr.background, android.R.attr.layout_width, android.R.attr.layout_height
        });
        try {
            btnSrc = arr.getResourceId(0, -1);
            btnBackground = arr.getDrawable(1);
            width = arr.getLayoutDimension(2, -1);
            height = arr.getLayoutDimension(3, -1);
        } finally {
            arr.recycle();
        }
        imageButton.setImageResource(btnSrc);
        imageButton.setBackground(btnBackground);

        spinner = findViewById(R.id.spinner_loading);
        spinner.setLayoutParams(new LayoutParams(width, height));
    }

    public ImageButton getImageButton() {
        return imageButton;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
//        Log.d(TAG, "Button = " + btnText + (enabled ? " enabled" : "disabled!"));

        this.loading = loading;

        if(loading) {
            imageButton.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
        } else {
            imageButton.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        imageButton.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        imageButton.setEnabled(enabled);
    }


}
