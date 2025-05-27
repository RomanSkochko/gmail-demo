package org.example.gmaildemoproject.model;

/**
 * A record representing the definition of a Gmail label,
 * including its name and desired background color.
 */
public record LabelDefinition (String name,
                               String color) {}
