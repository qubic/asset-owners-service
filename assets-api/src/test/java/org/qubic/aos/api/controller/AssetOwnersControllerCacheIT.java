package org.qubic.aos.api.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.qubic.aos.api.AbstractSpringIntegrationTest;
import org.qubic.aos.api.controller.service.AssetOwnersService;
import org.qubic.aos.api.redis.AssetsCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

public class AssetOwnersControllerCacheIT extends AbstractSpringIntegrationTest {

    private static final String ISSUER = "ISSUERISSUERISSUERISSUERISSUERISSUERISSUERISSUERISSUERISPXHC";
    private static final String ASSET = "TEST123";

    @MockitoBean
    private AssetOwnersService assetOwnersService;

    @Autowired
    private AssetOwnersController assetOwnersController;

    @Autowired
    private AssetsCacheManager cacheManager;

    @Test
    void getTopAssetOwners_thenHitCache() {
        assetOwnersController.getTopAssetOwners(ISSUER, ASSET);
        assetOwnersController.getTopAssetOwners(ISSUER, ASSET);
        assetOwnersController.getTopAssetOwners(ISSUER, ASSET);

        verify(assetOwnersService, times(1)).getTopAssetOwners(ISSUER, ASSET);
    }

    @Test
    void getTopAssetOwners_givenCacheEvicted_thenHitServiceAgain() {
        cacheManager.clearAssetOwnersCache(ISSUER, ASSET);
        assetOwnersController.getTopAssetOwners(ISSUER, ASSET);
        assetOwnersController.getTopAssetOwners(ISSUER, ASSET);
        cacheManager.clearAssetOwnersCache(ISSUER, ASSET);
        assetOwnersController.getTopAssetOwners(ISSUER, ASSET);

        verify(assetOwnersService, times(2)).getTopAssetOwners(ISSUER, ASSET);
    }

    @AfterEach
    void clearCache() {
        evictAllCaches();
    }

}
