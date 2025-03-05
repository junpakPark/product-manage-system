package com.github.junpakpark.productmanage.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.member.application.port.in.web.ChangePasswordCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.LoginCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.RegisterMemberCommand;
import com.github.junpakpark.productmanage.member.application.port.out.security.PasswordEncryptor;
import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.MemberRepository;
import com.github.junpakpark.productmanage.member.domain.Password;
import com.github.junpakpark.productmanage.common.domain.Role;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

class MemberServiceTest {

    private MemberService sut;
    private MemberRepository memberRepository;
    private RegisterMemberCommand registerCommand;

    @BeforeEach
    void setUp() {
        memberRepository = getFakeMemberRepository();
        sut = new MemberService(memberRepository, getFakePasswordEncryptor());

        // Arrange
        registerCommand = new RegisterMemberCommand(
                "박준현",
                "junpak.park@gmail.com",
                "비밀번호",
                Role.SELLER
        );
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
                assertThat(member.getPassword().getPassword()).isEqualTo("encoded-비밀번호");
                assertThat(member.getRole()).isEqualTo(Role.SELLER);
            });

        }

        @Test
        @DisplayName("중복된 이메일이면 예외가 발생한다.")
        void fail() {
            // Arrange
            sut.register(registerCommand);
            final RegisterMemberCommand duplicateCommand = new RegisterMemberCommand(
                    "박준현",
                    "junpak.park@gmail.com",
                    "new-password",
                    Role.BUYER);

            // Action & Assert
            assertThatThrownBy(() -> sut.register(duplicateCommand))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Email already exists");
        }

    }

    @Nested
    @DisplayName("비밀번호 변경 시,")
    class ChangePasswordTest {

        private ChangePasswordCommand command;

        @BeforeEach
        void setUp() {
            command = new ChangePasswordCommand("new-password");
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
            final LoginCommand command = new LoginCommand(registerCommand.email(), registerCommand.password());

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
                    .isInstanceOf(NoSuchElementException.class);
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

    private PasswordEncryptor getFakePasswordEncryptor() {
        return new PasswordEncryptor() {
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
        };
    }

    private MemberRepository getFakeMemberRepository() {
        return new MemberRepository() {

            private final Map<Long, Member> store = new HashMap<>();
            private final AtomicLong sequence = new AtomicLong(1L);

            @Override
            public boolean existsByEmail(String email) {
                return store.values().stream()
                        .anyMatch(member -> member.getEmail().equals(email));
            }

            @Override
            public Optional<Member> findByEmail(final String email) {
                return store.values().stream()
                        .filter(member -> member.getEmail().equals(email))
                        .findFirst();
            }

            @Override
            public Member save(Member member) {
                if (member.getId() == null) {
                    Long id = sequence.getAndIncrement();
                    Member savedMember = new Member(
                            member.getName(),
                            member.getEmail(),
                            member.getPassword(),
                            member.getRole()
                    );
                    setId(savedMember, id);
                    store.put(id, savedMember);
                    return savedMember;
                } else {
                    store.put(member.getId(), member);
                    return member;
                }
            }

            @Override
            public Member getMemberById(Long memberId) {
                if (!store.containsKey(memberId)) {
                    throw new NoSuchElementException("Member not found");
                }
                return store.get(memberId);
            }

            @Override
            public List<Member> findAll(final Sort sort) {
                return List.of();
            }

            @Override
            public Page<Member> findAll(final Pageable pageable) {
                return null;
            }

            @Override
            public <S extends Member> List<S> saveAll(final Iterable<S> entities) {
                return List.of();
            }

            @Override
            public Optional<Member> findById(final Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(final Long aLong) {
                return false;
            }

            @Override
            public List<Member> findAll() {
                return List.of();
            }

            @Override
            public List<Member> findAllById(final Iterable<Long> longs) {
                return List.of();
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(final Long aLong) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void delete(final Member entity) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void deleteAllById(final Iterable<? extends Long> longs) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void deleteAll(final Iterable<? extends Member> entities) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void deleteAll() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void flush() {
                throw new UnsupportedOperationException();
            }

            @Override
            public <S extends Member> S saveAndFlush(final S entity) {
                return null;
            }

            @Override
            public <S extends Member> List<S> saveAllAndFlush(final Iterable<S> entities) {
                return List.of();
            }

            @Override
            public void deleteAllInBatch(final Iterable<Member> entities) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void deleteAllByIdInBatch(final Iterable<Long> longs) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void deleteAllInBatch() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Member getOne(final Long aLong) {
                return null;
            }

            @Override
            public Member getById(final Long aLong) {
                return null;
            }

            @Override
            public Member getReferenceById(final Long aLong) {
                return null;
            }

            @Override
            public <S extends Member> Optional<S> findOne(final Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends Member> List<S> findAll(final Example<S> example) {
                return List.of();
            }

            @Override
            public <S extends Member> List<S> findAll(final Example<S> example, final Sort sort) {
                return List.of();
            }

            @Override
            public <S extends Member> Page<S> findAll(final Example<S> example, final Pageable pageable) {
                return null;
            }

            @Override
            public <S extends Member> long count(final Example<S> example) {
                return 0;
            }

            @Override
            public <S extends Member> boolean exists(final Example<S> example) {
                return false;
            }

            @Override
            public <S extends Member, R> R findBy(final Example<S> example,
                                                  final Function<FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }

            private void setId(Member member, Long id) {
                try {
                    var field = Member.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(member, id);
                } catch (Exception e) {
                    throw new IllegalStateException("Failed to set id", e);
                }
            }
        };
    }

}
