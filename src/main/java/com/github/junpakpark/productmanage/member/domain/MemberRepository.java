package com.github.junpakpark.productmanage.member.domain;

import com.github.junpakpark.productmanage.member.exception.MemberErrorCode;
import com.github.junpakpark.productmanage.member.exception.MemberNotFoundException;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(final String email);

    Optional<Member> findByEmail(final String email);

    default Member getMemberById(final Long id) {
        return findById(id).orElseThrow(NoSuchElementException::new);
    }

    default Member getMemberByEmail(final String email) {
        return findByEmail(email).orElseThrow(() -> new MemberNotFoundException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

}
