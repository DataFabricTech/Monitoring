package com.mobigen.monitoring.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.NewOpenMetadataConfig;
import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.utils.Util;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

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
        getToken();
    }


    public JsonNode get(String endPoint) {
        String sb = this.tokenType +
                " " +
                this.accessToken;
        var url = newOpenMetadataConfig.getOrigin() + endPoint;
        var request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", sb)
                .build();

        try (
                Response response = client.newCall(request).execute();
        ) {
            return util.getJsonNode(response.body().string());
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    public JsonNode getDatabaseServices() {
        return get(newOpenMetadataConfig.getPath().getDatabaseService()).get("data");
    }

    public JsonNode getStorageServices() {
        return get(newOpenMetadataConfig.getPath().getStorageService()).get("data");
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
                .method("POST", request_body);
        if (this.tokenType != null && this.accessToken != null)
            requestBuilder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", sb.toString());

        var as = requestBuilder.build();

        try (
                Response response = client.newCall(as).execute();
        ) {
            // todo
            return response.body().string();
        } catch (IOException e) {
            // todo
            throw new RuntimeException(e);
        }
    }

    private void getToken() {
        // getHostToken
        var id = this.openMetadataConfig.getAuth().getId();
        var pw = this.openMetadataConfig.getAuth().getPasswd();
        var encodePw = Base64.getEncoder().encodeToString(pw.getBytes());

        var token = post(newOpenMetadataConfig.getPath().getLogin(),
                "{\"email\":\"" + id + "\",\"password\":\"" + encodePw + "\"}");
        var tokenJson = util.getJsonNode(token);
        this.accessToken = tokenJson.get(ACCESS_TOKEN.getName()).asText();
        this.tokenType = tokenJson.get(TOKEN_TYPE.getName()).asText();

        // getBotId
        var botIdJson = get(newOpenMetadataConfig.getPath().getBot());
        var botId = botIdJson.get(BOT_USER.getName()).get(ID.getName()).asText();

        var botConfig = get(newOpenMetadataConfig.getPath().getAuthMechanism() + "/" + botId);
        this.accessToken = botConfig.get(CONFIG.getName()).get(JWT_TOKEN.getName()).asText();
    }
}