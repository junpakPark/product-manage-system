package com.github.junpakpark.productmanage.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(final String email);

    default Member getMemberById(final Long id) {
        return findById(id).orElseThrow(IllegalArgumentException::new);
    }

}
