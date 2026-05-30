package com.tasktracker.service;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;
import com.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business logic for all task operations.
 * The service loads/saves via TaskRepository on every call so that the
 * JSON file is always the single source of truth.
 */
@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Add a new task with the given description.
     * @return the created Task
     */
    public Task addTask(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Task description cannot be empty.");
        }
        List<Task> tasks = repository.loadAll();
        long newId = tasks.stream()
                .mapToLong(Task::getId)
                .max()
                .orElse(0L) + 1;

        Task task = new Task(newId, description.trim());
        tasks.add(task);
        repository.saveAll(tasks);
        return task;
    }

    // ── Update ────────────────────────────────────────────────────────────────

    /**
     * Update the description of an existing task.
     * @return the updated Task
     */
    public Task updateTask(long id, String newDescription) {
        if (newDescription == null || newDescription.isBlank()) {
            throw new IllegalArgumentException("New description cannot be empty.");
        }
        List<Task> tasks = repository.loadAll();
        Task task = findOrThrow(tasks, id);
        task.setDescription(newDescription.trim());
        task.setUpdatedAt(LocalDateTime.now());
        repository.saveAll(tasks);
        return task;
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Delete a task by ID.
     * @return the deleted Task (for confirmation output)
     */
    public Task deleteTask(long id) {
        List<Task> tasks = repository.loadAll();
        Task task = findOrThrow(tasks, id);
        tasks.remove(task);
        repository.saveAll(tasks);
        return task;
    }

    // ── Status changes ────────────────────────────────────────────────────────

    /**
     * Mark a task as in-progress.
     */
    public Task markInProgress(long id) {
        return changeStatus(id, TaskStatus.IN_PROGRESS);
    }

    /**
     * Mark a task as done.
     */
    public Task markDone(long id) {
        return changeStatus(id, TaskStatus.DONE);
    }

    /**
     * Mark a task back to todo.
     */
    public Task markTodo(long id) {
        return changeStatus(id, TaskStatus.TODO);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    /** Return all tasks sorted by ID. */
    public List<Task> listAll() {
        return sorted(repository.loadAll());
    }

    /** Return tasks whose status matches the given filter. */
    public List<Task> listByStatus(TaskStatus status) {
        return sorted(repository.loadAll().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Task changeStatus(long id, TaskStatus status) {
        List<Task> tasks = repository.loadAll();
        Task task = findOrThrow(tasks, id);
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        repository.saveAll(tasks);
        return task;
    }

    private Task findOrThrow(List<Task> tasks, long id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No task found with ID: " + id));
    }

    private List<Task> sorted(List<Task> tasks) {
        tasks.sort(Comparator.comparingLong(Task::getId));
        return tasks;
    }
}
