import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Customer {
    //Attributes

    private UUID customerId;
    private String fullName;
    private String phone;
    private String email;
    private Category category;
    private String notes;
    private LocalDateTime createdAt;

    //Constructor
    public Customer(String fullName, String phone, String email, Category category, String notes) {
        this.customerId = UUID.randomUUID();
        this.fullName = fullName;
        this.phone = phone;
        this.email = this.email;
        this.category = this.category;
        this.notes = this.notes;
        this.createdAt = LocalDateTime.now();
    }

    //Getters

    public UUID getCustomerId() {return customerId;}
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public Category getCategory() { return category; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setCustomerId(UUID customerId) {this.customerId = customerId;}
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setCategory(Category category) { this.category = category; }
    public void setNotes(String notes) { this.notes = notes; }

    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}

    public List<String> validation() {
        ArrayList<String> errors = new ArrayList<>();

        if (fullName == null || fullName.isEmpty()) {
            errors.add("Name is required.");
        }

        if (phone == null || phone.length() < 10) {
            errors.add("Phone number length must be 10 digits or phone number is required.");
        }

        if (email == null || !(email.contains("@") && email.contains("."))) {
            errors.add("Enter a valid email.");
        }

        if (category == null) {
            errors.add("Enter a valid category.");
        }

        if (notes != null && notes.length() > 250) {
            errors.add("Enter notes less than 250 chars.");
        }

        return errors;
    }



    public String getFormattedDate(){
        DateTimeFormatter formatτer=DateTimeFormatter.ofPattern("dd/MM/yyyy | HH:mm:ss");
        return createdAt.format(formatτer);
    }

    public String toString() {
        return "\nID: " + customerId +
                "\nFull Name: " + fullName +
                "\nPhone Number: " + phone +
                "\nEmail: " + email +
                "\nCategory: " + category +
                "\nNotes: "+notes+
                "\nCreated at: "+getFormattedDate();
    }

    @Override
    public boolean equals(Object obj){
        if(this==obj){return true;}
        if(obj==null || getClass()!=obj.getClass()){return false;}
        Customer customer=(Customer) obj;
        return Objects.equals(customerId,customer.customerId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(customerId);
    }

    public boolean isVip(){
        return category==Category.VIP;
    }

    public boolean isRegular(){
        return category==Category.REGULAR;
    }
    public boolean isNew(){
        return category==Category.NEW;
    }

}
