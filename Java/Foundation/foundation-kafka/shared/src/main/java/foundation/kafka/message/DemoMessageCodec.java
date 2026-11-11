package foundation.kafka.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;

public final class DemoMessageCodec {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Schema AVRO_SCHEMA = loadSchema();

    public byte[] toJson(DemoMessage message) throws IOException {
        return JSON_MAPPER.writeValueAsBytes(message);
    }

    public DemoMessage fromJson(byte[] bytes) throws IOException {
        return JSON_MAPPER.readValue(bytes, DemoMessage.class);
    }

    public byte[] toAvro(DemoMessage message) throws IOException {
        GenericRecord record = new GenericData.Record(AVRO_SCHEMA);
        record.put("id", message.getId());
        record.put("text", message.getText());
        record.put("createdAtEpochMillis", message.getCreatedAtEpochMillis());

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
        new GenericDatumWriter<GenericRecord>(AVRO_SCHEMA).write(record, encoder);
        encoder.flush();
        return output.toByteArray();
    }

    public DemoMessage fromAvro(byte[] bytes) throws IOException {
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        GenericRecord record = new GenericDatumReader<GenericRecord>(AVRO_SCHEMA).read(null, decoder);
        return new DemoMessage(
                record.get("id").toString(),
                record.get("text").toString(),
                (Long) record.get("createdAtEpochMillis"));
    }

    private static Schema loadSchema() {
        try (InputStream input = DemoMessageCodec.class.getResourceAsStream("/avro/demo-message.avsc")) {
            if (input == null) {
                throw new IllegalStateException("Missing Avro schema resource: /avro/demo-message.avsc");
            }
            return new Schema.Parser().parse(input);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the bundled Avro schema", exception);
        }
    }
}
