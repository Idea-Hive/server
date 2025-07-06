package Idea.Idea_Hive.file.service;

import Idea.Idea_Hive.exception.handler.custom.FileStorageException;
import Idea.Idea_Hive.task.dto.response.DownloadFileResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Profile("local")
@Slf4j
public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;
    private final String UPLOAD_DIR_NAME = "test_uploads";
    private final TaskRepository taskRepository;

    public LocalFileStorageService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        try {
            // 1. ClassLoader를 통해 resources 디렉터리의 URL을 얻습니다.
            URL resourceDirUrl = getClass().getClassLoader().getResource("");
            if (resourceDirUrl == null) {
                throw new FileStorageException("Cannot find resources directory.");
            }

            // 2. URL을 Path 객체로 변환합니다.
            Path resourcesPath = Paths.get(resourceDirUrl.toURI());

            // 3. resources 디렉터리 하위에 업로드 디렉터리 경로를 설정합니다.
            this.fileStorageLocation = resourcesPath.resolve(UPLOAD_DIR_NAME).toAbsolutePath().normalize();

            // 4. 디렉터리가 없으면 생성합니다.
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
                System.out.println("테스트용 파일 저장 위치 (resources 하위): " + this.fileStorageLocation.toString());
            } else {
                System.out.println("테스트용 파일 저장 위치 (resources 하위, 이미 존재함): " + this.fileStorageLocation.toString());
            }

        } catch (Exception ex) {
            throw new FileStorageException("테스트용 파일 저장 디렉토리(" + UPLOAD_DIR_NAME + ")를 resources 하위에 생성할 수 없습니다." + ex);
        }
    }



    @Override
    @Transactional
    public TaskResponse storeFile(MultipartFile file, Long taskId) {

        // 1. Task 엔티티 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new FileStorageException("존재하지 않는 과제입니다. taskId: " + taskId));

        // 기존 파일 삭제
        if (task.getFilePath() != null && !task.getFilePath().isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(task.getFilePath()));
                log.info("기존 로컬 파일 삭제 성공: {}", task.getFilePath());
            } catch (Exception e) {
                log.error("기존 로컬 파일 삭제 실패: {}. 에러: {}", task.getFilePath(), e.getMessage());
            }
        }

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String storedFileName = UUID.randomUUID() + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            // ★★★ 핵심: 파일 경로와 원본 파일 이름을 각각의 필드에 저장 ★★★
            task.uploadFile(targetLocation.toString(), originalFileName);
            Task savedTask = taskRepository.save(task);

            return TaskResponse.from(savedTask);

        } catch (IOException ex) {
            throw new FileStorageException("파일 저장 실패: " + originalFileName + " : " + ex);
        }
    }

    @Override
    public DownloadFileResponse downloadFile(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new FileStorageException("존재하지 않는 과제입니다. taskId: " + taskId));

        String storedPath = task.getFilePath();
        if (storedPath == null || storedPath.isEmpty()) {
            throw new FileStorageException("과제에 업로드된 파일이 없습니다. taskId: " + taskId);
        }

        try {
            Path filePath = Paths.get(storedPath);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("파일을 찾을 수 없거나 읽을 수 없습니다. path: " + storedPath);
            }

            // ★★★ 핵심: DB에 저장된 원본 파일 이름을 바로 사용 ★★★
            String originalFileName = task.getOriginalFileName();

            return DownloadFileResponse.of(resource, originalFileName);

        } catch (Exception e) {
            throw new FileStorageException("파일 다운로드 중 오류가 발생했습니다." + " " + e);
        }
    }

    // 저장된 파일 이름에서 원본 파일 이름을 추출하는 헬퍼 메서드
    private String extractOriginalFileName(String storedFileName) {
        // 예: originalName_uuid.txt -> originalName.txt
        int underscoreIndex = storedFileName.lastIndexOf("_");
        if (underscoreIndex == -1) {
            return storedFileName; // UUID가 없는 경우 (오래된 데이터 등)
        }
        String extension = "";
        int dotIndex = storedFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            extension = storedFileName.substring(dotIndex);
        }
        return storedFileName.substring(0, underscoreIndex) + extension;
    }


}
