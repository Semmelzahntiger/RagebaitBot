package com.tree3.util;

import com.tree3.Main;

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

public class BotHttpClient {
    private final HttpClient httpClient;
    public BotHttpClient() {
        this.httpClient = HttpClient.newBuilder().build();
    }
    public BotHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String proveLatex(String token, InputStream data, String ans) {
        try {
            UUID boundary = UUID.randomUUID();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.getLatexInterpreterUri()))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(ofMimeMultipartData(
                            boundary.toString(),
                            "img",
                            data,
                            "proof.png",
                            "image/png",
                            Map.of("token", token, "ans", ans)))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch(IOException | InterruptedException e) {
            LogUtil.getLogger().error(e.getMessage());
            return "";
        }
    }
    public String interpretLatex(InputStream data) {
        try {
            UUID boundary = UUID.randomUUID();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Main.getLatexInterpreterUri()))
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(ofMimeMultipartData(
                            boundary.toString(),
                            "img",
                            data,
                            "interpret.png",
                            "image/png",
                            Map.of()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }
        catch(IOException | InterruptedException e) {
            LogUtil.getLogger().error(e.getMessage());
            return "";
        }
    }
    private static  HttpRequest.BodyPublisher ofMimeMultipartData(String boundary,
                                                                  String fileFieldName,
                                                                  InputStream data,
                                                                  String filename,
                                                                  String contentType,
                                                                  Map<String,String> fields) throws IOException {
        var parts = new ArrayList<byte[]>();
        byte[] sep = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
                .getBytes(StandardCharsets.UTF_8);
        for (var e : fields.entrySet()) {
            parts.add(sep);
            parts.add(("\"" + e.getKey() + "\"\r\n\r\n" + e.getValue() + "\r\n")
                    .getBytes(StandardCharsets.UTF_8));
        }
        parts.add(sep);
        parts.add(("\"" + fileFieldName + "\"; filename=\"" + filename + "\"\r\n"
                + "Content-Type: " + contentType + "\r\n\r\n")
                .getBytes(StandardCharsets.UTF_8));
        parts.add(data.readAllBytes());
        parts.add("\r\n".getBytes(StandardCharsets.UTF_8));

        parts.add(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        return HttpRequest.BodyPublishers.ofByteArrays(parts);
    }
}
