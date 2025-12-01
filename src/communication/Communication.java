import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class Communication {

    // Attributes (common for ALL communication types)
    private UUID communicationId;
    private UUID customerId;
    private String subject;
    private String message;
    private CommunicationType type;
    private LocalDateTime createdAt;

    // Constructor
    public Communication(UUID customerId, String subject, String message, CommunicationType type) {
        this.communicationId = UUID.randomUUID();
        this.customerId = customerId;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getCommunicationId() { return communicationId; }
    public UUID getCustomerId() { return customerId; }
    public String getSubject() { return subject; }
    public String getMessage() { return message; }
    public CommunicationType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setCommunicationId(UUID communicationId){this.communicationId=communicationId;}
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setMessage(String message) { this.message = message; }
    public void setType(CommunicationType type) { this.type = type; }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    // Validation
    public List<String> validation() {
        List<String> errors = new ArrayList<>();

        if (customerId == null)
            errors.add("Customer ID is required.");

        if (subject == null || subject.isEmpty())
            errors.add("Subject is required.");
        else if (subject.length() > 150)
            errors.add("Subject cannot exceed 150 characters.");

        if (message != null && message.length() > 500)
            errors.add("Message cannot exceed 500 characters.");

        if (type == null)
            errors.add("Communication type is required.");

        return errors;
    }

    // Helper methods
    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
        return createdAt.format(formatter);
    }

    @Override
    public String toString() {
        return "\nCommunication ID: " + communicationId +
                "\nCustomer ID: " + customerId +
                "\nType: " + type +
                "\nSubject: " + subject +
                "\nMessage: " + message +
                "\nCreated At: " + getFormattedCreatedAt();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Communication that = (Communication) obj;
        return Objects.equals(communicationId, that.communicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(communicationId);
    }
}
