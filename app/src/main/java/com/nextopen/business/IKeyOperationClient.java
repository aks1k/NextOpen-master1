package com.nextopen.business;

import com.nextopen.vo.KeyOperationRequestVO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IKeyOperationClient {
    @Headers({"Content-Type: application/json"})
    @POST("keyoperation")
    //Call<KeyOperationResponseVO> keyOperation(@Body KeyOperationRequestVO requestVO);
    Call<ResponseBody> keyOperation(@Body KeyOperationRequestVO keyOperationRequestVO);
}
