package me.codetalk.model;

import android.content.Context;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.api.ApiUtils;
import me.codetalk.api.FileUploadApi;
import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.util.FileUtils;
import me.codetalk.util.HttpUtils;
import okhttp3.MultipartBody;

import static me.codetalk.flowapp.AppConstants.FIELD_FILE;

/**
 * Created by guobxu on 2018/1/6.
 */

public class FileUploadApiModel extends BaseApiModel {

    private FileUploadApi fileUploadApi = ApiUtils.getRestApi(FileUploadApi.class);

    private static FileUploadApiModel INSTANCE = null;

    private FileUploadApiModel(Context appContext) {
        super(appContext);
    }

    public static FileUploadApiModel getInstance(Context appContext) {
        if(INSTANCE == null) {
            INSTANCE = new FileUploadApiModel(appContext);
        }

        return INSTANCE;
    }

    public void uploadByPath(String filePath, String type) {
        upload(new File(filePath), type);
    }

    public void upload(File file, String type) {
        List<File> files = Arrays.asList(new File[] {file});
        upload(files, type);
    }

    public void uploadByPath(List<String> filePaths, String type) {
        List<File> files = FileUtils.pathsToFiles(filePaths);
        upload(files, type);
    }

    public void upload(List<File> files, String type) {
        List<MultipartBody.Part> fileParts = HttpUtils.createProgressMultiPart(type, files, FIELD_FILE);

        fileUploadApi.fileupload(fileParts).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(rt -> {
                    if(ApiUtils.isSuccess(rt)) {
                        List<String> fileUrlList = (List<String>)rt.getRetData();

                        Event e = Event.builder().name(AppConstants.EVENT_FILEUPLOAD_RT)
                                .extra1(type).extra2(fileUrlList).build();
                        EventBus.publish(e);
                    } else {
                        handleApiFailure(rt.getRetMsg(), AppConstants.EVENT_FILEUPLOAD_ERR, type);
                    }
                }, throwable -> {
                    handleApiErr(throwable, AppConstants.EVENT_FILEUPLOAD_ERR, type);
                });
    }

}
