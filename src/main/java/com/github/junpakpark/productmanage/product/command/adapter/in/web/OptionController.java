package com.github.junpakpark.productmanage.product.command.adapter.in.web;

import com.github.junpakpark.productmanage.common.resolver.memberinfo.AuthMember;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionCommand;
import com.github.junpakpark.productmanage.product.command.application.port.in.web.OptionUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productId}/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionUseCase optionUseCase;


    @PostMapping
    public ResponseEntity<Void> addOption(
            @AuthMember final MemberInfo memberInfo,
            @PathVariable final Long productId,
            @Valid @RequestBody final OptionCommand request
    ) {
        optionUseCase.addOption(memberInfo, productId, request);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{optionId}")
    public ResponseEntity<Void> updateOption(
            @AuthMember final MemberInfo memberInfo,
            @PathVariable final Long productId,
            @PathVariable final Long optionId,
            @Valid @RequestBody final OptionCommand request
    ) {
        optionUseCase.updateOption(memberInfo, productId, optionId, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @AuthMember final MemberInfo memberInfo,
            @PathVariable final Long productId,
            @PathVariable final Long optionId
    ) {
        optionUseCase.deleteOption(memberInfo, productId, optionId);

        return ResponseEntity.noContent().build();
    }

}
