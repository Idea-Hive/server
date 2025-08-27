package Idea.Idea_Hive.task.dto.response;

import org.springframework.core.io.Resource;

public record DownloadFileResponse(
        Resource resource,
        String originalFileName
) {
    public static DownloadFileResponse of(Resource resource, String originalFileName) {
        return new DownloadFileResponse(resource, originalFileName);
    }
}
