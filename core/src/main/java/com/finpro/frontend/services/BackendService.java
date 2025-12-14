package com.finpro.frontend.services;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;


public class BackendService {
    private static final String BASE_URL = "http://localhost:8080/api";


    public interface RequestCallback {
        void onSuccess(String response);
        void onError(String error);
    }


    public void createPlayer(String username, RequestCallback callback) {
        String json = "{\"username\":\"" + username + "\"}";


        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/players")
            .header("Content-Type", "application/json")
            .content(json)
            .build();


        sendRequest(request, callback);
    }


    public void submitScore(String playerId, int scoreValue, int coins, int distance, RequestCallback callback) {
        String json = String.format(
            "{\"playerId\":\"%s\",\"value\":%d,\"coinsCollected\":%d,\"distanceTravelled\":%d}",
            playerId, scoreValue, coins, distance
        );


        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/scores")
            .header("Content-Type", "application/json")
            .content(json)
            .build();


        sendRequest(request, callback);
    }


    private void sendRequest(Net.HttpRequest request, RequestCallback callback) {
        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String result = httpResponse.getResultAsString();
                int statusCode = httpResponse.getStatus().getStatusCode();


                if (statusCode >= 200 && statusCode < 300) {
                    callback.onSuccess(result);
                } else {
                    Gdx.app.error("BackendService", "Error Code: " + statusCode + ", Msg: " + result);
                    callback.onError("Server Error: " + statusCode);
                }
            }


            @Override
            public void failed(Throwable t) {
                callback.onError(t.getMessage());
            }


            @Override
            public void cancelled() {
                callback.onError("Request cancelled");
            }
        });
    }
}
