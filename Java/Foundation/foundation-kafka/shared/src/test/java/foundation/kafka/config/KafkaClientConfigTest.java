package foundation.kafka.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

class KafkaClientConfigTest {
    @Test
    void buildsProducerAndConsumerPropertiesForTheLocalKafkaBroker() {
        KafkaClientConfig config = KafkaClientConfig.from(Map.of(
                "KAFKA_USERNAME", "dev",
                "KAFKA_PASSWORD", "dev-secret"));

        Properties producer = config.producerProperties();
        Properties consumer = config.consumerProperties();

        assertEquals("dev-test", config.topic());
        assertEquals("dev-foundation-kafka", config.groupId());
        assertEquals("localhost:9094", producer.getProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("SASL_PLAINTEXT", producer.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        assertEquals("SCRAM-SHA-256", producer.getProperty(SaslConfigs.SASL_MECHANISM));
        assertTrue(producer.getProperty(SaslConfigs.SASL_JAAS_CONFIG).contains("username=\"dev\""));
        assertEquals("all", producer.getProperty(ProducerConfig.ACKS_CONFIG));
        assertEquals(StringSerializer.class.getName(), producer.getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        assertEquals(ByteArraySerializer.class.getName(), producer.getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
        assertEquals("dev-foundation-kafka", consumer.getProperty(ConsumerConfig.GROUP_ID_CONFIG));
        assertEquals("earliest", consumer.getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        assertEquals(Boolean.FALSE, consumer.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG));
        assertEquals(StringDeserializer.class.getName(), consumer.getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertEquals(ByteArrayDeserializer.class.getName(), consumer.getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
    }

    @Test
    void usesEnvironmentOverridesAndEscapesJaasCredentials() {
        KafkaClientConfig config = KafkaClientConfig.from(Map.of(
                "KAFKA_BOOTSTRAP_SERVERS", "broker:9094",
                "KAFKA_TOPIC", "events",
                "KAFKA_GROUP_ID", "dev-events",
                "KAFKA_USERNAME", "dev-user",
                "KAFKA_PASSWORD", "dev\"secret"));

        assertEquals("events", config.topic());
        assertEquals("dev-events", config.groupId());
        assertEquals(
                "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"dev-user\" password=\"dev\\\"secret\";",
                config.producerProperties().getProperty(SaslConfigs.SASL_JAAS_CONFIG));
    }

    @Test
    void requiresBothKafkaCredentials() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> KafkaClientConfig.from(Map.of("KAFKA_USERNAME", "dev")));

        assertTrue(exception.getMessage().contains("KAFKA_PASSWORD"));
    }
}
