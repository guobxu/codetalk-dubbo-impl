package me.codetalk.auth.api;

import io.reactivex.Observable;
import me.codetalk.auth.api.request.LoginBody;
import me.codetalk.auth.api.request.SignupBody;
import me.codetalk.api.response.BaseResponse;
import me.codetalk.auth.api.request.UserInfoBody;
import me.codetalk.auth.api.response.LoginResponse;
import me.codetalk.auth.api.response.SignupSessionResponse;
import me.codetalk.auth.api.response.UserInfoResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by guobxu on 2017/12/23.
 */

public interface AuthApi {

    @GET("/auth/signup/keycode")
    Observable<SignupSessionResponse> signupSession();

    @POST("/auth/signup")
    Observable<BaseResponse> signup(@Body SignupBody signupInfo);

    @POST("/auth/login")
    Observable<LoginResponse> login(@Body LoginBody loginBody);

    @GET("/auth/user")
    Observable<UserInfoResponse> userinfo(@Query("user_id_get") Long userId);

    @POST("/auth/user/update")
    Observable<BaseResponse> updateUserInfo(@Body UserInfoBody userInfo);

}




