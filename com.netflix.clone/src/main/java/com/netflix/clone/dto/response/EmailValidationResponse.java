package com.netflix.clone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailValidationResponse {
    private boolean exists;
    private boolean available;
}
