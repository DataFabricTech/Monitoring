package com.mobigen.monitoring.utils;

import com.mobigen.monitoring.config.OpenMetadataConfig;
import io.jsonwebtoken.Jwts;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class RestAPI {
    OpenMetadataConfig openMetadataConfig;
    String accessToken;
    String tokenType;

    public RestAPI(OpenMetadataConfig openMetadataConfig) {
        this.openMetadataConfig = openMetadataConfig;
    }


    /**
     * Object는 T가 될 것이다.
     */
    public List<Object> get(String endPoint) {
        if (this.accessToken == null && tokenExpireCheck()) {
            this.accessToken = getToken();
        }
        var client = new OkHttpClient()
                .newBuilder()
                .build();

        var mediaType = MediaType.parse("text/plain");
        var body = RequestBody.create("", mediaType);

        var sb = new StringBuilder();
        sb.append(this.tokenType)
                .append(" ")
                .append(this.accessToken);
        var url = openMetadataConfig.getOrigin() + endPoint;
        var request = new Request.Builder()
                .url(url)
                .method("GET", body)
                .addHeader("Authorization", sb.toString())
                .build();
        try (
                Response response = client.newCall(request).execute();
                ){
            // TODO 이걸로 변경될 예정
            response.body();
            return new ArrayList<>();
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    public void post(String endPoint, String body) {
        if (this.accessToken == null && tokenExpireCheck()) {
            this.accessToken = getToken();
        }
        var client = new OkHttpClient()
                .newBuilder()
                .build();

        var mediaType = MediaType.parse("application/json");
        var request_body = RequestBody.create(body, mediaType);

        var sb = new StringBuilder();
        sb.append(this.tokenType)
                .append(" ")
                .append(this.accessToken);

        var url = openMetadataConfig.getOrigin()+endPoint;
        var request = new Request.Builder()
                .url(url)
                .method("GET", request_body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", sb.toString())
                .build();
        try (
                Response response = client.newCall(request).execute();
        ){
            // todo
            System.out.println(response);
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        var client = new OkHttpClient()
                .newBuilder()
                .build();
        var id = openMetadataConfig.getAuth().getId();
        var pw = openMetadataConfig.getAuth().getPasswd();
        var encodePw = Base64.getEncoder().encodeToString(pw.getBytes());
        var sb = new StringBuilder();
        sb.append("{\"email\":\"").append(id).append("\",\"password\":\"").append(encodePw).append("\"}");

        var mediaType = MediaType.parse("application/json");
        var request_body = RequestBody.create(sb.toString(), mediaType);

        sb = new StringBuilder();
        var url = sb.append(openMetadataConfig.getOrigin()).append("/api/v1/users/login").toString();
        var request = new Request.Builder()
                .url(url)
                .method("GET", request_body)
                .addHeader("Content-Type", "application/json")
                .build();
        try (
                Response response = client.newCall(request).execute();
        ){
            // todo accessToken을 줄 수 있도록 고치기
            return response.body().toString();
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    private boolean tokenExpireCheck() {
        var claims = Jwts.parser()
                .parseClaimsJws(accessToken)
                .getBody();

        var now = new Date();

        return !now.before(claims.getExpiration());
    }
}
