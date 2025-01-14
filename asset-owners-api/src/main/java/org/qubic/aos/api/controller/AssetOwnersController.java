package org.qubic.aos.api.controller;

import org.qubic.aos.api.controller.service.AssetOwnersService;
import org.qubic.aos.api.db.dto.AmountPerEntityDto;
import org.qubic.aos.api.validation.AssetName;
import org.qubic.aos.api.validation.Identity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.qubic.aos.api.redis.ApplicationCacheManager.CACHE_KEY_ASSET;
import static org.qubic.aos.api.redis.ApplicationCacheManager.CACHE_NAME_ASSET_OWNERS;

@CrossOrigin
@Validated
@RestController
@RequestMapping("/service/v1")
public class AssetOwnersController {

    private final AssetOwnersService assetOwnersService;

    public AssetOwnersController(AssetOwnersService assetOwnersService) {
        this.assetOwnersService = assetOwnersService;
    }

    @Cacheable(cacheNames = CACHE_NAME_ASSET_OWNERS, key = CACHE_KEY_ASSET)
    @GetMapping("/issuers/{issuer}/assets/{asset}/owners")
    public List<AmountPerEntityDto> getTopAssetOwners(@PathVariable("issuer") @Identity String issuer,
                                                      @PathVariable("asset") @AssetName String asset) {
        return assetOwnersService.getTopAssetOwners(issuer, asset);
    }

}
