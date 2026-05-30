package com.tasktracker.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * Represents a single task in the tracker.
 * Status values: "todo", "in-progress", "done"
 */
public class Task {

    private Long id;
    private String description;
    private TaskStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Task() {}

    public Task(Long id, String description) {
        this.id          = id;
        this.description = description;
        this.status      = TaskStatus.TODO;
        this.createdAt   = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId()                       { return id; }
    public void setId(Long id)               { this.id = id; }

    public String getDescription()            { return description; }
    public void setDescription(String desc)  { this.description = desc; }

    public TaskStatus getStatus()             { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)     { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()                    { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)     { this.updatedAt = updatedAt; }

    // ── Display ───────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("[%d] %-12s %s  (created: %s)",
                id, "[" + status.getLabel() + "]", description, createdAt.toLocalDate());
    }
}
