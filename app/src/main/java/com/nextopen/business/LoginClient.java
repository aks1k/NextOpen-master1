package com.nextopen.business;

import android.util.Log;

import com.nextopen.common.util.IContants;
import com.nextopen.vo.LoginRequestVO;
import com.nextopen.vo.LoginResponseVO;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginClient implements IContants{
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private Retrofit.Builder builder;
    private Retrofit retrofit;
    private LoginResponseVO loginResponseVO;

    public void manageRequest(LoginRequestVO requestVO) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        ILoginClient service = retrofit.create(ILoginClient.class);
        Call<LoginResponseVO> callSync = service.login(requestVO);

        try {
            Response<LoginResponseVO> response = callSync.execute();
            loginResponseVO = response.body();
//            callSync.enqueue(new Callback<LoginResponseVO>() {
//                @Override
//                public void onResponse(Call<LoginResponseVO> call, Response<LoginResponseVO> response) {
//                    loginResponseVO = response.body();
//                }
//
//                @Override
//                public void onFailure(Call<LoginResponseVO> call, Throwable t) {
//                    Log.d("Tag"," Failed: ");
//                }
//            });
//            //Log.d("Response", loginResponseVO.getMobileCountry());
        } catch (Exception ex) {
            Log.d("Retro Error", ex.getLocalizedMessage());
        }
    }


/*
    public void manageRequest(LoginRequestVO requestVO) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        ILoginClient service = retrofit.create(ILoginClient.class);
        Call<LoginResponseVO> callSync = service.login(requestVO);

        try {
            Response<LoginResponseVO> response = callSync.execute();
            loginResponseVO = response.body();
            Log.d("Response", loginResponseVO.getMobileCountry());
        } catch (Exception ex) {
            Log.d("Retro Error", ex.getLocalizedMessage());
        }
    }
*/
    public LoginResponseVO getLoginResponseVO() {
        return loginResponseVO;
    }

    public void setLoginResponseVO(LoginResponseVO loginResponseVO) {
        this.loginResponseVO = loginResponseVO;
    }
}
