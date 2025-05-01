package Idea.Idea_Hive.exception.handler;

import Idea.Idea_Hive.exception.handler.custom.SkillStackAlreadyExistsException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden // Swagger 충돌
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(SkillStackAlreadyExistsException.class)
    public ResponseEntity<String> handlerHashtagAlreadyExistsException(SkillStackAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ResponseEntity<String> handleOAuth2AuthenticationException(OAuth2AuthenticationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
}
