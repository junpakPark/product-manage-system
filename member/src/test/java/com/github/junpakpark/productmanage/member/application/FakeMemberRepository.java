package com.github.junpakpark.productmanage.member.application;

import com.github.junpakpark.productmanage.member.domain.Member;
import com.github.junpakpark.productmanage.member.domain.MemberRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;

public class FakeMemberRepository implements MemberRepository {

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
}
