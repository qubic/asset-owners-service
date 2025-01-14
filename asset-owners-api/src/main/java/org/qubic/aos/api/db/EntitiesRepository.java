package org.qubic.aos.api.db;

import org.qubic.aos.api.db.domain.Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EntitiesRepository extends CrudRepository<Entity, Long> {

    Optional<Entity> findByIdentity(String identity);

}
