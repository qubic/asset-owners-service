package org.qubic.aos.api.redis;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.*;
import static org.qubic.aos.api.redis.AssetsCacheManager.CACHE_NAME_ASSET_OWNERS;

class AssetsCacheManagerTest {

    private final CacheManager cacheManager = mock();
    private final AssetsCacheManager assetsCacheManager = new AssetsCacheManager(cacheManager);

    @Test
    void clearAssetOwnersCacheForAsset() {
        Cache cache = mock();
        when(cache.getName()).thenReturn("cache-name");
        when(cacheManager.getCache(CACHE_NAME_ASSET_OWNERS)).thenReturn(cache);
        assetsCacheManager.clearAssetOwnersCacheForAsset("issuer", "assetName");
        verify(cache).evict("issuer:assetName");
    }

    @Test
    void clearAssetOwnersCacheForAllAssets() {
        Cache cache = mock();
        when(cache.getName()).thenReturn("cache-name");
        when(cacheManager.getCache(CACHE_NAME_ASSET_OWNERS)).thenReturn(cache);
        assetsCacheManager.clearAssetOwnersCacheForAllAssets();
        verify(cache).clear();
    }

}