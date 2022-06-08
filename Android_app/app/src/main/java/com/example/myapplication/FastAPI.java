package com.example.myapplication;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FastAPI {
    //@Headers({"accept: application/json", "Content-Type: multipart/form-data"})
    @POST("file-to-number")
    Call<ResponseBody> upload(
            @Body MultipartBody.Part files
    );


}
