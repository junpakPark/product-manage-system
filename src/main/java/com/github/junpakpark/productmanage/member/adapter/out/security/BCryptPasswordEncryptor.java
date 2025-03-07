package com.github.junpakpark.productmanage.member.adapter.out.security;

import com.github.junpakpark.productmanage.member.application.port.out.security.PasswordEncryptor;
import com.github.junpakpark.productmanage.member.domain.Password;
import com.github.junpakpark.productmanage.member.exception.MemberErrorCode;
import com.github.junpakpark.productmanage.member.exception.PasswordUnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptPasswordEncryptor implements PasswordEncryptor {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Password encode(final String password) {
        return new Password(passwordEncoder.encode(password));
    }

    @Override
    public void validatePassword(final String rawPassword, final Password password) {
        if (!passwordEncoder.matches(rawPassword, password.getPassword())) {
            throw new PasswordUnauthorizedException(MemberErrorCode.PASSWORD_UNMATCHED);
        }
    }

}
