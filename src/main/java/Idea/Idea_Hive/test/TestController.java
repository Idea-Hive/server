package Idea.Idea_Hive.test;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Profile("prod")
@RestController
public class TestController {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public TestController(AmazonS3 amazonS3, @Value("${aws.s3.bucket-name}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "test!!";
    }

    // S3 연결 테스트
    @GetMapping("/test/s3/connection")
    public ResponseEntity<String> testS3Connection() {
        try {
            boolean bucketExists = amazonS3.doesBucketExistV2(bucketName);
            if (bucketExists) {
                return ResponseEntity.ok("S3 연결 성공! 버킷 '" + bucketName + "' 존재함");
            } else {
                return ResponseEntity.badRequest().body("S3 연결 실패! 버킷 '" + bucketName + "' 존재하지 않음");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("S3 연결 오류: " + e.getMessage());
        }
    }

    // S3 버킷 내 파일 목록 조회
    @GetMapping("/test/s3/files")
    public ResponseEntity<List<String>> listS3Files() {
        try {
            List<S3ObjectSummary> objects = amazonS3.listObjectsV2(bucketName).getObjectSummaries();
            List<String> fileNames = objects.stream()
                    .map(S3ObjectSummary::getKey)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(fileNames);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of("파일 목록 조회 실패: " + e.getMessage()));
        }
    }

    // S3에 파일 업로드 테스트
    @PostMapping("/test/s3/upload")
    public ResponseEntity<String> uploadFileToS3(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("업로드할 파일이 없습니다.");
            }

            // image 폴더에 업로드
            String fileName = "image/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
            
            // S3에 파일 업로드
            amazonS3.putObject(bucketName, fileName, file.getInputStream(), null);
            
            String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
            
            return ResponseEntity.ok("파일 업로드 성공!\n파일명: " + fileName + "\nURL: " + fileUrl);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // S3 파일 삭제 테스트
    @DeleteMapping("/test/s3/delete/{fileName}")
    public ResponseEntity<String> deleteFileFromS3(@PathVariable String fileName) {
        try {
            amazonS3.deleteObject(bucketName, fileName);
            return ResponseEntity.ok("파일 삭제 성공: " + fileName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 삭제 실패: " + e.getMessage());
        }
    }
}
