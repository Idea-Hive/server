package Idea.Idea_Hive.exception.handler.custom;

import lombok.Getter;

import java.util.Map;

@Getter
public class ProjectValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ProjectValidationException(Map<String, String> errors) {
        this.errors = errors;
    }

}
