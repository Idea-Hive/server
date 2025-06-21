package Idea.Idea_Hive.file.service;

import Idea.Idea_Hive.exception.handler.custom.FileStorageException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
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
public class LocalFileStorageService implements FileStorageService {

    private final Path fileStorageLocation;
    private final String UPLOAD_DIR_NAME = "test_uploads";

    public LocalFileStorageService() {
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
    public String storeFile(MultipartFile file, Long taskId) {

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = ""; // 확장자(.pdf, .txt, .ppt ...)
        int i = originalFileName.lastIndexOf('.');
        if (i>0) {
            extension = originalFileName.substring(i); // 확장자 추출
        }
        String storedFileName = UUID.randomUUID().toString() + extension; // 파일명 겹칠 경우 처리

        try {
            if (originalFileName.contains("..")) {
                throw new FileStorageException("잘못된 파일명입니다: " + originalFileName);
            }

            Path targetLocation =
                    this.fileStorageLocation.resolve(storedFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("파일 저장 실패:" + originalFileName + ". 재시도 해주세요 : " + ex.getMessage());
        }
    }
}
