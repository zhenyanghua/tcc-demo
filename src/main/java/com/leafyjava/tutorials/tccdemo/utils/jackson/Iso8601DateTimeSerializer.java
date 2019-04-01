package com.leafyjava.tutorials.tccdemo.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class Iso8601DateTimeSerializer extends JsonSerializer<OffsetDateTime> {
    @Override
    public void serialize(final OffsetDateTime value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeString(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
