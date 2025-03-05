package com.github.junpakpark.productmanage.common.infrastructure.audit;

import com.github.junpakpark.productmanage.common.interceptor.AuthorizationHeaderExtractor;
import com.github.junpakpark.productmanage.common.resolver.memberinfo.MemberInfo;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class JwtAuditAware implements AuditorAware<Long> {

    private final TokenValidator tokenValidator;

    public JwtAuditAware(final TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                    .getRequest();
            final String token = AuthorizationHeaderExtractor.extractToken(request);
            final MemberInfo memberInfo = tokenValidator.parseToken(token);

            return Optional.of(memberInfo.memberId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
