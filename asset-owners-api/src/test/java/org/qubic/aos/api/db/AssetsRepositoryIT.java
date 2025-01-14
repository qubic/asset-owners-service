package org.qubic.aos.api.db;

import org.junit.jupiter.api.Test;
import org.qubic.aos.api.db.domain.Asset;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class AssetsRepositoryIT extends AbstractPostgresJdbcTest {

    @Autowired
    private AssetsRepository repository;

    @Test
    public void saveAndLoad() {
        Asset asset = Asset.builder()
                .issuer("FOO")
                .name("BAR")
                .build();

        Asset saved = repository.save(asset);
        assertThat(saved.getId()).isNotNull();

        Asset reloaded = repository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded).isEqualTo(saved);
    }

    @Test
    public void findByIssuerAndName() {
        assertThat(repository.findByIssuerAndName("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFXIB", "QX")).isNotEmpty();
        assertThat(repository.findByIssuerAndName("FOO", "BAR")).isEmpty();

    }

}
