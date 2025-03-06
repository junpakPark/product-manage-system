package com.github.junpakpark.productmanage.member.application;

import com.github.junpakpark.productmanage.member.application.port.out.security.PasswordEncryptor;
import com.github.junpakpark.productmanage.member.domain.Password;

public class FakePasswordEncryptor implements PasswordEncryptor {

    @Override
    public Password encode(final String rawPassword) {
        return new Password("encoded-" + rawPassword);
    }

    @Override
    public void validatePassword(final String rawPassword, final Password password) {
        if (!password.getPassword().equals("encoded-" + rawPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }
    }

}
