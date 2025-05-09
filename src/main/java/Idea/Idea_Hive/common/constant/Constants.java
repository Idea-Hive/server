package Idea.Idea_Hive.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


public record Constants(
        @Value("${frontend.url}") String FRONTEND_URL) {
}
