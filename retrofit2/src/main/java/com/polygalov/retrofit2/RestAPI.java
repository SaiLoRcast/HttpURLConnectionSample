package com.polygalov.retrofit2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Константин on 16.01.2018.
 */

public interface RestAPI {

    @GET("users")
    Call<List<RetrofitModel>> loadUsers();
}
