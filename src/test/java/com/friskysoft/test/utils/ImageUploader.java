package com.friskysoft.test.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.file.Files;

public class ImageUploader {

    private static final String CONNECTION_SCHEME = "https:";
    private static final String IMAGE_UPLOAD_PROVIDER_URL = CONNECTION_SCHEME + "//imgsafe.org/upload-image";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageUploader.class);

    public static String upload(String filepath) throws IOException {
        String charset = "UTF-8";
        File imageFile = new File(filepath);
        String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String CRLF = "\r\n"; // Line separator required by multipart/form-data.

        HttpURLConnection connection = (HttpURLConnection) new java.net.URL(IMAGE_UPLOAD_PROVIDER_URL).openConnection();
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
        ) {

            // Send binary file.
            writer.append("--" + boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + imageFile.getName() + "\"").append(CRLF);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(imageFile.getName())).append(CRLF);
            writer.append("Content-Transfer-Encoding: binary").append(CRLF);
            writer.append(CRLF).flush();
            Files.copy(imageFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

            // End of multipart/form-data.
            writer.append("--" + boundary + "--").append(CRLF).flush();
        }

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
            ImgSafeFile uploadedData = gson.fromJson(json, ImgSafeFile.class);
            return CONNECTION_SCHEME + uploadedData.url;
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

    public class ImgSafeFile {
        String ext;
        String name;
        String og_name;
        long size;
        String type;
        String url;
    }

}
