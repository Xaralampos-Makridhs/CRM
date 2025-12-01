import java.util.UUID;

public class CallCommunication extends Communication {

    private int callDurationSeconds;
    private CallOutcome outcome;

    public CallCommunication(UUID customerId, String subject, String message,
                             int callDurationSeconds, CallOutcome outcome) {
        super(customerId, subject, message, CommunicationType.CALL);
        this.callDurationSeconds = callDurationSeconds;
        this.outcome = outcome;
    }

    public int getCallDurationSeconds() { return callDurationSeconds; }
    public CallOutcome getOutcome() { return outcome; }

    public void setCallDurationSeconds(int seconds) { this.callDurationSeconds = seconds; }
    public void setOutcome(CallOutcome outcome) { this.outcome = outcome; }

    @Override
    public String toString() {
        return super.toString() +
                "\nCall Duration: " + callDurationSeconds + " sec" +
                "\nOutcome: " + outcome;
    }
}
