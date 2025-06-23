package Idea.Idea_Hive.member.entity.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record ProfileUpdateRequest(
        String name,
        String job,
        @PositiveOrZero(message = "경력은 0년 이상이어야 합니다.")
        Integer career,
        List<Long> skillStackIds
) {
}
