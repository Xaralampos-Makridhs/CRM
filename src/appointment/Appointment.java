import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Appointment {

    // Attributes
    private UUID appointmentId;
    private UUID customerId;
    private String title;
    private String description;
    private LocalDateTime appointmentDate;
    private String location;
    private AppointmentStatus status;
    private LocalDateTime createdAt;

    // Constructor
    public Appointment(UUID customerId, String title, String description, LocalDateTime appointmentDate,
                       String location, AppointmentStatus status) {
        this.appointmentId = UUID.randomUUID();
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.appointmentDate = appointmentDate;
        this.location = location;
        this.status = status != null ? status : AppointmentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public UUID getAppointmentId(){return appointmentId;}
    public UUID getCustomerId() { return customerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public String getLocation() { return location; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setAppointmentId(UUID appointmentId){this.appointmentId=appointmentId;}
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
    public void setLocation(String location) { this.location = location; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt=createdAt;}

    // Validation
    public List<String> validation() {
        List<String> errors = new ArrayList<>();

        if (customerId == null) {
            errors.add("Customer ID is required.");
        }
        if (title == null || title.isEmpty()) {
            errors.add("Title is required.");
        }
        if (appointmentDate == null || appointmentDate.isBefore(LocalDateTime.now())) {
            errors.add("Appointment date must be in the future.");
        }
        if (description != null && description.length() > 250) {
            errors.add("Description cannot exceed 250 characters.");
        }
        if (status == null) {
            errors.add("Status is required.");
        }

        return errors;
    }

    // Helper Methods
    public void markDone() { this.status = AppointmentStatus.DONE; }
    public void markCancelled() { this.status = AppointmentStatus.CANCELLED; }
    public boolean isUpcoming() { return appointmentDate != null && appointmentDate.isAfter(LocalDateTime.now()); }

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
        return createdAt.format(formatter);
    }

    public String getFormattedAppointmentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
        return appointmentDate != null ? appointmentDate.format(formatter) : "N/A";
    }

    // toString
    @Override
    public String toString() {
        return "\nID: " + appointmentId +
                "\nCustomer ID: " + customerId +
                "\nTitle: " + title +
                "\nDescription: " + description +
                "\nAppointment Date: " + getFormattedAppointmentDate() +
                "\nLocation: " + location +
                "\nStatus: " + status +
                "\nCreated At: " + getFormattedCreatedAt();
    }

    // equals & hashCode
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Appointment that = (Appointment) obj;
        return Objects.equals(appointmentId, that.appointmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId);
    }
}
