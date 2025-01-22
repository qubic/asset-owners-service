package org.qubic.as.sync.adapter.il.mapping;

import org.junit.jupiter.api.Test;
import org.qubic.as.sync.adapter.il.domain.IlTickInfo;
import org.qubic.as.sync.domain.TickInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IlCoreMapperIT {

    @Autowired
    private IlCoreMapper mapper;

    @Test
    void mapTickInfo() {
        IlTickInfo source = new IlTickInfo(129, 16394274, 16000000);
        TickInfo target = mapper.map(source);
        assertThat(target).isEqualTo(new TickInfo(129, 16394274, 16000000));
    }

}