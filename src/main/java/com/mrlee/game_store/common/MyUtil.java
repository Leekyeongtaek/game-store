package com.mrlee.game_store.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
public class MyUtil {

    public static String uploadImageFile(MultipartFile file) {
        final String UPLOAD_PATH = "src/main/resources/static/images/";
        final String DIRECTORY_PATH = "/images/";

        if (file.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
        }

        try {
            String fileName = UUID.randomUUID() + "." + file.getOriginalFilename().split("\\.")[1];
            File file2 = new File(UPLOAD_PATH + fileName);
            file.transferTo(file2.toPath());
            return DIRECTORY_PATH + fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 저장 중 오류 발생.", e);
        }
    }

    public static String truncateGameTitle(String gameName) {
        return gameName.length() > 30 ? gameName.substring(0, 30) + "..." : gameName;
    }

    public static String toFormattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return date.format(formatter);
    }

    public static Pageable getPageableWithSortByIdDesc(Pageable pageable) {
        Sort sortByIdDesc = Sort.by(Sort.Direction.DESC, "id");
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortByIdDesc
        );
    }


}
