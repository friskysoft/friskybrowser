package com.friskysoft.test.utils.image;

import java.io.IOException;

public interface ImageUploader {

    /**
     *
     * @param filepath = absolute filepath of the image file
     * @return String of the generated http URL for the uploaded image
     */
    String upload(String filepath) throws IOException;

}
