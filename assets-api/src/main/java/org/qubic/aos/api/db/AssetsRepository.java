package org.qubic.aos.api.db;

import org.qubic.aos.api.db.domain.Asset;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AssetsRepository extends CrudRepository<Asset, Long> {

    Optional<Asset> findByIssuerAndName(String issuer, String name);

}
