package foundation.kafka.consumer;

import foundation.kafka.config.KafkaClientConfig;
import foundation.kafka.message.DemoMessage;
import foundation.kafka.message.DemoMessageCodec;
import foundation.kafka.message.MessageType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

public final class KafkaConsumerApp {
    private static final Duration POLL_TIMEOUT = Duration.ofSeconds(1);
    private static final DemoMessageCodec CODEC = new DemoMessageCodec();

    private KafkaConsumerApp() {}

    public static void main(String[] args) throws IOException {
        KafkaClientConfig config = KafkaClientConfig.fromEnvironment();
        AtomicBoolean stopping = new AtomicBoolean();

        try (KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(config.consumerProperties())) {
            Thread shutdownHook = new Thread(() -> {
                stopping.set(true);
                consumer.wakeup();
            }, "kafka-consumer-shutdown");
            Runtime.getRuntime().addShutdownHook(shutdownHook);
            consumer.subscribe(List.of(config.topic()));
            System.out.printf("Consuming topic %s as group %s. Enter q to quit, or press Ctrl+C.%n",
                    config.topic(), config.groupId());
            Thread inputThread = new Thread(() -> waitForQuit(consumer, stopping), "kafka-consumer-input");
            inputThread.setDaemon(true);
            inputThread.start();

            try {
                while (!stopping.get()) {
                    ConsumerRecords<String, byte[]> records = consumer.poll(POLL_TIMEOUT);
                    for (ConsumerRecord<String, byte[]> record : records) {
                        printRecord(record);
                    }
                    if (!records.isEmpty()) {
                        consumer.commitSync();
                    }
                }
            } catch (WakeupException exception) {
                if (!stopping.get()) {
                    throw exception;
                }
            }
        }
    }

    private static void waitForQuit(KafkaConsumer<String, byte[]> consumer, AtomicBoolean stopping) {
        Scanner input = new Scanner(System.in);
        while (!stopping.get() && input.hasNextLine()) {
            if ("q".equalsIgnoreCase(input.nextLine().trim())) {
                stopping.set(true);
                consumer.wakeup();
                return;
            }
        }
        if (stopping.compareAndSet(false, true)) {
            consumer.wakeup();
        }
    }

    private static void printRecord(ConsumerRecord<String, byte[]> record) throws IOException {
        MessageType type = MessageType.fromHeaders(record.headers());
        String location = String.format(
                "topic=%s partition=%d offset=%d key=%s",
                record.topic(),
                record.partition(),
                record.offset(),
                record.key());

        switch (type) {
            case STRING -> System.out.printf(
                    "Received STRING %s value=%s%n",
                    location,
                    new String(record.value(), StandardCharsets.UTF_8));
            case JSON -> {
                DemoMessage message = CODEC.fromJson(record.value());
                System.out.printf("Received JSON %s value=%s%n", location, message);
            }
            case AVRO -> {
                DemoMessage message = CODEC.fromAvro(record.value());
                System.out.printf("Received AVRO %s value=%s%n", location, message);
            }
        }
    }
}
