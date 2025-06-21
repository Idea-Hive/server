package Idea.Idea_Hive.file.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file); // 저장 후 파일 접근 경로 또는 키 반환

}
