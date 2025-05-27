package org.example.gmaildemoproject.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.gmaildemoproject.model.LabelDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Configuration properties for Gmail label definitions.
 * This class is bound to properties under 'gmail.labels' prefix in application.yml.
 * Uses constructor binding for immutability.
 */
@ConfigurationProperties(prefix = "gmail.labels")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public class LabelDefinitionsProperties {

    // Getter for the definitions list
    List<LabelDefinition> definitions;

}