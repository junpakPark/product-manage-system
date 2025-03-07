package com.github.junpakpark.productmanage.common.config;

import com.github.junpakpark.productmanage.common.infrastructure.audit.JwtAuditAware;
import com.github.junpakpark.productmanage.common.security.application.port.out.token.TokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<Long> auditorProvider(final TokenValidator tokenValidator) {
        return new JwtAuditAware(tokenValidator);
    }

}
