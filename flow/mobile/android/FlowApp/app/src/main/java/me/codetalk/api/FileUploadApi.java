package me.codetalk.api;

import java.util.List;

import io.reactivex.Observable;
import me.codetalk.api.response.ObjectDataResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by guobxu on 2018/1/6.
 */

public interface FileUploadApi {


    @Multipart
    @PUT("/upload")
    Observable<ObjectDataResponse> fileupload(@Part List<MultipartBody.Part> files);


}
