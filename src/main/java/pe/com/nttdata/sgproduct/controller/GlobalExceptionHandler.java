package pe.com.nttdata.sgproduct.controller;

import com.nttdata.sgproduct.model.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pe.com.nttdata.sgproduct.exception.CustomerException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final String ERROR_MESSAGE = "Lo sentimos por favor vuelve a intentar mas tarde.";

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomerException(CustomerException ex) {
        ApiErrorResponse  apiErrorResponse = new ApiErrorResponse();
        if (StringUtils.hasText(ex.getMessage())) {

            apiErrorResponse.setCode(ex.getCode());
            apiErrorResponse.message(ex.getMessage());
            return ResponseEntity.status(ex.getHttpStatus()).body(apiErrorResponse);
        }
        apiErrorResponse.setCode("500");
        apiErrorResponse.message(ERROR_MESSAGE);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiErrorResponse);
    }
}
