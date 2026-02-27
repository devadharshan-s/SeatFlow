package org.example.bookmyshowshowservice.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;
}
