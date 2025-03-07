package com.cn.bamboo.util;

import com.google.gson.Gson;
import okhttp3.*;
import java.io.IOException;

public class HttpRequest {

    public static Headers getHeaders(){
        Headers.Builder headers = new Headers.Builder();
        headers.add("User-Agent", "bambu_network_agent/01.09.05.01");
        headers.add("X-BBL-Client-Name", "OrcaSlicer");
        headers.add("X-BBL-Client-Type", "slicer");
        headers.add("X-BBL-Client-Version", "01.09.05.51");
        headers.add("X-BBL-Language", "en-US");
        headers.add("X-BBL-OS-Type", "linux");
        headers.add("X-BBL-OS-Version", "6.2.0");
        headers.add("X-BBL-Agent-Version", "01.09.05.01");
        headers.add("X-BBL-Executable-info", "{}");
        headers.add("X-BBL-Agent-OS-Type", "linux");
        headers.add("accept", "application/json");
        headers.add("Content-Type", "application/json");
        return headers.build();
    }

    public static String post(String url, Object object){

        OkHttpClient client = new OkHttpClient();
        String json = new Gson().toJson(object);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .headers(getHeaders())
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Headers getHeadersByAuth(String accessToken){
        return getHeaders().newBuilder()
                .add("Authorization","Bearer " + accessToken)
                .build();
    }

    public static String get(String url, String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .headers(getHeadersByAuth(accessToken))
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
