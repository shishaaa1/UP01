package com.example.boobleproject;

import java.util.Date;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
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
    @GET("api/UserController/GetOppositeSexUsers")
    Call<List<Profile>> getOppositeSexUsers(@Query("userId") int userId);
    @GET("api/UserController/GetUsersAndPhoto")
    Call<Profile> getUserWithPhoto(@Query("id") int id);
    @GET("api/PhotoController/GetPhotoByUser")
    Call<ResponseBody> getPhotoByUser(@Query("userId") int userId);


    @Multipart
    @PUT("api/UserController/UpdateUsers")
    Call<Void> updateUser(
            @Query("userId") int userId,
            @Part("FirstName") RequestBody firstName,
            @Part("LastName") RequestBody lastName,
            @Part("BIO") RequestBody bio
    );

    @GET("api/UserController/GetUserById")
    Call<Profile> getUserById(@Query("id") int id);

    @Multipart
    @POST("api/PhotoController/UploadPhoto")
    Call<ResponseBody> uploadPhoto(
            @Part("UserId") RequestBody userId,
            @Part MultipartBody.Part photoFile
    );
///
    @DELETE("api/PhotoController/DeletePhoto")
    Call<ResponseBody> deletePhoto(@Query("id") int photoId);

    @GET("api/UserController/GetUsers")
    Call<List<Profile>> getAllUsers();

    @GET("api/UserController/GetAllUsersWithPhoto")
    Call<List<Profile>> getAllUsersWithPhoto();
    @GET("api/PhotoController/GetPhotoByUsersId")
    Call<ResponseBody> getPhotoByUserId(@Query("userId") int userId);
    @GET("api/PhotoController/GetUserPhotoId")
    Call<Integer> getUserPhotoId(@Query("userId") int userId);
}
