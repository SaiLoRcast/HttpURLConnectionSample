package com.polygalov.retrofit2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Константин on 16.01.2018.
 */

public interface restAPIforUsers {

    @GET("users/{user}")
    Call<List<RetrofitModel>> loadUsers(@Path("user") String user);
}
