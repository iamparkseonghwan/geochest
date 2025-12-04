package hello.hello_spring.locker;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class LockerException extends RuntimeException {

    public LockerException(String message) {
        super(message);
    }
}