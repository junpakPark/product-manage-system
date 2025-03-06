package com.github.junpakpark.productmanage.product.command.adapter.in.web;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.AuthMember;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.ProductUseCase;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.CreateProductCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.UpdateProductCommand;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;

    @PostMapping
    public ResponseEntity<Void> createProduct(
            @AuthMember final MemberInfo memberInfo,
            @Valid @RequestBody final CreateProductCommand request,
            final UriComponentsBuilder uriComponentsBuilder
    ) {
        final Long productId = productUseCase.create(memberInfo, request);
        final URI locationUri = uriComponentsBuilder.path("/products/{productId}")
                .buildAndExpand(productId)
                .toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(
            @AuthMember final MemberInfo memberInfo,
            @PathVariable final Long productId,
            @Valid @RequestBody final UpdateProductCommand request
    ) {
        productUseCase.update(memberInfo, productId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @AuthMember final MemberInfo memberInfo,
            @PathVariable final Long productId
    ) {
        productUseCase.delete(memberInfo, productId);

        return ResponseEntity.noContent().build();
    }

}
