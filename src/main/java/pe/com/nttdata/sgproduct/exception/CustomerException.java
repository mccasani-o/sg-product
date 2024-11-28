package pe.com.nttdata.sgproduct.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter

public class CustomerException extends RuntimeException{

    private final String code;
    private final HttpStatus httpStatus;

    public CustomerException(String code, HttpStatus httpStatus) {
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public CustomerException(String message, String code, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}
