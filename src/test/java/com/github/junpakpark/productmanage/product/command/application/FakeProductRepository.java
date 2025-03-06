package com.github.junpakpark.productmanage.product.command.application;

import com.github.junpakpark.productmanage.product.command.domain.Product;
import com.github.junpakpark.productmanage.product.command.domain.ProductRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.test.util.ReflectionTestUtils;

class FakeProductRepository implements ProductRepository {

    private final Map<Long, Product> store = new HashMap<>();
    private long sequence = 1L;

    @Override
    public Product save(final Product product) {
        final Product savedProduct = new Product(
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getShippingFee(),
                product.getMemberId()
        );
        ReflectionTestUtils.setField(savedProduct, "id", sequence++);
        store.put(savedProduct.getId(), savedProduct);
        return savedProduct;
    }

    @Override
    public Product getProductById(final Long productId) {
        return Optional.ofNullable(store.get(productId))
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    @Override
    public void delete(final Product product) {
        store.remove(product.getId());
    }

    @Override
    public void deleteAllById(final Iterable<? extends Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll(final Iterable<? extends Product> entities) {
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
    public <S extends Product> S saveAndFlush(final S entity) {
        return null;
    }

    @Override
    public <S extends Product> List<S> saveAllAndFlush(final Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(final Iterable<Product> entities) {
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
    public Product getOne(final Long aLong) {
        return null;
    }

    @Override
    public Product getById(final Long aLong) {
        return null;
    }

    @Override
    public Product getReferenceById(final Long aLong) {
        return null;
    }

    @Override
    public <S extends Product> Optional<S> findOne(final Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Product> List<S> findAll(final Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Product> List<S> findAll(final Example<S> example, final Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Product> Page<S> findAll(final Example<S> example, final Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Product> long count(final Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Product> boolean exists(final Example<S> example) {
        return false;
    }

    @Override
    public <S extends Product, R> R findBy(final Example<S> example,
                                           final Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Product> List<S> saveAll(final Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Product> findById(final Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(final Long aLong) {
        return false;
    }

    @Override
    public List<Product> findAll() {
        return List.of();
    }

    @Override
    public List<Product> findAllById(final Iterable<Long> longs) {
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
    public List<Product> findAll(final Sort sort) {
        return List.of();
    }

    @Override
    public Page<Product> findAll(final Pageable pageable) {
        return null;
    }
}
