package Idea.Idea_Hive.project.dto.request;

import Idea.Idea_Hive.project.entity.ProjectStatus;

/**
 * 프로젝트 관리 > 본인 프로젝트 리스트 조회를 위한 Request DTO
 * 현재 로그인한 사용자 기준이므로 memeber id는 security context holder에서 가져옴
 */

public record ProjectListInManageRequest(
        ProjectStatus status
) {
}
