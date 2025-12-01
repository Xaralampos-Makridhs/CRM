import java.util.UUID;

public class EmailCommunication extends Communication {

    private String recipientEmail;
    private String attachmentName;

    public EmailCommunication(UUID customerId, String subject, String message,
                              String recipientEmail, String attachmentName) {
        super(customerId, subject, message, CommunicationType.EMAIL);
        this.recipientEmail = recipientEmail;
        this.attachmentName = attachmentName;
    }

    public String getRecipientEmail() { return recipientEmail; }
    public String getAttachmentName() { return attachmentName; }

    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }

    @Override
    public String toString() {
        return super.toString() +
                "\nRecipient Email: " + recipientEmail +
                "\nAttachment: " + (attachmentName != null ? attachmentName : "None");
    }
}
