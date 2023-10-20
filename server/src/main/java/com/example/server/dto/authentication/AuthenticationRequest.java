package com.example.server.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    @NotNull
    @Email(message = "Invalid email format")
    private String email;
    @NotNull
    @Size(min = 8, max = 20)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).*$",
            message = "Password must contain at least one uppercase and lowercase letter")
    @Pattern(regexp = "^(?=.*\\d).*$",
            message = "Password must contain at least one number")
    @Pattern(regexp = "^[^А-Яа-яЇїІіЄєҐґЁё]+$",
            message = "Password must contain only latin letters")
    private String password;
}
