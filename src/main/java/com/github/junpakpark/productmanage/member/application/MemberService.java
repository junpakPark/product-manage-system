package com.github.junpakpark.productmanage.member.application;

import com.github.junpakpark.productmanage.member.application.port.in.ChangePasswordCommand;
import com.github.junpakpark.productmanage.member.application.port.in.MemberUseCase;
import com.github.junpakpark.productmanage.member.application.port.in.RegisterMemberCommand;
import com.github.junpakpark.productmanage.member.application.port.out.PasswordEncryptor;
import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.MemberRepository;
import com.github.junpakpark.productmanage.member.domain.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements MemberUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Override
    public Long register(final RegisterMemberCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        final Password password = passwordEncryptor.encode(command.password());
        final Member member = memberRepository.save(command.toMember(password));

        return member.getId();
    }

    @Override
    public void changePassword(final Long memberId, final ChangePasswordCommand command) {
        final Member member = memberRepository.getMemberById(memberId);
        final Password password = passwordEncryptor.encode(command.password());
        member.changePassword(password);
    }

}
