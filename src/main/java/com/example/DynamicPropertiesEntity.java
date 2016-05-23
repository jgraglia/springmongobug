package com.example;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Value
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
/** I just want a simple non spring bean, no @Document or other annotation */
public class DynamicPropertiesEntity {
    private final String id;

    @Singular
    private final Map details;

    @Singular
    private final Map<String, Object> genericDetails;

}
