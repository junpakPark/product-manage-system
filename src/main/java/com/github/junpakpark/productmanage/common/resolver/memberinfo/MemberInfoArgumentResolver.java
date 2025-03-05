package com.github.junpakpark.productmanage.common.resolver.memberinfo;

import com.github.junpakpark.productmanage.common.security.util.AuthorizationHeaderExtractor;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberInfoArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenValidator tokenValidator;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        final boolean hasMemberInfoAnnotation = parameter.hasParameterAnnotation(AuthMember.class);
        final boolean hasMemberInfoDto = MemberInfo.class.isAssignableFrom(parameter.getParameterType());

        return hasMemberInfoAnnotation && hasMemberInfoDto;
    }

    @Override
    public Object resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) {
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        final String token = AuthorizationHeaderExtractor.extractToken(request);

        return tokenValidator.parseToken(token);
    }
}
