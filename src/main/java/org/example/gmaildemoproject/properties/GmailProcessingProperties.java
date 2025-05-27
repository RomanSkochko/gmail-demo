package org.example.gmaildemoproject.properties;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Map;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * Configuration properties for Gmail email processing.
 * This class is bound to properties under 'gmail.processing' prefix in application.yml.
 * Uses constructor binding for immutability.
 */
@ConfigurationProperties(prefix = "gmail.processing")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
public class GmailProcessingProperties {

    /**
     * The Gmail query string used to filter messages to be processed (e.g., "is:unread").
     */
    String query;

    /**
     * A map where keys are sender email addresses and values are the label names
     * to apply to messages from those senders.
     */
    Map<String, String> senderLabelMapping;

    /**
     * A boolean flag indicating whether to process and extract email attachments.
     */
    Boolean processAttachments;

    /**
     * The maximum number of messages to fetch per Gmail API list call.
     * Used for pagination.
     */
    Integer maxResults;

    // Custom constructor to provide default values for optional properties
    @ConstructorBinding
    public GmailProcessingProperties(String query, Map<String, String> senderLabelMapping, Boolean processAttachments, Integer maxResults) {
        this.query = query;
        this.senderLabelMapping = Optional.ofNullable(senderLabelMapping).orElse(Map.of()); // Default to empty map
        this.processAttachments = Optional.ofNullable(processAttachments).orElse(false); // Default to false
        this.maxResults = Optional.ofNullable(maxResults).orElse(100); // Default to 100
    }
}