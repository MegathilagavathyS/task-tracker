package com.tasktracker;

import com.tasktracker.cli.TaskCliHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Entry point for the Task Tracker CLI.
 *
 * Spring Boot's CommandLineRunner gives us access to the raw args array
 * after the application context is ready, so all beans (service, repo, etc.)
 * are fully wired before we process the command.
 */
@SpringBootApplication
public class TaskTrackerApplication implements CommandLineRunner {

    private final TaskCliHandler cliHandler;
    private final ConfigurableApplicationContext context;

    public TaskTrackerApplication(TaskCliHandler cliHandler,
                                  ConfigurableApplicationContext context) {
        this.cliHandler = cliHandler;
        this.context    = context;
    }

    public static void main(String[] args) {
        // Disable Spring Boot's startup banner and logging noise for a clean CLI
        SpringApplication app = new SpringApplication(TaskTrackerApplication.class);
        app.setLogStartupInfo(false);
        ConfigurableApplicationContext ctx = app.run(args);
        // Exit code propagation is handled inside run()
    }

    @Override
    public void run(String... args) {
        int exitCode = cliHandler.handle(args);
        // Cleanly shut down Spring context and exit with the correct code
        context.close();
        System.exit(exitCode);
    }
}
