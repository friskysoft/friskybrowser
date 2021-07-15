package com.friskysoft.test.utils.image;

import com.google.gson.Gson;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgbbImageUploader implements ImageUploader {

    private static final String CONNECTION_SCHEME = "https://";
    private static final String UPLOADER_HOST = "imgbb.com";
    private static final String UPLOADER_URL = CONNECTION_SCHEME + UPLOADER_HOST + "/json";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImgbbImageUploader.class);
    private static final Pattern TOKEN_PATTERN = Pattern.compile("auth_token=\"(\\w+)\";");

//    public static void main(String[] args) throws Exception {
//        LOGGER.info(new ImgbbImageUploader().upload("/Users/hossain/Desktop/testtest.png"));
//    }

    private static String getNewToken() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new java.net.URL(CONNECTION_SCHEME + UPLOADER_HOST).openConnection();
        int code = connection.getResponseCode();
        LOGGER.info("Token response code: " + code + " " + connection.getResponseMessage());
        BufferedReader br = new BufferedReader(new InputStreamReader((code == 200 ? connection.getInputStream() : connection.getErrorStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        String response = sb.toString();
        Matcher matcher = TOKEN_PATTERN.matcher(response);
        if (matcher.find() && matcher.groupCount() > 0) {
            LOGGER.info("Token: " + matcher.group(1));
            return matcher.group(1);
        } else {
            LOGGER.warn("Could not parse token");
            return "";
        }
    }

    public String upload(String filepath) throws IOException {
        filepath = filepath.replace("/./", "/");
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.

        HttpURLConnection connection = (HttpURLConnection) new java.net.URL(UPLOADER_URL).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        connection.setRequestProperty("Accept", "application/json");

        HttpEntity formEntity = MultipartEntityBuilder
                .create().setMode(HttpMultipartMode.STRICT)
                .setBoundary(boundary)
                .addPart("source", new FileBody(new File(filepath)))
                .addPart("type", new StringBody("file", ContentType.TEXT_HTML))
                .addPart("action", new StringBody("upload", ContentType.TEXT_HTML))
                .addPart("timestamp", new StringBody(System.currentTimeMillis() + "", ContentType.TEXT_HTML))
                .addPart("auth_token", new StringBody(getNewToken(), ContentType.TEXT_HTML))
                .build();

        formEntity.writeTo(connection.getOutputStream());

        // Request is lazily fired whenever you need to obtain information about response.
        int code = connection.getResponseCode();
        LOGGER.info("Image upload response code: " + code + " " + connection.getResponseMessage());

        BufferedReader br = new BufferedReader(new InputStreamReader((code == 200 ? connection.getInputStream() : connection.getErrorStream())));
        StringBuilder sb = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            sb.append(output);
        }
        String json = sb.toString();
        LOGGER.info("Image upload response body: " + json);

        try {
            Gson gson = new Gson();
            ImgbbResponse response = gson.fromJson(json, ImgbbResponse.class);
            return response.image.url;

        } catch (Exception ex) {
            String errMsg = "Image upload failed. ";
            if (code != 200) {
                errMsg = errMsg + "Server Response: " + code + " - " + json;
            } else {
                errMsg = errMsg + "Exception: " + ex.getClass().getSimpleName() + " - " + ex.getMessage();
            }
            LOGGER.error(errMsg);
            throw new RuntimeException(errMsg, ex);
        }
    }

    public static class ImgbbResponse {

        public Image image;

        public static class Image {
            public String url;
        }
    }
}
