package me.codetalk.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.codetalk.api.request.ProgressRequestBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by guobxu on 2018/1/10.
 */

public final class HttpUtils {

    public static List<MultipartBody.Part> createProgressMultiPartByPath(String uploadType, List<String> filePaths, String field) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for(String filePath : filePaths) {
            parts.add(createProgressMultiPartByPath(uploadType, filePath, field));
        }

        return parts;
    }

    public static List<MultipartBody.Part> createProgressMultiPart(String uploadType, List<File> files, String field) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for(File file : files) {
            parts.add(createProgressMultiPart(uploadType, file, field));
        }

        return parts;
    }

    public static MultipartBody.Part createProgressMultiPartByPath(String uploadType, String filePath, String field) {
        return createProgressMultiPart(uploadType, new File(filePath), field);
    }

    public static MultipartBody.Part createProgressMultiPart(String uploadType, File file, String field) {
//        String mimeType = FileUtils.getMimeType(filePath);
//        RequestBody requestFile = RequestBody.create(mimeType == null ? null : MediaType.parse(mimeType), file);
        RequestBody requestFile = new ProgressRequestBody(file, uploadType);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(field, file.getName(), requestFile);

        return filePart;
    }



}
