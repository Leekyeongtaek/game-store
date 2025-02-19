package com.mrlee.game_store.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class ImageUploadUtil {

    private static final String UPLOAD_PATH = "src/main/resources/static/images/";

    public static String uploadImageFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }

        try {
            String fileName = UUID.randomUUID() + "." + file.getOriginalFilename().split("\\.")[1];
            File file2 = new File(UPLOAD_PATH + fileName);
            file.transferTo(file2.toPath());
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 저장 중 오류 발생.", e);
        }
    }

}
