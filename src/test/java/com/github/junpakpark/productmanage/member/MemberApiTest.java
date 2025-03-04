package com.github.junpakpark.productmanage.member;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.junpakpark.productmanage.ApiTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class MemberApiTest extends ApiTest {

    @Test
    @DisplayName("회원 등록 성공 시 201과 Location 헤더 반환")
    void registerMember() {
        // Arrange
        final var request = MemberSteps.멤버가입요청_생성();

        // Action
        final var response = MemberSteps.멤버가입요청(request);
        
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.header("Location")).isEqualTo(String.format("http://localhost:%d/members/1", getPort()));
        });
    }

    @Test
    @DisplayName("비밀번호 변경 성공 시 204 반환")
    void changePassword_success() {
        // Arrange
        MemberSteps.멤버가입요청(MemberSteps.멤버가입요청_생성());
        final var request = MemberSteps.비밀번호변경요청_생성();

        // Action
        final var response = MemberSteps.비밀번호변경요청(request);

        // Assert
        assertThat(response.statusCode()).isEqualTo(204);
    }

}
