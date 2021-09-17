package com.szymonlukiewicz.polskieepatnociserver;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GIOSService extends Service {

    private static String ACTION_GET_LIST = "get_station_list";

    private static Messenger replyMessenger = null;

    static class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            super.handleMessage(message);

            replyMessenger = message.replyTo;
            final String action = message.getData().getString("action");

            final OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();

            assert action != null;
            if (action.equals(ACTION_GET_LIST)) {
                final Request request = new Request.Builder()
                        .url("https://api.gios.gov.pl/pjp-api/rest/station/findAll")
                        .method("GET", null)
                        .build();
                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            Message replyMessage = Message.obtain(IncomingHandler.this);
                            Bundle bundle = new Bundle();
                            bundle.putString("executedAction", action);
                            bundle.putString("response", response.body().string());
                            replyMessage.setData(bundle);
                            replyMessenger.send(replyMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                final Request request = new Request.Builder()
                        .url("https://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + message.getData().getInt("stationID"))
                        .method("GET", null)
                        .build();
                client.newCall(request).enqueue(new okhttp3.Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {

                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        try {
                            Message replyMessage = Message.obtain(IncomingHandler.this);
                            Bundle bundle = new Bundle();
                            bundle.putString("executedAction", action);
                            bundle.putString("response", response.body().string());
                            replyMessage.setData(bundle);
                            replyMessenger.send(replyMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    Messenger messenger;


    @Override
    public IBinder onBind(Intent intent) {
        messenger = new Messenger(new IncomingHandler());
        return messenger.getBinder();
    }
}