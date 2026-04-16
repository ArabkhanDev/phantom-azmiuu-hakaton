package az.microservices.azmiuuhakaton.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExceptionMessage {

    USER_NOT_FOUND("user not found");

    private final String message;
}
