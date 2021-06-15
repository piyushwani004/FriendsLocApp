package com.piyush004.friendslocapp.Home.Fragments.Chat.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAxz3ImZI:APA91bF2lOJ-hH2GBV4PpjWhaMN3BtqHzLNiCUYrEGKMgV4TiBmqVIW3Bj_u6SQM6_TIgxrEYh5XBXjTGbi6vLW98V5b3EqIjVWN0X7Qur52m0VzwQqkqu9ejirmwkNLtfKJbLTfszRU"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

