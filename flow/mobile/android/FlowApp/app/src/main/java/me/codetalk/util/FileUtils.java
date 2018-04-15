package me.codetalk.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.codetalk.flowapp.FlowApplication;
import me.codetalk.flowapp.R;
import okhttp3.MediaType;

/**
 * Created by guobxu on 2018/1/8.
 */

public final class FileUtils {


    /**
     * Content Uri to Real Path
     *
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Uri contentUri, Context context) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return null;
    }

    public static List<File> pathsToFiles(List<String> filePaths) {
        List<File> files = new ArrayList<>();
        for(String filePath : filePaths) {
            files.add(new File(filePath));
        }

        return files;
    }

}
