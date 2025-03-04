package com.github.junpakpark.productmanage.member.adapter.in.web;

import com.github.junpakpark.productmanage.member.application.port.in.web.ChangePasswordCommand;
import com.github.junpakpark.productmanage.member.application.port.in.web.MemberUseCase;
import com.github.junpakpark.productmanage.member.application.port.in.web.RegisterMemberCommand;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberUseCase memberUseCase;

    @PostMapping
    public ResponseEntity<Void> registerMember(
            @RequestBody final RegisterMemberCommand request,
            final UriComponentsBuilder uriComponentsBuilder
    ) {
        final Long memberId = memberUseCase.register(request);
        final URI locationUri = uriComponentsBuilder.path("/members/{memberId}")
                .buildAndExpand(memberId)
                .toUri();

        return ResponseEntity.created(locationUri).build();
    }

    @PatchMapping("/{memberId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable final Long memberId,
            @RequestBody final ChangePasswordCommand request
    ) {
        memberUseCase.changePassword(memberId, request);

        return ResponseEntity.noContent().build();
    }

}
