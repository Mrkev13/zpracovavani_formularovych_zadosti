public class InMemoryEmailSender implements EmailSender {
    @Override
    public void sendConfirmation(FormRequest req) {
        System.out.println("Email sent to " + req.data.get("email"));
    }
}
