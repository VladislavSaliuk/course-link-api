package com.courselink.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private int statusCode;

    private String message;
    public ApiError(String message) {
        this.message = message;
    }

}
