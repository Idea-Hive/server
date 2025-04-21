package Idea.Idea_Hive.exception.handler.custom;

public class HashtagAlreadyExistsException extends RuntimeException {
    public HashtagAlreadyExistsException(String name) {
        super("이미 존재하는 해시태그입니다: " + name);
    }
}
