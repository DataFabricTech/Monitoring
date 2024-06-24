package com.mobigen.monitoring.service;

import com.mobigen.monitoring.config.NewOpenMetadataConfig;
import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.utils.Util;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.util.*;

@Component
public class OpenMetadataService {
    OpenMetadataConfig openMetadataConfig;
    NewOpenMetadataConfig newOpenMetadataConfig;
    String accessToken;
    String tokenType;
    OkHttpClient client;
    final Util util = new Util();


    public OpenMetadataService(OpenMetadataConfig openMetadataConfig, NewOpenMetadataConfig newOpenMetadataConfig) {
        this.openMetadataConfig = openMetadataConfig;
        this.newOpenMetadataConfig = newOpenMetadataConfig;
        this.client = new OkHttpClient()
                .newBuilder().build();
        this.accessToken = getToken();
//        this.tokenType = getTokenType();
    }


    public List<Object> get(String endPoint) {
        var mediaType = MediaType.parse("text/plain");

        var sb = new StringBuilder();
        sb.append(this.tokenType)
                .append(" ")
                .append(this.accessToken);
        var url = newOpenMetadataConfig.getOrigin() + endPoint;
        var request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", sb.toString())
                .build();
        try (
                Response response = client.newCall(request).execute();
        ) {
            // TODO 이걸로 변경될 예정
            response.body();
            return new ArrayList<>();
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    public List<Object> getDatabaseServices() {
        return get(newOpenMetadataConfig.getPath().getDatabaseService());
    }

    public List<Object> getStorageServices() {
        return get(newOpenMetadataConfig.getPath().getStorageService());
    }

    public String post(String endPoint, String body) {
        var mediaType = MediaType.parse("application/json");
        var request_body = RequestBody.create(body, mediaType);

        var sb = new StringBuilder();
        sb.append(this.tokenType != null)
                .append(" ")
                .append(this.accessToken);

        var url = newOpenMetadataConfig.getOrigin() + endPoint;
        var requestBuilder = new Request.Builder()
                .url(url)
                .method("POST", request_body)
                .addHeader("Content-Type", "application/json");
        if (this.tokenType != null && this.accessToken!= null)
            requestBuilder.addHeader("Authorization", sb.toString());

        try (
                Response response = client.newCall(requestBuilder.build()).execute();
        ) {
            // todo
            return response.body().string();
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        // getHostToken
        var id = this.openMetadataConfig.getAuth().getId();
        var pw = this.openMetadataConfig.getAuth().getPasswd();
        var encodePw = Base64.getEncoder().encodeToString(pw.getBytes());
        var sb = new StringBuilder();
        sb.append("{\"email\":\"").append(id).append("\",\"password\":\"").append(encodePw).append("\"}");

        var mediaType = MediaType.parse("application/json");
        var request_body = RequestBody.create(sb.toString(), mediaType);

        sb = new StringBuilder();
        var endPoint = sb.append(newOpenMetadataConfig.getOrigin()).append(newOpenMetadataConfig.getPath().getLogin()).toString();
        var token = post(newOpenMetadataConfig.getPath().getLogin(), sb.toString());
        System.out.println("?????????");
        System.out.println(token);
        return null;
//        var request = new Request.Builder()
//                .url(url)
//                .method("POST", request_body)
//                .addHeader("Content-Type", "application/json")
//                .build();
//
//        try (
//                Response response = client.newCall(request).execute();
//        ) {
//            var json = util.getJsonNode(Objects.requireNonNull(response.body()).string());
//            this.accessToken = json.get(ACCESS_TOKEN.getName()).asText();
//            this.tokenType = json.get(TOKEN_TYPE.getName()).asText();
//        } catch (IOException e) {
//            // todo
//            throw new RuntimeException(e);
//        }
//
//        // get BotToken
//        sb = new StringBuilder();
//        sb.append(this.tokenType)
//                .append(" ")
//                .append(this.accessToken);
//
//        url = newOpenMetadataConfig.getOrigin() + newOpenMetadataConfig.getPath().getBot();
//        request = new Request.Builder()
//                .url(url)
//                .method("GET", null)
//                .addHeader("Authorization", sb.toString())
//                .build();
//
//        try (
//                Response response = client.newCall(request).execute();
//        ) {
//            // todo
//            var botId = util.getJsonNode(response.body().string()).get(ID.getName()).asLong();
//            url = newOpenMetadataConfig.getOrigin() + newOpenMetadataConfig.getPath().getAuthMechanism() + "/" + botId;
//            request
//        } catch (IOException e) {
//            // todo
//            throw new RuntimeException(e);
//        }
    }

    private boolean tokenExpireCheck() {
        var decoder = Base64.getUrlDecoder();
        var chunks = this.accessToken.split("\\.");
        var payLoad = new String(decoder.decode(chunks[1]));
        var exp = new Date(util.getJsonNode(payLoad).get("exp").asLong() * 1000);

        var now = new Date();
        return !now.before(exp);
    }
}