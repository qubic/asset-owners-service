package org.qubic.aos.api.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

import java.util.Objects;

@Slf4j
public class ApplicationCacheManager {

    private final CacheManager cacheManager;

    public static final String CACHE_NAME_ASSET_OWNERS = "cache:assetOwners";
    public static final String CACHE_NAME_GET_OR_CREATE_ASSET =  "cache:getOrCreateAsset";
    public static final String CACHE_KEY_ASSET = "#issuer + ':' + #asset";

    public ApplicationCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // TODO use and test
    public void evictCachesForAsset(String issuer, String name) {
        log.debug("Evicting cache for asset with issuer [{}] and name [{}].", issuer, name);
        Objects.requireNonNull(cacheManager.getCache(CACHE_NAME_ASSET_OWNERS)).evict(String.format("%s:%s", issuer, name));
    }

}
