package org.qubic.aos.api.owners;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.qubic.aos.api.db.AssetOwnersRepository;
import org.qubic.aos.api.db.AssetsDbService;
import org.qubic.aos.api.db.EntitiesDbService;
import org.qubic.aos.api.db.domain.Asset;
import org.qubic.aos.api.db.domain.AssetOwner;
import org.qubic.aos.api.db.domain.Entity;
import org.qubic.aos.api.validation.ValidationUtility;

import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferAssetsServiceTest {

    private final AssetsDbService assetsDbService = mock();
    private final EntitiesDbService entitiesDbService = mock();
    private final AssetOwnersRepository assetOwnersRepository = mock();
    private final ValidationUtility validationUtility = mock();

    private final TransferAssetsService transferAssetsService = new TransferAssetsService(assetsDbService, entitiesDbService, assetOwnersRepository, validationUtility);

    @BeforeEach
    void initMocks() {
        when(validationUtility.validateAssetName(anyString())).thenReturn(Optional.empty());
        when(validationUtility.validateAmount(any(BigInteger.class))).thenReturn(Optional.empty());
        when(validationUtility.validateIdentity(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void transfer() {
        AssetOwner fromOwner = AssetOwner.builder().id(1L).assetId(1).entityId(1).amount(BigInteger.valueOf(20)).build();
        AssetOwner toOwner = AssetOwner.builder().id(2L).assetId(1).entityId(2).amount(BigInteger.valueOf(1)).build();

        when(entitiesDbService.getOrCreateEntity("from")).thenReturn(Entity.builder().id(1L).identity("from").build());
        when(entitiesDbService.getOrCreateEntity("to")).thenReturn(Entity.builder().id(2L).identity("to").build());
        when(assetsDbService.getOrCreateAsset("issuer", "assetName")).thenReturn(Asset.builder().id(1L).issuer("issuer").name("assetName").build());
        when(assetOwnersRepository.findByAssetIdAndEntityId(1, 1)).thenReturn(Optional.of(fromOwner));
        when(assetOwnersRepository.findByAssetIdAndEntityId(1,2)).thenReturn(Optional.of(toOwner));

        transferAssetsService.transfer("from", "to", "issuer", "assetName", 10);

        assertThat(fromOwner.getAmount()).isEqualTo(BigInteger.valueOf(10));
        assertThat(toOwner.getAmount()).isEqualTo(BigInteger.valueOf(11));

        verify(assetOwnersRepository).save(fromOwner);
        verify(assetOwnersRepository).save(toOwner);

    }

    @Test
    void transfer_givenNoSharesLeft_ThenDeleteOldOwner() {
        AssetOwner fromOwner = AssetOwner.builder().id(1L).assetId(1).entityId(1).amount(BigInteger.valueOf(10)).build();
        AssetOwner toOwner = AssetOwner.builder().id(2L).assetId(1).entityId(2).amount(BigInteger.valueOf(1)).build();

        when(entitiesDbService.getOrCreateEntity("from")).thenReturn(Entity.builder().id(1L).identity("from").build());
        when(entitiesDbService.getOrCreateEntity("to")).thenReturn(Entity.builder().id(2L).identity("to").build());
        when(assetsDbService.getOrCreateAsset("issuer", "assetName")).thenReturn(Asset.builder().id(1L).issuer("issuer").name("assetName").build());
        when(assetOwnersRepository.findByAssetIdAndEntityId(1, 1)).thenReturn(Optional.of(fromOwner));
        when(assetOwnersRepository.findByAssetIdAndEntityId(1,2)).thenReturn(Optional.of(toOwner));

        transferAssetsService.transfer("from", "to", "issuer", "assetName", 10);

        assertThat(toOwner.getAmount()).isEqualTo(BigInteger.valueOf(11));
        verify(assetOwnersRepository).delete(fromOwner);
        verify(assetOwnersRepository).save(toOwner);
    }

    @Test
    void transfer_givenToFewShares_ThenDeleteOldOwner() {
        AssetOwner fromOwner = AssetOwner.builder().id(1L).assetId(1).entityId(1).amount(BigInteger.valueOf(1)).build();
        AssetOwner toOwner = AssetOwner.builder().id(2L).assetId(1).entityId(2).amount(BigInteger.valueOf(1)).build();

        when(entitiesDbService.getOrCreateEntity("from")).thenReturn(Entity.builder().id(1L).identity("from").build());
        when(entitiesDbService.getOrCreateEntity("to")).thenReturn(Entity.builder().id(2L).identity("to").build());
        when(assetsDbService.getOrCreateAsset("issuer", "assetName")).thenReturn(Asset.builder().id(1L).issuer("issuer").name("assetName").build());
        when(assetOwnersRepository.findByAssetIdAndEntityId(1, 1)).thenReturn(Optional.of(fromOwner));
        when(assetOwnersRepository.findByAssetIdAndEntityId(1,2)).thenReturn(Optional.of(toOwner));

        transferAssetsService.transfer("from", "to", "issuer", "assetName", 10);

        assertThat(toOwner.getAmount()).isEqualTo(BigInteger.valueOf(11));
        verify(assetOwnersRepository).delete(fromOwner);
        verify(assetOwnersRepository).save(toOwner);
    }

    @Test
    void transfer_givenNoSourceOwner_ThenAddShares() {
        AssetOwner toOwner = AssetOwner.builder().id(2L).assetId(1).entityId(2).amount(BigInteger.valueOf(1)).build();

        when(entitiesDbService.getOrCreateEntity("from")).thenReturn(Entity.builder().id(1L).identity("from").build());
        when(entitiesDbService.getOrCreateEntity("to")).thenReturn(Entity.builder().id(2L).identity("to").build());
        when(assetsDbService.getOrCreateAsset("issuer", "assetName")).thenReturn(Asset.builder().id(1L).issuer("issuer").name("assetName").build());
        when(assetOwnersRepository.findByAssetIdAndEntityId(1, 1)).thenReturn(Optional.empty());
        when(assetOwnersRepository.findByAssetIdAndEntityId(1,2)).thenReturn(Optional.of(toOwner));

        transferAssetsService.transfer("from", "to", "issuer", "assetName", 10);

        assertThat(toOwner.getAmount()).isEqualTo(BigInteger.valueOf(11));
        verify(assetOwnersRepository).save(toOwner);
    }

    @Test
    void transfer_givenNoDestinationOwner_ThenCreate() {
        AssetOwner fromOwner = AssetOwner.builder().id(1L).assetId(1).entityId(1).amount(BigInteger.valueOf(11)).build();

        when(entitiesDbService.getOrCreateEntity("from")).thenReturn(Entity.builder().id(1L).identity("from").build());
        when(entitiesDbService.getOrCreateEntity("to")).thenReturn(Entity.builder().id(2L).identity("to").build());
        when(assetsDbService.getOrCreateAsset("issuer", "assetName")).thenReturn(Asset.builder().id(1L).issuer("issuer").name("assetName").build());
        when(assetOwnersRepository.findByAssetIdAndEntityId(1, 1)).thenReturn(Optional.of(fromOwner));
        when(assetOwnersRepository.findByAssetIdAndEntityId(1,2)).thenReturn(Optional.empty());

        transferAssetsService.transfer("from", "to", "issuer", "assetName", 10);

        assertThat(fromOwner.getAmount()).isEqualTo(BigInteger.valueOf(1));
        verify(assetOwnersRepository).save(fromOwner);
        verify(assetOwnersRepository).save(AssetOwner.builder().assetId(1).entityId(2).amount(BigInteger.valueOf(10)).build());
    }

    @Test
    void issueAsset() {
        when(entitiesDbService.getOrCreateEntity("issuer")).thenReturn(Entity.builder()
                .id(1L)
                .identity("issuer")
                .build());
        when(assetsDbService.createAsset("issuer", "assetName")).thenReturn(Asset.builder()
                .id(1L)
                .issuer("issuer")
                .name("assetName")
                .build());

        when(assetOwnersRepository.findByAssetIdAndEntityId(1,1)).thenReturn(Optional.empty());

        transferAssetsService.issueAsset("issuer", "assetName", 12345L);
        verify(assetOwnersRepository).save(AssetOwner.builder().assetId(1).entityId(1).amount(BigInteger.valueOf(12345)).build());
    }

}