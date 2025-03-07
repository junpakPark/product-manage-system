package com.github.junpakpark.productmanage.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.domain.Role;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.member.AuthSteps;
import com.github.junpakpark.productmanage.member.MemberSteps;
import com.github.junpakpark.productmanage.member.application.port.in.web.ChangePasswordCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.LoginCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.RegisterMemberCommand;
import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.MemberRepository;
import com.github.junpakpark.productmanage.member.exception.EmailConflictException;
import com.github.junpakpark.productmanage.member.exception.MemberErrorCode;
import com.github.junpakpark.productmanage.member.exception.MemberNotFoundException;
import java.util.NoSuchElementException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class MemberServiceTest {

    private MemberService sut;
    private MemberRepository memberRepository;
    private RegisterMemberCommand registerCommand;

    @BeforeEach
    void setUp() {
        memberRepository = new FakeMemberRepository();
        sut = new MemberService(memberRepository, new FakePasswordEncryptor());

        // Arrange
        registerCommand = MemberSteps.멤버가입요청_생성();
    }

    @Nested
    @DisplayName("회원 등록 시,")
    class RegisterTest {

        @Test
        @DisplayName("중복되지 않은 이메일이면 성공한다.")
        void success() {
            // Action
            final Long memberId = sut.register(registerCommand);

            // Assert
            final Member member = memberRepository.getMemberById(memberId);
            SoftAssertions.assertSoftly(softly -> {
                assertThat(member).isNotNull();
                assertThat(member.getName()).isEqualTo("박준현");
                assertThat(member.getEmail()).isEqualTo("junpak.park@gmail.com");
                assertThat(member.getPassword().getPassword()).isEqualTo("encoded-password");
                assertThat(member.getRole()).isEqualTo(Role.SELLER);
            });

        }

        @Test
        @DisplayName("중복된 이메일이면 예외가 발생한다.")
        void fail() {
            // Arrange
            sut.register(registerCommand);
            final RegisterMemberCommand duplicateCommand = MemberSteps.멤버가입요청_생성();

            // Action & Assert
            assertThatThrownBy(() -> sut.register(duplicateCommand))
                    .isInstanceOf(EmailConflictException.class)
                    .hasMessage(MemberErrorCode.EMAIL_CONFLICT.getMessage());
        }

    }

    @Nested
    @DisplayName("비밀번호 변경 시,")
    class ChangePasswordTest {

        private ChangePasswordCommand command;

        @BeforeEach
        void setUp() {
            command = MemberSteps.비밀번호변경요청_생성();
        }

        @Test
        @DisplayName("비밀번호 변경 시 암호화 후 저장된다.")
        void success() {
            // Arrange
            final Long memberId = sut.register(registerCommand);

            // Action
            sut.changePassword(memberId, command);

            // Assert
            final Member member = memberRepository.getMemberById(memberId);
            assertThat(member.getPassword().getPassword()).isEqualTo("encoded-new-password");
        }

        @Test
        @DisplayName("존재하지 않는 회원의 비밀번호 변경 시 예외가 발생한다.")
        void fail() {
            // Arrange
            Long nonExistentMemberId = 999L;

            // Action
            // Assert
            assertThatThrownBy(() -> sut.changePassword(nonExistentMemberId, command))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Member not found");
        }
    }

    @Nested
    @DisplayName("로그인 시,")
    class ValidateMemberTest {

        @Test
        @DisplayName("이메일과 비밀번호가 일치하면 MemberInfo를 반환한다.")
        void success() {
            // Arrange
            sut.register(registerCommand);
            final LoginCommand command = AuthSteps.로그인요청_생성();

            // Action
            final MemberInfo memberInfo = sut.validateMember(command);

            // Assert
            SoftAssertions.assertSoftly(softly -> {
                assertThat(memberInfo.memberId()).isNotNull();
                assertThat(memberInfo.role()).isEqualTo(Role.SELLER);
            });
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 예외가 발생한다.")
        void emailNotFound() {
            // Arrange
            final LoginCommand command = new LoginCommand("non-existent@gmail.com", "비밀번호");

            // Action & Assert
            assertThatThrownBy(() -> sut.validateMember(command))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않으면 예외가 발생한다.")
        void invalidPassword() {
            // Arrange
            sut.register(registerCommand);
            final LoginCommand command = new LoginCommand(registerCommand.email(), "wrong-password");

            // Action
            // Assert
            assertThatThrownBy(() -> sut.validateMember(command))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Invalid password");
        }
    }

}
