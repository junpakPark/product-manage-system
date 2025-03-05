package com.github.junpakpark.productmanage.member.application.port.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginCommand(
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 8, max = 100)
        String password
) {
}
