package org.qubic.aos.api.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

@Slf4j
public class AssetsCacheManager {

    private final CacheManager cacheManager;

    /**
     * This cache is used for internal db access. We don't need to clear it because assets do not get removed.
     */
    public static final String CACHE_NAME_GET_ASSET =  "cache:as:db:asset";

    /**
     * This cache is for api access. Needs to be cleared for one asset if the owners change.
     */
    public static final String CACHE_NAME_ASSET_OWNERS = "cache:as:api:asset-owners";

    /**
     * Key for an asset related cache entry.
     */
    public static final String CACHE_KEY_ASSET = "#a0 + ':' + #a1";

    public AssetsCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearAssetOwnersCacheForAsset(String issuer, String name) {
        getCache(CACHE_NAME_ASSET_OWNERS).ifPresent(c -> {
            String key = String.format("%s:%s", issuer, name);
            log.debug("Evicting cache [{}] for key [{}].", c.getName(), key);
            c.evict(key);
        });
    }

    public void clearAssetOwnersCacheForAllAssets() {
        getCache(CACHE_NAME_ASSET_OWNERS).ifPresent(c -> {
            log.debug("Clearing cache [{}].", c.getName());
            c.clear();
        });
    }

    @SuppressWarnings("SameParameterValue")
    private Optional<Cache> getCache(String name) {
        return Optional.ofNullable(cacheManager.getCache(name));
    }

}
