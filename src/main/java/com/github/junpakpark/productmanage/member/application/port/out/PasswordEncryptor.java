package com.github.junpakpark.productmanage.member.application.port.out;

import com.github.junpakpark.productmanage.member.domain.Password;

public interface PasswordEncryptor {

    Password encode(String rawPassword);

    void validatePassword(final String rawPassword, final Password password);

}
