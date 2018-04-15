package me.codetalk.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import me.codetalk.flowapp.FlowApplication;

/**
 * Created by guobxu on 2018/1/10.
 */

public final class ImageUtils {

    /**
     *
     * @param src       source bitmap
     * @param cropRatio crop ratio
     * @param radius    corner radius
     * @return
     */
    public static RoundedBitmapDrawable getCroppedRoundedBitmap(Bitmap src, float cropRatio, float radius) {
        // crop
        Resources res = FlowApplication.getInstance().getApplicationContext().getResources();
        int w, h, x, y;
        int srcw = src.getWidth(), srch = src.getHeight();
        w = Math.round( (srcw / srch > cropRatio) ? srch * cropRatio : srcw );
        h = Math.round( (srcw / srch > cropRatio) ? srch : srcw / cropRatio);
        x = (srcw - w) / 2;
        y = (srch - h) / 2;

        Bitmap cropped = Bitmap.createBitmap(src, x, y, w, h);

        // corner
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, cropped);
        dr.setCornerRadius(w / 8);
        dr.setAntiAlias(true);

        return dr;
    }

}
