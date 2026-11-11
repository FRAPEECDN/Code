package foundation.kafka.producer;

import foundation.kafka.config.KafkaClientConfig;
import foundation.kafka.message.DemoMessage;
import foundation.kafka.message.DemoMessageCodec;
import foundation.kafka.message.MessageType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public final class KafkaProducerApp {
    private static final DemoMessageCodec CODEC = new DemoMessageCodec();

    private KafkaProducerApp() {}

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        KafkaClientConfig config = KafkaClientConfig.fromEnvironment();

        try (Scanner input = new Scanner(System.in);
                KafkaProducer<String, byte[]> producer = new KafkaProducer<>(config.producerProperties())) {
            while (true) {
                printMenu();
                if (!input.hasNextLine()) {
                    return;
                }

                String choice = input.nextLine().trim();
                switch (choice.toLowerCase(Locale.ROOT)) {
                    case "1" -> {
                        if (!sendString(input, producer, config)) {
                            return;
                        }
                    }
                    case "2" -> {
                        if (!sendPojo(input, producer, config, MessageType.JSON)) {
                            return;
                        }
                    }
                    case "3" -> {
                        if (!sendPojo(input, producer, config, MessageType.AVRO)) {
                            return;
                        }
                    }
                    case "q" -> {
                        return;
                    }
                    default -> System.out.println("Choose 1, 2, 3, or q.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Choose a message format:");
        System.out.println("1. String");
        System.out.println("2. POJO as JSON");
        System.out.println("3. POJO as Apache Avro");
        System.out.println("q. Quit");
        System.out.print("> ");
    }

    private static boolean sendString(
            Scanner input,
            KafkaProducer<String, byte[]> producer,
            KafkaClientConfig config) throws InterruptedException, ExecutionException {
        String text = prompt(input, "String message: ");
        if (text == null) {
            return false;
        }
        send(producer, config, MessageType.STRING, text.getBytes(StandardCharsets.UTF_8));
        return true;
    }

    private static boolean sendPojo(
            Scanner input,
            KafkaProducer<String, byte[]> producer,
            KafkaClientConfig config,
            MessageType type) throws IOException, InterruptedException, ExecutionException {
        String id = prompt(input, "Message id: ");
        if (id == null) {
            return false;
        }
        String text = prompt(input, "Message text: ");
        if (text == null) {
            return false;
        }

        DemoMessage message = new DemoMessage(id, text, System.currentTimeMillis());
        byte[] value = type == MessageType.JSON ? CODEC.toJson(message) : CODEC.toAvro(message);
        send(producer, config, type, value);
        return true;
    }

    private static String prompt(Scanner input, String label) {
        System.out.print(label);
        return input.hasNextLine() ? input.nextLine() : null;
    }

    private static void send(
            KafkaProducer<String, byte[]> producer,
            KafkaClientConfig config,
            MessageType type,
            byte[] value) throws InterruptedException, ExecutionException {
        String key = UUID.randomUUID().toString();
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(config.topic(), key, value);
        record.headers().add(MessageType.HEADER_NAME, type.headerValue());
        RecordMetadata metadata = producer.send(record).get();
        System.out.printf(
                "Sent %s message to %s partition %d offset %d (key %s)%n",
                type,
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                key);
    }
}
