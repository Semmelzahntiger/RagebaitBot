package com.tree3.util;

import com.tree3.Main;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BotHttpClient {
    private final OkHttpClient httpClient;
    public BotHttpClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
    public BotHttpClient(OkHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String proveLatex(String token, InputStream data, String ans) {
        try {
            RequestBody fileBody = RequestBody.create(data.readAllBytes(), MediaType.parse("image/png"));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("token", token)
                    .addFormDataPart("img", "solve.png", fileBody)
                    .addFormDataPart("ans", ans)
                    .build();
            Request request = new Request.Builder()
                    .url(Main.getLatexInterpreterUri() + "/solve")
                    .post(requestBody)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if(response.code() != 200) {
                throw new IOException(response.body().string());
            }
            return response.body().string();
        } catch (IOException e) {
            return "";
        }
    }
    public String interpretLatex(InputStream data) {
        try {
            RequestBody fileBody = RequestBody.create(data.readAllBytes(), MediaType.parse("image/png"));
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("img", "solve.png", fileBody)
                    .build();
            Request request = new Request.Builder()
                    .url(Main.getLatexInterpreterUri() + "/interpret")
                    .post(requestBody)
                    .build();
            Response response = httpClient.newCall(request).execute();
            if(response.code() != 200) {
                throw new IOException(response.body().string());
            }
            return response.body().string();
        } catch (IOException e) {
            return "";
        }

    }

}
