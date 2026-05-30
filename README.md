# Task Tracker CLI — Spring Boot

A fully-featured command-line task manager built with **Java 17** and **Spring Boot 3**.  
Tasks are stored in a local `tasks.json` file — no database or internet connection required.

---

## Features

| Command | What it does |
|---|---|
| `add` | Create a new task |
| `update` | Change a task's description |
| `delete` | Remove a task permanently |
| `mark-in-progress` | Set status → in-progress |
| `mark-done` | Set status → done |
| `mark-todo` | Reset status → todo |
| `list` | List all tasks |
| `list done` | List completed tasks |
| `list todo` | List pending tasks |
| `list in-progress` | List tasks currently in progress |

---

## Prerequisites

| Tool | Version |
|---|---|
| Java JDK | 17 or higher |
| Maven | 3.8 or higher |

Check your versions:
```bash
java -version
mvn -version
```

---

## Quick Start

### 1. Clone / download the project

```bash
git clone https://github.com/your-username/task-tracker-cli.git
cd task-tracker-cli
```

### 2. Build the fat JAR

```bash
mvn clean package -q
```

This produces `target/task-tracker-cli-1.0.0.jar`.

### 3. Run a command

```bash
java -jar target/task-tracker-cli-1.0.0.jar add "Buy groceries"
```

### 4. (Optional) Use the shell wrapper

```bash
chmod +x task-cli.sh
./task-cli.sh add "Buy groceries"
```

Or symlink it to your PATH:

```bash
sudo ln -s "$(pwd)/task-cli.sh" /usr/local/bin/task-cli
task-cli list
```

---

## Command Reference

```
task-cli add "<description>"
task-cli update <id> "<new description>"
task-cli delete <id>
task-cli mark-in-progress <id>
task-cli mark-done <id>
task-cli mark-todo <id>
task-cli list
task-cli list done
task-cli list todo
task-cli list in-progress
task-cli help
```

### Examples

```bash
# Add tasks
java -jar task-tracker.jar add "Write unit tests"
java -jar task-tracker.jar add "Deploy to staging"

# Update a task
java -jar task-tracker.jar update 1 "Write unit and integration tests"

# Change status
java -jar task-tracker.jar mark-in-progress 1
java -jar task-tracker.jar mark-done 1

# List tasks
java -jar task-tracker.jar list
java -jar task-tracker.jar list in-progress
java -jar task-tracker.jar list done

# Delete a task
java -jar task-tracker.jar delete 2
```

---

## Project Structure

```
task-tracker-cli/
├── pom.xml
├── task-cli.sh                          # Optional shell wrapper
├── tasks.json                           # Auto-created on first use
└── src/main/java/com/tasktracker/
    ├── TaskTrackerApplication.java      # Spring Boot entry point
    ├── cli/
    │   └── TaskCliHandler.java          # Argument parsing & output
    ├── model/
    │   ├── Task.java                    # Task entity
    │   └── TaskStatus.java              # Enum: TODO | IN_PROGRESS | DONE
    ├── repository/
    │   └── TaskRepository.java          # Read/write tasks.json via Jackson
    └── service/
        └── TaskService.java             # Business logic (CRUD + status)
```

---

## Storage Format

Tasks are saved to `tasks.json` in the working directory.  
Example file:

```json
[
  {
    "id": 1,
    "description": "Buy groceries",
    "status": "done",
    "createdAt": "2024-01-15 09:30:00",
    "updatedAt": "2024-01-15 10:00:00"
  },
  {
    "id": 2,
    "description": "Write README",
    "status": "in-progress",
    "createdAt": "2024-01-15 09:35:00",
    "updatedAt": "2024-01-15 09:35:00"
  }
]
```

You can change the storage path in `src/main/resources/application.properties`:

```properties
task.storage.file=/home/yourname/my-tasks.json
```

---

## Configuration

All settings live in `src/main/resources/application.properties`.

| Property | Default | Description |
|---|---|---|
| `task.storage.file` | `tasks.json` | Path to the JSON storage file |

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.2** — dependency injection, `CommandLineRunner`
- **Jackson** — JSON serialisation with `JavaTimeModule` for `LocalDateTime`
- **Maven** — build & dependency management

---

## Exit Codes

| Code | Meaning |
|---|---|
| `0` | Success |
| `1` | Bad arguments or task not found |

---

## License

MIT — free to use and modify.
