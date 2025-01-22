package org.qubic.aos.api.controller.service;

import org.qubic.aos.api.db.AssetOwnersRepository;
import org.qubic.aos.api.db.dto.AmountPerEntityDto;

import java.util.List;

public class AssetOwnersService {

    private final AssetOwnersRepository assetOwnersRepository;

    public AssetOwnersService(AssetOwnersRepository assetOwnersRepository) {
        this.assetOwnersRepository = assetOwnersRepository;
    }

    public List<AmountPerEntityDto> getTopAssetOwners(String issuer, String assetName) {
        return assetOwnersRepository.findOwnersByAsset(issuer, assetName, 1000);
    }

}
