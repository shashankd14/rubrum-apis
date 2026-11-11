package com.steel.product.trading.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

public final class TradingErrors {
    private static final Logger log = LogManager.getLogger(TradingErrors.class);

    private TradingErrors() {
    }

    public static ResponseEntity<Object> failure(String action, Throwable error) {
        return failure(action, error, null);
    }

    public static ResponseEntity<Object> failure(String action, Throwable error, String context) {
        String where = context == null ? "" : " (" + context + ")";
        if (error instanceof ResponseStatusException) {
            ResponseStatusException status = (ResponseStatusException) error;
            String message = status.getReason() != null ? status.getReason() : "Could not " + action + ".";
            if (status.getStatus().is5xxServerError()) return unexpected(action, error, where);
            log.warn("Could not {}{}: {}", action, where, message);
            return body(status.getStatus(), message, null);
        }
        if (error instanceof IllegalArgumentException) {
            String message = error.getMessage() != null && !error.getMessage().trim().isEmpty()
                    ? error.getMessage() : "Please check the entered details and try again.";
            log.warn("Could not {}{}: {}", action, where, message);
            return body(HttpStatus.BAD_REQUEST, message, null);
        }
        if (error instanceof DataIntegrityViolationException) {
            Throwable cause = ((DataIntegrityViolationException) error).getMostSpecificCause();
            log.warn("Could not {}{}: {}", action, where, cause.getMessage());
            return body(HttpStatus.CONFLICT, "Could not " + action
                    + " because it conflicts with existing data (a duplicate value, or a record that is still in use).", null);
        }
        return unexpected(action, error, where);
    }

    public static ResponseEntity<Object> body(HttpStatus status, String message, String reference) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", "fail");
        body.put("message", message);
        if (reference != null) body.put("ref", reference);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    private static ResponseEntity<Object> unexpected(String action, Throwable error, String where) {
        String reference = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        log.error("Could not {}{} [ref={}]", action, where, reference, error);
        return body(HttpStatus.BAD_REQUEST, "Could not " + action
                + " due to an unexpected error. Please try again; if it keeps happening, share reference " + reference + " with support.", reference);
    }
}
