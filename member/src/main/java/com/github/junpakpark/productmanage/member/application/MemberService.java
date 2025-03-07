package com.github.junpakpark.productmanage.member.application;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.member.application.port.in.web.ChangePasswordCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.LoginCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.MemberUseCase;
import com.github.junpakpark.productmanage.member.application.port.in.web.RegisterMemberCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.ValidateMemberUseCase;
import com.github.junpakpark.productmanage.member.application.port.out.security.PasswordEncryptor;
import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.MemberRepository;
import com.github.junpakpark.productmanage.member.domain.Password;
import com.github.junpakpark.productmanage.member.exception.EmailConflictException;
import com.github.junpakpark.productmanage.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements MemberUseCase, ValidateMemberUseCase {

    private final MemberRepository memberRepository;
    private final PasswordEncryptor passwordEncryptor;

    @Override
    public Long register(final RegisterMemberCommand command) {
        if (memberRepository.existsByEmail(command.email())) {
            throw new EmailConflictException(MemberErrorCode.EMAIL_CONFLICT);
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

    @Override
    @Transactional(readOnly = true)
    public MemberInfo validateMember(final LoginCommand command) {
        final Member member = memberRepository.getMemberByEmail(command.email());
        passwordEncryptor.validatePassword(command.password(), member.getPassword());

        return new MemberInfo(member.getId(), member.getRole());
    }

}
