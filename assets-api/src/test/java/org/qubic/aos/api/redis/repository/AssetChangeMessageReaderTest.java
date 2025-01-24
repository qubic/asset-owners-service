package org.qubic.aos.api.redis.repository;

import org.junit.jupiter.api.Test;
import org.qubic.aos.api.AbstractSpringIntegrationTest;
import org.qubic.aos.api.util.JsonUtil;
import org.qubic.as.messages.AssetChangeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.qubic.aos.api.redis.repository.AssetChangeMessageReader.*;

class AssetChangeMessageReaderTest extends AbstractSpringIntegrationTest {

    private static final String TEST_MESSAGE = """
            {"source":"ARUQAHPSFFWKXBTDGKMKUEUQQZHBUUFHMBULUOVHZGTRCPVXMMGXRPDESVJI","destination":"MTQFDAXGPAEAEGPYAICCZDQKECBDYSPPYTJGPJKCTASIDYZJJPWWRNKEOHMF","issuer":"QCAPWMYRSHLBJHSTTZQVCIBARVOASKDENASAKNOBRGPFWWKRCUVUAXYEZVOG","assetName":"QCAP","numberOfShares":1,"transactionHash":"dvljhqahxlwmladrfrhaxiwjaxscjziagddhzqbeefrlwghqlqkrylmeclbf","tickNumber":18682590,"eventType":2}""";


    @Autowired
    private AssetChangeMessageReader reader;

    @Autowired
    private StringRedisTemplate redisStringTemplate;

    @Test
    void readFromQueue() {
        pushTestTradeIntoReceiveQueue();

        AssetChangeMessage message = reader.readFromQueue();

        assertThat(message).isEqualTo(
                new AssetChangeMessage("ARUQAHPSFFWKXBTDGKMKUEUQQZHBUUFHMBULUOVHZGTRCPVXMMGXRPDESVJI",
                        "MTQFDAXGPAEAEGPYAICCZDQKECBDYSPPYTJGPJKCTASIDYZJJPWWRNKEOHMF",
                        "QCAPWMYRSHLBJHSTTZQVCIBARVOASKDENASAKNOBRGPFWWKRCUVUAXYEZVOG",
                        "QCAP",
                        1,
                        "dvljhqahxlwmladrfrhaxiwjaxscjziagddhzqbeefrlwghqlqkrylmeclbf",
                        18682590,
                        2)
        );
        assertThat(redisStringTemplate.opsForList().size(KEY_QUEUE_RECEIVE)).isZero();
        assertThat(redisStringTemplate.opsForList().size(KEY_QUEUE_PROCESS)).isOne();
        assertThat(redisStringTemplate.opsForList().remove(KEY_QUEUE_PROCESS, 1, TEST_MESSAGE)).isOne();
    }

    @Test
    void readFromQueue_givenEmpty_thenReturnEmpty() {
        AssetChangeMessage message = reader.readFromQueue();
        assertThat(message).isNull();
        assertThat(redisStringTemplate.opsForList().size(KEY_QUEUE_PROCESS)).isZero();
    }

    @Test
    void removeFromProcessingQueue() {
        AssetChangeMessage message = reader.readFromQueue();
        assertThat(message).isNull();
        assertThat(redisStringTemplate.opsForList().size(KEY_QUEUE_PROCESS)).isZero();
    }

    @Test
    void pushIntoErrorsQueue() {
        AssetChangeMessage message = JsonUtil.fromJson(TEST_MESSAGE, AssetChangeMessage.class);
        assertThat(reader.pushIntoErrorsQueue(message)).isOne();
        assertThat(redisStringTemplate.opsForList().size(KEY_QUEUE_ERRORS)).isOne();
        assertThat(redisStringTemplate.opsForList().rightPop(KEY_QUEUE_ERRORS)).isEqualTo(TEST_MESSAGE);
    }

    private void pushTestTradeIntoReceiveQueue() {
        Long count = redisStringTemplate.opsForList().leftPush(KEY_QUEUE_RECEIVE, TEST_MESSAGE);
        assertThat(count).isOne();
    }

}