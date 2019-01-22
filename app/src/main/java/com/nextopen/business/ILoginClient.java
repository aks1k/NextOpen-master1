package com.nextopen.business;

import com.nextopen.vo.LoginRequestVO;
import com.nextopen.vo.LoginResponseVO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ILoginClient {
    @Headers({"Content-Type: application/json"})
    @POST("firsttimelogin")
    Call<LoginResponseVO> login(@Body LoginRequestVO requestVO);
}
