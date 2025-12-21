package com.finpro.frontend.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

public class BackendService {
    // Changed to localhost to avoid some system-specific IPv4/IPv6 binding issues
    private static final String BASE_URL = "http://localhost:8081/api";

    public interface RequestCallback {
        void onSuccess(String response);

        void onError(String error);
    }

    public void createPlayer(String username, RequestCallback callback) {
        // PlayerController uses @RequestParam, so we pass it in the URL
        String url = BASE_URL + "/players/register?username=" + username;

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .url(url)
                // .header("Content-Type", "application/json") // Not needed for empty body
                .build();

        // Set timeout to avoid hanging (default is usually long)
        request.setTimeOut(5000);

        sendRequest(request, callback);
    }

    public void submitScore(String playerId, long l1, long l2, long l3, long l4, long l5, RequestCallback callback) {
        String json = String.format(
                "{\"playerId\":\"%s\",\"level1Time\":%d,\"level2Time\":%d,\"level3Time\":%d,\"level4Time\":%d,\"level5Time\":%d}",
                playerId, l1, l2, l3, l4, l5);

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
                .method(Net.HttpMethods.POST)
                .url(BASE_URL + "/scores")
                .header("Content-Type", "application/json")
                .content(json)
                .build();

        sendRequest(request, callback);
    }

    public void getLeaderboard(int limit, RequestCallback callback) {
        String url = BASE_URL + "/scores/leaderboard"; // Backend endpoint for overall

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
                .build();

        sendRequest(request, callback);
    }

    public void getLeaderboardByLevel(int level, int limit, RequestCallback callback) {
        String url = BASE_URL + "/scores/leaderboard/level/" + level;

        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest request = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url(url)
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
                    Gdx.app.error("BackendService", "HTTP Error: " + statusCode + ", Msg: " + result);
                    callback.onError("Server Error: " + statusCode);
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("BackendService", "Request Failed (Game)", t);
                callback.onError("Connection Failed: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                callback.onError("Request cancelled");
            }
        });
    }
}
