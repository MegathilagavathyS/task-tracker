package com.tasktracker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the possible states of a task.
 */
public enum TaskStatus {

    TODO("todo"),
    IN_PROGRESS("in-progress"),
    DONE("done");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    /**
     * Parse a status from a string value (case-insensitive).
     * Accepts: "todo", "in-progress", "done"
     */
    @JsonCreator
    public static TaskStatus fromString(String value) {
        for (TaskStatus s : values()) {
            if (s.label.equalsIgnoreCase(value) || s.name().equalsIgnoreCase(value)) {
                return s;
            }
        }
        throw new IllegalArgumentException(
            "Unknown status: '" + value + "'. Valid values: todo, in-progress, done");
    }
}
