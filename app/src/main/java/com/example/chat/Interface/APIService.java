package com.example.chat.Interface;

import com.example.chat.Notification.MyResponse;
import com.example.chat.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {


    @Headers(
        {
            "Content-Type:application/json",
                "Authorization:key=AAAAs6HOtxo:APA91bEtONgHx7bsPbqh7oxsJ_zCy2EqNxav6upjuZUENhb92EOLFSYg7BMmPz5V4wVU0KKSyijU6lfNx4Y_Qoly2gCG49THVeXOQsSr15RB7GWzmPQiL2dWX3bmNHg9y9hiIX_HV5oK"
        }
    )

    @POST("fcm/send")
        Call<MyResponse> sendNotification(@Body Sender body);

}
