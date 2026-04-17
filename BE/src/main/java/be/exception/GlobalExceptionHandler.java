package be.exception;

import be.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {

        ErrorCode errorCode = ex.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.builder()
                        .status(errorCode.getStatus().value())
                        .message(errorCode.getMessage())
                        .data(null)
                        .build());
    }

    // ✅ Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Invalid input");

        return ResponseEntity.badRequest().body(
                ApiResponse.builder()
                        .status(400)
                        .message(message)
                        .data(null)
                        .build()
        );
    }

    // ✅ fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {

        return ResponseEntity
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatus())
                .body(ApiResponse.builder()
                        .status(500)
                        .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                        .data(null)
                        .build());
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException ex) {

        return ResponseEntity
                .status(ErrorCode.PERMISSION_DENIED.getStatus())
                .body(ApiResponse.builder()
                        .status(ErrorCode.PERMISSION_DENIED.getStatus().value())
                        .message(ErrorCode.PERMISSION_DENIED.getMessage())
                        .data(null)
                        .build());
    }
}