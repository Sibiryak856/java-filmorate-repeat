package ru.yandex.practicum.filmorate.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Getter
public class ApiError {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @JsonIgnore
    private List<StackTraceElement> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;

    public ApiError(Exception e, String message, String reason, HttpStatus status) {
        this.errors = Arrays.asList(e.getStackTrace());
        this.message = message;
        this.reason = reason;
        this.status = status.getReasonPhrase().toUpperCase();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    public ApiError(Exception e, HttpStatus status) {
        this.errors = Arrays.asList(e.getStackTrace());
        this.message = stackTraceToString(e);
        this.reason = "Unexpected error occurred";
        this.status = status.getReasonPhrase().toUpperCase();
        this.timestamp = LocalDateTime.now().format(FORMATTER);
    }

    private String stackTraceToString(Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

}
