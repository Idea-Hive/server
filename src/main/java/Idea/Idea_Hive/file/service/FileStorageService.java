package Idea.Idea_Hive.file.service;

import Idea.Idea_Hive.task.dto.response.TaskResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    TaskResponse storeFile(MultipartFile file, Long taskId); // 저장 후 파일 접근 경로 또는 키 반환

}
