import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Task {
    private UUID taskId;
    private UUID customerId;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;

    // Constructor
    public Task(UUID customerId, String title, String description, TaskStatus status, LocalDateTime dueDate) {
        this.taskId = UUID.randomUUID();
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getTaskId() { return taskId; }
    public UUID getCustomerId() { return customerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getDueDate() { return dueDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Validation
    public List<String> validation() {
        List<String> errors = new ArrayList<>();
        if (title == null || title.isEmpty()) errors.add("Title is required.");
        if (status == null) errors.add("Status is required.");
        if (customerId == null) errors.add("Customer ID is required.");
        if (dueDate != null && dueDate.isBefore(createdAt)) errors.add("Due date cannot be before creation date.");
        return errors;
    }

    // Helper Methods
    public void markDone() { this.status = TaskStatus.DONE; }
    public void markCancelled() { this.status = TaskStatus.CANCELLED; }

    public boolean isOverdue() {
        return dueDate != null && dueDate.isBefore(LocalDateTime.now()) && status != TaskStatus.DONE;
    }

    public String getFormattedDueDate() {
        if (dueDate == null) return "N/A";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
        return dueDate.format(formatter);
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
        return createdAt.format(formatter);
    }

    // toString
    @Override
    public String toString() {
        return "\nTask ID: " + taskId +
                "\nCustomer ID: " + customerId +
                "\nTitle: " + title +
                "\nDescription: " + description +
                "\nStatus: " + status +
                "\nDue Date: " + getFormattedDueDate() +
                "\nCreated At: " + getFormattedCreatedAt();
    }

    // equals & hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(taskId, task.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}