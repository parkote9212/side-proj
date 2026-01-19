package com.pgc.sideproj.exception;

import com.pgc.sideproj.dto.common.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception e) {
        // 에러로그
        log.error("[Global Error] 처리되지 않은 예외 발생", e);

        // 클라이언트에게 구체적인 서버 내부 정보 노출방지
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String message = "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(statusCode, message);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 2. (선택 사항) CustomException을 위한 예시 슬롯 (나중에 사용)
    // 예를 들어, 직접 정의한 'ResourceNotFoundException'이 발생하면 404를 반환하도록 처리할 수 있습니다.
    /*
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDTO> handleCustomException(CustomException e) {
        log.warn("🚨 [Custom Error] Custom 예외 발생: {}", e.getMessage());

        int statusCode = e.getStatusCode(); // CustomException에 정의된 상태 코드

        ErrorResponseDTO errorResponse = ErrorResponseDTO.of(statusCode, e.getMessage());

        // HTTP Status도 CustomException에 맞게 설정하여 반환
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
    }
    */


}
