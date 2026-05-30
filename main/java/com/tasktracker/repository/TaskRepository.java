package com.tasktracker.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tasktracker.model.Task;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading and writing tasks to a local JSON file.
 * The file location defaults to tasks.json in the working directory,
 * but can be overridden via application.properties.
 */
@Repository
public class TaskRepository {

    private final File storageFile;
    private final ObjectMapper mapper;

    public TaskRepository(@Value("${task.storage.file:tasks.json}") String filePath) {
        this.storageFile = new File(filePath);
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Load all tasks from the JSON file.
     * Returns an empty list if the file does not yet exist.
     */
    public List<Task> loadAll() {
        if (!storageFile.exists()) {
            return new ArrayList<>();
        }
        try {
            return mapper.readValue(storageFile, new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to read tasks from " + storageFile.getAbsolutePath(), e);
        }
    }

    /**
     * Persist the full task list to the JSON file.
     */
    public void saveAll(List<Task> tasks) {
        try {
            mapper.writeValue(storageFile, tasks);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write tasks to " + storageFile.getAbsolutePath(), e);
        }
    }

    /** Returns the absolute path to the storage file (useful for feedback messages). */
    public String getStoragePath() {
        return storageFile.getAbsolutePath();
    }
}
