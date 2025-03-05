package com.github.junpakpark.productmanage.member.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.common.domain.Role;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberTest {

    private Member sut;

    @BeforeEach
    void setUp() {
        sut = new Member(
                "박준현",
                "junpak.park@gmail.com",
                new Password("encrypted-password"),
                Role.SELLER
        );
    }

    @Test
    @DisplayName("정상적으로 회원을 생성할 수 있다.")
    void createMember() {
        // Arrange
        final String name = "박준현";
        final String email = "junpak.park@gmail.com";
        final Password password = new Password("encrypted-password");
        final Role role = Role.SELLER;

        // Action
        final Member member = new Member(name, email, password, role);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(member.getName()).isEqualTo(name);
            assertThat(member.getEmail()).isEqualTo(email);
            assertThat(member.getPassword()).isEqualTo(password);
            assertThat(member.getRole()).isEqualTo(role);
        });
    }

    @Test
    @DisplayName("비밀번호를 변경할 수 있다.")
    void changePassword() {
        // Arrange
        final Password newPassword = new Password("new-encrypted-password");

        // Action
        sut.changePassword(newPassword);

        // Assert
        assertThat(sut.getPassword()).isEqualTo(newPassword);
    }
}
