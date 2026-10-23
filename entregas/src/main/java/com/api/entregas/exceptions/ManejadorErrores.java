package com.api.entregas.exceptions;

import com.api.entregas.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorErrores {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ApiError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest req) { return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ApiError> conflicto(ReglaNegocioException ex, HttpServletRequest req) { return build(HttpStatus.CONFLICT, ex.getMessage(), req, Map.of()); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String,String> d=new LinkedHashMap<>(); ex.getBindingResult().getFieldErrors().forEach(e->d.put(e.getField(), e.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "La solicitud contiene campos inválidos", req, d);
    }
    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> solicitudInvalida(Exception ex, HttpServletRequest req) { return build(HttpStatus.BAD_REQUEST, ex.getMessage()==null?"Solicitud inválida":ex.getMessage(), req, Map.of()); }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> errorInterno(Exception ex, HttpServletRequest req) { return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Consulte los registros del servidor.", req, Map.of()); }
    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest req, Map<String,String> details) {
        return ResponseEntity.status(status).body(new ApiError(Instant.now(),status.value(),status.getReasonPhrase(),message,req.getRequestURI(),details));
    }
}
