package foundation.kafka.config;

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

public final class KafkaClientConfig {
    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9094";
    private static final String DEFAULT_TOPIC = "dev-test";
    private static final String DEFAULT_GROUP_ID = "dev-foundation-kafka";

    private final String bootstrapServers;
    private final String topic;
    private final String groupId;
    private final String username;
    private final String password;

    private KafkaClientConfig(
            String bootstrapServers,
            String topic,
            String groupId,
            String username,
            String password) {
        this.bootstrapServers = bootstrapServers;
        this.topic = topic;
        this.groupId = groupId;
        this.username = username;
        this.password = password;
    }

    public static KafkaClientConfig fromEnvironment() {
        return from(System.getenv());
    }

    static KafkaClientConfig from(Map<String, String> environment) {
        return new KafkaClientConfig(
                optional(environment, "KAFKA_BOOTSTRAP_SERVERS", DEFAULT_BOOTSTRAP_SERVERS),
                optional(environment, "KAFKA_TOPIC", DEFAULT_TOPIC),
                optional(environment, "KAFKA_GROUP_ID", DEFAULT_GROUP_ID),
                required(environment, "KAFKA_USERNAME"),
                required(environment, "KAFKA_PASSWORD"));
    }

    public String topic() {
        return topic;
    }

    public String groupId() {
        return groupId;
    }

    public Properties producerProperties() {
        Properties properties = connectionProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        return properties;
    }

    public Properties consumerProperties() {
        Properties properties = connectionProperties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return properties;
    }

    private Properties connectionProperties() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        properties.put(SaslConfigs.SASL_MECHANISM, "SCRAM-SHA-256");
        properties.put(
                SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.scram.ScramLoginModule required"
                        + " username=\"" + escapeJaasValue(username) + "\""
                        + " password=\"" + escapeJaasValue(password) + "\";");
        return properties;
    }

    private static String required(Map<String, String> environment, String name) {
        String value = environment.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be set to connect to Kafka");
        }
        return value;
    }

    private static String optional(Map<String, String> environment, String name, String defaultValue) {
        String value = environment.get(name);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static String escapeJaasValue(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
