package com.leafyjava.tutorials.tccdemo.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.leafyjava.tutorials.tccdemo.utils.jackson.Iso8601DateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    private String uri;
    @JsonSerialize(using = Iso8601DateTimeSerializer.class)
    private OffsetDateTime expireTime;
}
