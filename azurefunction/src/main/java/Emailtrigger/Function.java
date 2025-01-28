package Emailtrigger;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Session;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        //final String query = request.getQueryParameters().get("name");
       // final String name = request.getBody().orElse(query);
        // Parse the incoming request body
        String requestBody = request.getBody().orElse("");
        Map<String, String> formData = parseFormData(requestBody);

        String email = formData.get("email");
        String name = formData.get("name");
        String message = formData.get("message");

        if (email == null || name == null || message == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid input: name, email, and message are required.")
                    .build();
        }

        // Send email
        try {
            sendEmail(email, name);
        } catch (Exception e) {
            context.getLogger().severe("Failed to send email: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage())
                    .build();
        }

        return request.createResponseBuilder(HttpStatus.OK)
                .body("Confirmation email sent successfully!")
                .build();
    }

    private void sendEmail(String toEmail, String name) {
        // Configure the JavaMailSender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com"); // Replace with your SMTP host
        mailSender.setPort(587);               // Replace with your SMTP port
        mailSender.setUsername("sahilcapgi@gmail.com"); // Replace with your email
        mailSender.setPassword("Sahilcapgi@001");          // Replace with your email password

        // Email properties
        Properties props = mailSender.getJavaMailProperties();
		 props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        // Create email content
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Thank you for contacting us!");
        message.setText("Hello " + name + ",\n\nThank you for reaching out. We will get back to you soon.\n\nBest regards,\nYour Company");

        // Send email
        mailSender.send(message);
    }

    private Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
        return map;
    
    }
}
