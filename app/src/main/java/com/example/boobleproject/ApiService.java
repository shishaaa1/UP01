package com.example.boobleproject;

import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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



    @FormUrlEncoded
    @POST("api/UserController/LoginUsers")
    Call<Integer> loginUser(
            @Field("login") String login,
            @Field("password") String password
    );

    @GET("api/UserController/GetUsersAndPhoto")
    Call<Profile> getUserWithPhoto(@Query("id") int id);
    @GET("api/PhotoController/GetPhotoByUser")
    Call<ResponseBody> getPhotoByUser(@Query("userId") int userId);

}
