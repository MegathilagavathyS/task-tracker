package com.tasktracker.cli;

import com.tasktracker.model.Task;
import com.tasktracker.model.TaskStatus;
import com.tasktracker.service.TaskService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Parses command-line arguments and dispatches to TaskService.
 *
 * Usage:
 *   task-cli add "Buy groceries"
 *   task-cli update 1 "Buy groceries and cook dinner"
 *   task-cli delete 1
 *   task-cli mark-in-progress 1
 *   task-cli mark-done 1
 *   task-cli mark-todo 1
 *   task-cli list
 *   task-cli list done
 *   task-cli list todo
 *   task-cli list in-progress
 */
@Component
public class TaskCliHandler {

    // ANSI colour codes for a nicer terminal experience
    private static final String RESET   = "\u001B[0m";
    private static final String GREEN   = "\u001B[32m";
    private static final String YELLOW  = "\u001B[33m";
    private static final String CYAN    = "\u001B[36m";
    private static final String RED     = "\u001B[31m";
    private static final String BOLD    = "\u001B[1m";

    private final TaskService taskService;

    public TaskCliHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Main entry point. Returns an exit code (0 = success, 1 = error).
     */
    public int handle(String[] args) {
        if (args.length == 0) {
            printHelp();
            return 0;
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "add"              -> handleAdd(args);
                case "update"          -> handleUpdate(args);
                case "delete"          -> handleDelete(args);
                case "mark-in-progress"-> handleMarkInProgress(args);
                case "mark-done"       -> handleMarkDone(args);
                case "mark-todo"       -> handleMarkTodo(args);
                case "list"            -> handleList(args);
                case "help", "--help", "-h" -> printHelp();
                default -> {
                    error("Unknown command: '" + command + "'. Run with 'help' to see available commands.");
                    return 1;
                }
            }
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
            return 1;
        } catch (Exception e) {
            error("Unexpected error: " + e.getMessage());
            return 1;
        }

        return 0;
    }

    // ── Command handlers ──────────────────────────────────────────────────────

    private void handleAdd(String[] args) {
        requireArgs(args, 2, "Usage: add \"<description>\"");
        String description = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
        Task task = taskService.addTask(description);
        success("Task added successfully (ID: " + task.getId() + ")");
        printTask(task);
    }

    private void handleUpdate(String[] args) {
        requireArgs(args, 3, "Usage: update <id> \"<new description>\"");
        long id = parseId(args[1]);
        String newDesc = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
        Task task = taskService.updateTask(id, newDesc);
        success("Task " + id + " updated.");
        printTask(task);
    }

    private void handleDelete(String[] args) {
        requireArgs(args, 2, "Usage: delete <id>");
        long id = parseId(args[1]);
        Task task = taskService.deleteTask(id);
        success("Task " + id + " deleted: \"" + task.getDescription() + "\"");
    }

    private void handleMarkInProgress(String[] args) {
        requireArgs(args, 2, "Usage: mark-in-progress <id>");
        long id = parseId(args[1]);
        Task task = taskService.markInProgress(id);
        success("Task " + id + " marked as in-progress.");
        printTask(task);
    }

    private void handleMarkDone(String[] args) {
        requireArgs(args, 2, "Usage: mark-done <id>");
        long id = parseId(args[1]);
        Task task = taskService.markDone(id);
        success("Task " + id + " marked as done.");
        printTask(task);
    }

    private void handleMarkTodo(String[] args) {
        requireArgs(args, 2, "Usage: mark-todo <id>");
        long id = parseId(args[1]);
        Task task = taskService.markTodo(id);
        success("Task " + id + " marked back to todo.");
        printTask(task);
    }

    private void handleList(String[] args) {
        List<Task> tasks;
        String filter = "all";

        if (args.length >= 2) {
            filter = args[1].toLowerCase();
            tasks = switch (filter) {
                case "done"        -> taskService.listByStatus(TaskStatus.DONE);
                case "todo"        -> taskService.listByStatus(TaskStatus.TODO);
                case "in-progress" -> taskService.listByStatus(TaskStatus.IN_PROGRESS);
                default -> throw new IllegalArgumentException(
                    "Unknown filter: '" + filter + "'. Valid: done | todo | in-progress");
            };
        } else {
            tasks = taskService.listAll();
        }

        if (tasks.isEmpty()) {
            info("No tasks found" + (filter.equals("all") ? "." : " with status: " + filter + "."));
            return;
        }

        System.out.println();
        System.out.println(BOLD + CYAN + "  Tasks (" + filter + ")" + RESET);
        System.out.println(CYAN + "  " + "─".repeat(60) + RESET);
        for (Task t : tasks) {
            printTask(t);
        }
        System.out.println(CYAN + "  " + "─".repeat(60) + RESET);
        System.out.println(CYAN + "  Total: " + tasks.size() + " task(s)" + RESET);
        System.out.println();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void printTask(Task task) {
        String statusColour = switch (task.getStatus()) {
            case DONE        -> GREEN;
            case IN_PROGRESS -> YELLOW;
            case TODO        -> CYAN;
        };
        System.out.printf("  %s[%d]%s %-14s %s%n",
                BOLD, task.getId(), RESET,
                statusColour + "[" + task.getStatus().getLabel() + "]" + RESET,
                task.getDescription());
    }

    private long parseId(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID must be a number, got: '" + value + "'");
        }
    }

    private void requireArgs(String[] args, int min, String usage) {
        if (args.length < min) {
            throw new IllegalArgumentException("Not enough arguments. " + usage);
        }
    }

    private void success(String msg) {
        System.out.println(GREEN + "✔ " + msg + RESET);
    }

    private void info(String msg) {
        System.out.println(CYAN + "ℹ " + msg + RESET);
    }

    private void error(String msg) {
        System.err.println(RED + "✖ Error: " + msg + RESET);
    }

    private void printHelp() {
        System.out.println();
        System.out.println(BOLD + CYAN + "  Task Tracker CLI" + RESET);
        System.out.println(CYAN + "  " + "═".repeat(55) + RESET);
        System.out.println();
        System.out.println(BOLD + "  Commands:" + RESET);
        System.out.println("  " + GREEN + "add" + RESET + " \"<description>\"         Add a new task");
        System.out.println("  " + GREEN + "update" + RESET + " <id> \"<description>\" Update task description");
        System.out.println("  " + GREEN + "delete" + RESET + " <id>                Delete a task");
        System.out.println("  " + GREEN + "mark-in-progress" + RESET + " <id>      Mark task as in-progress");
        System.out.println("  " + GREEN + "mark-done" + RESET + " <id>             Mark task as done");
        System.out.println("  " + GREEN + "mark-todo" + RESET + " <id>             Reset task to todo");
        System.out.println("  " + GREEN + "list" + RESET + "                       List all tasks");
        System.out.println("  " + GREEN + "list done" + RESET + "                  List completed tasks");
        System.out.println("  " + GREEN + "list todo" + RESET + "                  List pending tasks");
        System.out.println("  " + GREEN + "list in-progress" + RESET + "           List in-progress tasks");
        System.out.println("  " + GREEN + "help" + RESET + "                       Show this help message");
        System.out.println();
        System.out.println(BOLD + "  Examples:" + RESET);
        System.out.println("  java -jar task-tracker.jar add \"Buy groceries\"");
        System.out.println("  java -jar task-tracker.jar mark-done 1");
        System.out.println("  java -jar task-tracker.jar list in-progress");
        System.out.println();
    }
}
