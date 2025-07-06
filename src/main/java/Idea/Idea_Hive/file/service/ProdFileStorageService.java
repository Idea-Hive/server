package Idea.Idea_Hive.file.service;

import Idea.Idea_Hive.exception.handler.custom.FileStorageException;
import Idea.Idea_Hive.task.dto.response.DownloadFileResponse;
import Idea.Idea_Hive.task.dto.response.TaskResponse;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.repository.TaskRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
public class ProdFileStorageService implements FileStorageService {


    private final AmazonS3Client amazonS3Client;
    private final TaskRepository taskRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucket;

    @Override
    @Transactional
    public TaskResponse storeFile(MultipartFile file, Long taskId) {
        // Task 조회
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new FileStorageException("존재하지 않는 과제입니다. taskId: " + taskId));

        // 기존 파일 삭제 (DB에 저장된 S3 키를 사용)
        if (task.getFilePath() != null && !task.getFilePath().isEmpty()) {
            deleteFileFromS3(task.getFilePath());
        }

        // S3에서는 폴더 구분을 위해 '/'를 사용합니다.
        String originalFileName = file.getOriginalFilename();
        String s3Key = "tasks/" + taskId + "/" + UUID.randomUUID() + "_" + originalFileName;

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            try (InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(bucket, s3Key, inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            }

            // S3 키와 원본 파일 이름을 각각의 필드에 저장 ★★★
            task.uploadFile(s3Key, originalFileName);
            Task savedTask = taskRepository.save(task);

            return TaskResponse.from(savedTask);
        } catch (IOException ex) {
            throw new FileStorageException("S3 파일 업로드 실패: " + originalFileName + " " + ex);
        }
    }

    @Override
    public DownloadFileResponse downloadFile(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new FileStorageException("존재하지 않는 과제입니다. taskId: " + taskId));

        String s3Key = task.getFilePath();
        if (s3Key == null || s3Key.isEmpty()) {
            throw new FileStorageException("과제에 업로드된 파일이 없습니다. taskId: " + taskId);
        }

        try {
            S3Object s3Object = amazonS3Client.getObject(bucket, s3Key);
            Resource resource = new InputStreamResource(s3Object.getObjectContent());

            // ★★★ 핵심: DB에 저장된 원본 파일 이름을 바로 사용 ★★★
            String originalFileName = task.getOriginalFileName();

            return DownloadFileResponse.of(resource, originalFileName);

        } catch (Exception e) {
            throw new FileStorageException("S3 파일 다운로드 중 오류가 발생했습니다: " + e);
        }
    }


    private void deleteFileFromS3(String s3Key) {
        try {
            //
            log.info("S3에서 파일 삭제 시도: bucket={}, key={}", bucket, s3Key);

            amazonS3Client.deleteObject(bucket, s3Key);

            log.info("S3 파일 삭제 성공: key={}", s3Key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 중 오류가 발생했습니다. Key: {}", s3Key, e);
        }
    }
}
