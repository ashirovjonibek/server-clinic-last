package uz.napa.clinic.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uz.napa.clinic.exception.BadRequestException;
import uz.napa.clinic.exception.ForbiddenException;
import uz.napa.clinic.exception.UnauthorizedException;
import uz.napa.clinic.payload.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandlerController {


//      * HUQUQI YO'Q YO'LGA MUROJAAT QILGANDA
//     * @param ex
//     * @return

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<?> handleInvalidInputException(ForbiddenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /* TIZIMGA AUTORIZATSIYADAN O'TMAGAN HOLATDA MUROJAAT QILGANDA
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    /* SERVERGA BOG'LIQ HAR QANDAY XATO BO'LGANDA
     * @paramex
     * @return*/
    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<?> handleException(Exception ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * REQUEST VALIDATSIYADAN O'TA OLMAGAN HOLATDA
     *
     * @param ex
     * @return
     */

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * FOYDALANUVCHI TOMONIDAN XATO SODIR BO'LGANDA
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<?> handleException(BadRequestException ex) {
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
