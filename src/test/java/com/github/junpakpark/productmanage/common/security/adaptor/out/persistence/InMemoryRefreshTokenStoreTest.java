package com.github.junpakpark.productmanage.common.security.adaptor.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.domain.Role;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryRefreshTokenStoreTest {

    private InMemoryRefreshTokenStore sut;

    @BeforeEach
    void setUp() {
        sut = new InMemoryRefreshTokenStore(
                Caffeine.newBuilder()
                        .expireAfterWrite(14, TimeUnit.DAYS)
                        .maximumSize(50_000)
                        .build()
        );
    }

    @Test
    @DisplayName("리프레시 토큰을 저장하고 조회할 수 있다.")
    void save() {
        // Arrange
        final String refreshToken = "sample-refresh-token";
        final MemberInfo memberInfo = new MemberInfo(1L, Role.SELLER);

        // Action
        sut.save(refreshToken, memberInfo);
        final MemberInfo foundMemberInfo = sut.findByRefreshToken(refreshToken);

        // Assert
        assertThat(foundMemberInfo).isEqualTo(memberInfo);
    }

    @Test
    @DisplayName("리프레시 토큰을 삭제하면 조회 시 null을 반환한다.")
    void remove() {
        // Arrange
        final String refreshToken = "sample-refresh-token";
        final MemberInfo memberInfo = new MemberInfo(1L, Role.ADMIN);
        sut.save(refreshToken, memberInfo);

        // Act
        sut.remove(refreshToken);
        final MemberInfo foundMemberInfo = sut.findByRefreshToken(refreshToken);

        // Assert
        assertThat(foundMemberInfo).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 리프레시 토큰 조회 시 null을 반환한다.")
    void findByRefreshToken() {
        // Arrange
        final String invalidRefreshToken = "non-existent-token";

        // Action
        final MemberInfo foundMemberInfo = sut.findByRefreshToken(invalidRefreshToken);

        // Assert
        assertThat(foundMemberInfo).isNull();
    }
}
