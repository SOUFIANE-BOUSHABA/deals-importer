package com.bbg.fx.fx_deals_importer.common;

import com.bbg.fx.fx_deals_importer.service.DealImportService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onValidation(MethodArgumentNotValidException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getBindingResult().toString());
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onConstraintViolation(ConstraintViolationException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(DealImportService.DuplicateDealException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail onDuplicate(DealImportService.DuplicateDealException ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Duplicate deal");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler({NumberFormatException.class, java.time.format.DateTimeParseException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail onParseError(Exception ex) {
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid data format");
        pd.setDetail("Failed to parse CSV data: " + ex.getMessage());
        return pd;
    }
}