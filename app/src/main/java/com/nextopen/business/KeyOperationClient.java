package com.nextopen.business;

import android.util.Log;

import com.nextopen.common.util.IContants;
import com.nextopen.vo.KeyOperationRequestVO;
import com.nextopen.vo.KeyOperationResponseVO;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KeyOperationClient implements IContants{
    private OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private Retrofit.Builder builder;
    private Retrofit retrofit;
    private KeyOperationResponseVO keyOperationResponseVO;

    public void manageRequest(KeyOperationRequestVO requestVO) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        IKeyOperationClient service = retrofit.create(IKeyOperationClient.class);
        //Call<KeyOperationResponseVO> callSync = service.keyOperation(requestVO);
        Call<ResponseBody> callSync = service.keyOperation(requestVO);

        try {
            //Response<KeyOperationResponseVO> response = callSync.execute();
            //responseVO = response.body();
            Response response = callSync.execute();
            keyOperationResponseVO = new KeyOperationResponseVO();
            keyOperationResponseVO.setResponseCode(response.code());
            Log.d("Response", "Response Code:"+keyOperationResponseVO.getResponseCode());
        } catch (Exception ex) {
            Log.d("Retro Error", ex.getLocalizedMessage());
        }
    }

    public KeyOperationResponseVO getKeyOperationResponseVO() {
        return keyOperationResponseVO;
    }

    public void setKeyOperationResponseVO(KeyOperationResponseVO keyOperationResponseVO) {
        this.keyOperationResponseVO = keyOperationResponseVO;
    }
}
