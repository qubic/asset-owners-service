package org.qubic.aos.api.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

import java.util.Objects;

@Slf4j
public class AssetCacheManager {

    private final CacheManager cacheManager;

    /**
     * This cache is used for internal db access. We don't need to clear it because assets do not get removed.
     */
    public static final String CACHE_NAME_GET_ASSET =  "cache:asset:db";

    /**
     * This cache is for api access. Needs to be cleared for one asset if the owners change.
     */
    public static final String CACHE_NAME_ASSET_OWNERS = "cache:asset-owners:api";

    /**
     * Key for an asset related cache entry.
     */
    public static final String CACHE_KEY_ASSET = "#a0 + ':' + #a1";

    public AssetCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // TODO use and test
    public void clearAssetOwnersCache(String issuer, String name) {
        log.debug("Evicting cache for asset with issuer [{}] and name [{}].", issuer, name);
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME_ASSET_OWNERS)).evict(String.format("%s:%s", issuer, name));
    }

}
