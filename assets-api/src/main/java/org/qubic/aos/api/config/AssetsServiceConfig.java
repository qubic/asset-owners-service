package org.qubic.aos.api.config;

import at.qubic.api.crypto.IdentityUtil;
import at.qubic.api.crypto.NoCrypto;
import lombok.extern.slf4j.Slf4j;
import org.qubic.aos.api.controller.service.*;
import org.qubic.aos.api.db.*;
import org.qubic.aos.api.validation.IdentityValidator;
import org.qubic.aos.api.validation.ValidationUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AssetsServiceConfig {

    @Bean
    IdentityUtil identityUtil() {
        return new IdentityUtil(true, new NoCrypto()); // use no crypto to avoid shared lib dependency
    }

    @Bean
    ValidationUtility validationUtility(IdentityUtil identityUtil) {
        return new ValidationUtility(identityUtil);
    }

    @Bean
    IdentityValidator identityValidator() {
        return new IdentityValidator(identityUtil());
    }

    @Bean
    AssetOwnersService assetOwnersService(AssetOwnersRepository assetOwnersRepository) {
        return new AssetOwnersService(assetOwnersRepository);
    }

}
