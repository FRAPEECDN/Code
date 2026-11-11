package foundation.kafka.message;

public final class DemoMessage {
    private String id;
    private String text;
    private long createdAtEpochMillis;

    public DemoMessage() {}

    public DemoMessage(String id, String text, long createdAtEpochMillis) {
        this.id = id;
        this.text = text;
        this.createdAtEpochMillis = createdAtEpochMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getCreatedAtEpochMillis() {
        return createdAtEpochMillis;
    }

    public void setCreatedAtEpochMillis(long createdAtEpochMillis) {
        this.createdAtEpochMillis = createdAtEpochMillis;
    }

    @Override
    public String toString() {
        return "DemoMessage{id='%s', text='%s', createdAtEpochMillis=%d}"
                .formatted(id, text, createdAtEpochMillis);
    }
}
