package com.example.boobleproject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("api/UserController/AddUsers")
    Call<Void> registerUser(
            @Field("FirstName") String firstName,
            @Field("LastName") String lastName,
            @Field("Birthday") String birthday, // формат "yyyy-MM-dd"
            @Field("BIO") String bio,
            @Field("Sex") boolean sex,
            @Field("Login") String login,
            @Field("Password") String password
    );
}
