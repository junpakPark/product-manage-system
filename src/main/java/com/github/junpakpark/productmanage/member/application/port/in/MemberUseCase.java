package com.github.junpakpark.productmanage.member.application.port.in;

public interface MemberUseCase {

    Long register(final RegisterMemberCommand command);

    void changePassword(final Long memberId, final ChangePasswordCommand command);

}
