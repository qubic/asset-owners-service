package org.qubic.aos.api.redis;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.*;
import static org.qubic.aos.api.redis.AssetCacheManager.CACHE_NAME_ASSET_OWNERS;

class AssetCacheManagerTest {

    private final CacheManager cacheManager = mock();
    private final AssetCacheManager assetCacheManager = new AssetCacheManager(cacheManager);

    @Test
    void clearAssetOwnersCache() {
        Cache cache = mock();
        when(cacheManager.getCache(CACHE_NAME_ASSET_OWNERS)).thenReturn(cache);
        assetCacheManager.clearAssetOwnersCache("issuer", "assetName");
        verify(cache).evict("issuer:assetName");
    }

}