package hello.hello_spring.locker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LockerException.class)
    public ResponseEntity<Map<String, Object>> handleLockerException(LockerException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());      // 프론트에 보여줄 메시지
        body.put("code", "LOCKER_BAD_REQUEST");    // 커스텀 에러 코드
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
