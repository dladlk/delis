package dk.erst.delis.handler;

import dk.erst.delis.exception.model.ErrorResponseModel;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = RestUnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorized(RestUnauthorizedException error) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestBadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(RestBadRequestException error) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(RestForbiddenException error) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestConflictException.class)
    public ResponseEntity<Object> handleConflict(RestConflictException error) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestNotFoundException.class)
    public ResponseEntity<Object> handleNotFound(RestNotFoundException error){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestUnProcessableEntityException.class)
    public ResponseEntity<Object> handleUnProcessableEntity(RestUnProcessableEntityException error){
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestFailedDependencyException.class)
    public ResponseEntity<Object> handleFailedDependency(RestFailedDependencyException error){
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(value = RestInternalServerErrorException.class)
    public ResponseEntity<Object> handleInternalServerError(RestInternalServerErrorException error){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseModel(error));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> processBadRequestValidationError(ConstraintViolationException ex) {
        List<FieldErrorModel> errorModels = new ArrayList<>();
        ex.getConstraintViolations().forEach(constraint -> errorModels.add(new FieldErrorModel(
                constraint.getInvalidValue().toString(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                constraint.getMessage())));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseModel(errorModels));
    }
}
