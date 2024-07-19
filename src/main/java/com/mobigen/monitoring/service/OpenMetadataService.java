package com.mobigen.monitoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.mobigen.monitoring.config.OpenMetadataConfig;
import com.mobigen.monitoring.exception.CommonException;
import com.mobigen.monitoring.exception.ErrorCode;
import com.mobigen.monitoring.utils.Util;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.mobigen.monitoring.model.enums.Common.CONFIG;
import static com.mobigen.monitoring.model.enums.OpenMetadataEnums.*;

@Component
public class OpenMetadataService {
    final OpenMetadataConfig openMetadataConfig;
    final Util util = new Util();
    String accessToken;
    String tokenType;
    OkHttpClient client;


    public OpenMetadataService(OpenMetadataConfig openMetadataConfig) {
        this.openMetadataConfig = openMetadataConfig;
        this.client = new OkHttpClient()
                .newBuilder().build();
        getToken();
    }


    public JsonNode get(String endPoint) {
        String sb = this.tokenType +
                " " +
                this.accessToken;
        var url = openMetadataConfig.getOrigin() + endPoint;
        var request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", sb)
                .build();

        try (
                Response response = client.newCall(request).execute();
        ) {
            return util.getJsonNode(response.body().string());
        } catch (JsonProcessingException e) {
            throw CommonException.builder()
                    .errorCode(ErrorCode.JSON_MAPPER_FAIL)
                    .build();
        } catch (IOException e) {
            throw CommonException.builder()
                    .errorCode(ErrorCode.GET_FAIL)
                    .build();
        }
    }

    public JsonNode getDatabaseServices() {
        return get(openMetadataConfig.getPath().getDatabaseService()).get(DATA.getName());
    }

    public JsonNode getStorageServices() {
        return get(openMetadataConfig.getPath().getStorageService()).get(DATA.getName());
    }

    public JsonNode getQuery(String param) {
        var queryUrl = openMetadataConfig.getPath().getQuery();

        return get(queryUrl + param);
    }

    public String post(String endPoint, String body) {
        var mediaType = MediaType.parse("application/json");
        var request_body = RequestBody.create(body, mediaType);

        var sb = new StringBuilder();
        sb.append(this.tokenType != null)
                .append(" ")
                .append(this.accessToken);

        var url = openMetadataConfig.getOrigin() + endPoint;
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
            return response.body().string();
        } catch (IOException e) {
            throw CommonException.builder()
                    .errorCode(ErrorCode.GET_TOKEN_FAIL)
                    .build();
        }
    }

    private void getToken() {
        // getHostToken
        var id = this.openMetadataConfig.getAuth().getId();
        var pw = this.openMetadataConfig.getAuth().getPasswd();
        var encodePw = Base64.getEncoder().encodeToString(pw.getBytes());

        var token = post(openMetadataConfig.getPath().getLogin(),
                "{\"email\":\"" + id + "\",\"password\":\"" + encodePw + "\"}");
        try {
            var tokenJson = util.getJsonNode(token);
            this.accessToken = tokenJson.get(ACCESS_TOKEN.getName()).asText();
            this.tokenType = tokenJson.get(TOKEN_TYPE.getName()).asText();
        } catch (JsonProcessingException e) {
            throw CommonException.builder()
                    .errorCode(ErrorCode.GET_TOKEN_FAIL)
                    .build();
        }

        // getBotId
        var botIdJson = get(openMetadataConfig.getPath().getBot());
        var botId = botIdJson.get(BOT_USER.getName()).get(ID.getName()).asText();

        var botConfig = get(openMetadataConfig.getPath().getAuthMechanism() + "/" + botId);
        this.accessToken = botConfig.get(CONFIG.getName()).get(JWT_TOKEN.getName()).asText();
    }
}