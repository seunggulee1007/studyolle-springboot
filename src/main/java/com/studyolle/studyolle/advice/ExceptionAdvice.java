package com.studyolle.studyolle.advice;

import com.studyolle.studyolle.modules.account.AccountException;
import com.studyolle.studyolle.advice.exception.ExpiredRefreshTokenException;
import com.studyolle.studyolle.commons.ErrorsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Iterator;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected ResponseEntity defaultException(Exception e) {
        log.error( "defaultException", e );
        return failRequest( HttpStatus.BAD_REQUEST, e );
    }

    @ExceptionHandler(AccountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity NoAccountException( AccountException e) {
        log.error( "NoAccountException", e );
        return failRequest( HttpStatus.BAD_REQUEST, e );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity UsernameNotFoundException( UsernameNotFoundException e) {
        log.error( "UsernameNotFoundException", e );
        return failRequest( HttpStatus.BAD_REQUEST, e );
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    protected ResponseEntity expiredRefreshToken( ExpiredRefreshTokenException e) {
        log.error( "expiredRefreshTokenException", e );
        return failRequest( HttpStatus.UNAUTHORIZED, e );
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected void handleException( ConstraintViolationException exception) {
        System.err.println(getResultMessage(exception.getConstraintViolations().iterator()) ); // 오류 응답을 생성
    }

    protected String getResultMessage(final Iterator<ConstraintViolation<?>> violationIterator) {
        final StringBuilder resultMessageBuilder = new StringBuilder();
        resultMessageBuilder
                .append("['");
        while (violationIterator.hasNext() == true) {
            final ConstraintViolation<?> constraintViolation = violationIterator.next();
            resultMessageBuilder.append(getPropertyName(constraintViolation.getPropertyPath().toString())) // 유효성 검사가 실패한 속성
                    .append( "{" )
                    .append("' is '")
                    .append(constraintViolation.getInvalidValue()) // 유효하지 않은 값
                    .append("'. ")
                    .append(constraintViolation.getMessage()) // 유효성 검사 실패 시 메시지
                    .append("}");
            if (violationIterator.hasNext() == true) {
                resultMessageBuilder.append(", ");
            }
        }
        resultMessageBuilder.append("]");

        return resultMessageBuilder.toString();
    }

    protected String getPropertyName(String propertyPath) {
        return propertyPath.substring(propertyPath.lastIndexOf('.') + 1); // 전체 속성 경로에서 속성 이름만 가져온다.
    }

    private ResponseEntity failRequest ( HttpStatus status, Exception e ) {
        ErrorsDto errorsDto = ErrorsDto.builder()
                .code( -1 )
                .defaultMessage( e.getMessage() )
                .build();
        if(status == HttpStatus.UNAUTHORIZED) {
            errorsDto.setCode( 401 );
        }
        return ResponseEntity.status( status ).body( errorsDto );
    }

}
