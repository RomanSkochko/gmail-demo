package org.example.gmaildemoproject.config;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.LabelColor;
import com.google.api.services.gmail.model.ListLabelsResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.gmaildemoproject.model.LabelDefinition;
import org.example.gmaildemoproject.properties.LabelDefinitionsProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Configuration component responsible for checking and creating Gmail labels on application startup.
 * It receives label definitions from the injected LabelDefinitionsProperties.
 */
@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class GmailLabelsConfig {

    Gmail gmail;
    LabelDefinitionsProperties labelDefinitionsProperties;
    private static final String USER_ID = "me";

    /**
     * Initializes labels in Gmail. This method is executed after the Spring application context has been initialized.
     * It checks for the existence of defined labels and creates them if they do not already exist.
     */
    @EventListener(ContextRefreshedEvent.class)
    public void createLabels() {
        List<LabelDefinition> definitions = labelDefinitionsProperties.getDefinitions(); // Get definitions from injected object

        if (definitions == null || definitions.isEmpty()) {
            log.info("No label definitions found in configuration. Skipping label creation.");
            return;
        }

        try {
            log.info("Checking and creating Gmail labels...");

            // Fetch existing labels from Gmail
            ListLabelsResponse response = gmail.users().labels().list(USER_ID).execute();
            List<String> existingLabelNames = response.getLabels() != null ? response.getLabels().stream()
                                                                                                    .map(Label::getName)
                                                                                                    .toList() :
                                                                                Collections.emptyList();

            // Iterate through defined labels and create if not existing
            for (LabelDefinition labelDef : definitions) {
                String labelName = labelDef.name();
                String labelColor = labelDef.color();

                if (!existingLabelNames.contains(labelName)) {
                    createLabel(labelName, labelColor);
                } else {
                    log.info("Label '{}' already exists. Skipping creation.", labelName);
                }
            }
            log.info("Gmail label check and creation complete.");

        } catch (IOException e) {
            log.error("Error creating Gmail labels: {}", e.getMessage(), e);
            //throw new RuntimeException("Failed to create Gmail labels", e); //Make the app crash if error occurs during creating labels
        }
    }

    /**
     * Creates a new label in Gmail with the specified name and color.
     *
     * @param labelName The name of the label to create.
     * @param color The hexadecimal color code for the label's background (e.g., "#RRGGBB").
     * @throws IOException If an I/O error occurs during the API call.
     */
    private void createLabel(String labelName, String color) throws IOException {
        Label newLabel = new Label()
                .setName(labelName)
                .setLabelListVisibility("labelShow")
                .setMessageListVisibility("show")
                .setColor(new LabelColor().setBackgroundColor(color).setTextColor("#000000"));

        Label createdLabel = gmail.users().labels().create(USER_ID, newLabel).execute();
        log.info("Created new label: '{}' with ID: {}", createdLabel.getName(), createdLabel.getId());
    }
}