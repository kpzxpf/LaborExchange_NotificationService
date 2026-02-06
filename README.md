# 📧 LaborExchange Notification Service

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen?style=for-the-badge&logo=spring)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Consumer-black?style=for-the-badge&logo=apache-kafka)
![JavaMail](https://img.shields.io/badge/JavaMail-SMTP-blue?style=for-the-badge)

**Email Notification Service**

</div>

---

## 📋 Overview

Notification Service handles email notifications for the LaborExchange platform. It consumes events from Kafka and sends templated emails to users.

### Key Features

✅ **Event-Driven** - Consumes Kafka events  
✅ **Email Templates** - HTML email templates  
✅ **Multiple Events** - Application submitted, rejected, withdrawn  
✅ **SMTP Integration** - JavaMail with configurable SMTP  
✅ **Async Processing** - Non-blocking email sending  

## 🏗️ Architecture

**Service:** Port 8086 (stateless)  
**No Database** - Event processing only  

### System Flow

```
Kafka Events → Notification Service → SMTP Server → Email
```

## 🛠️ Technology Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.2 |
| Messaging | Apache Kafka |
| Email | Spring Mail (JavaMail) |
| Templates | Thymeleaf |

## 📨 Kafka Consumers

### Consumed Topics

1. **new-application-notification**
   - Triggered when application submitted
   - Sends confirmation email to candidate
   - Sends notification to employer

2. **rejected-application-notification**
   - Triggered when application rejected
   - Sends rejection email to candidate

3. **withdrawn-application-notification**
   - Triggered when application withdrawn
   - Sends confirmation to candidate
   - Sends notification to employer

### Consumer Implementation

```java
@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationEventConsumer {
    
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(
        topics = "new-application-notification",
        groupId = "notification-service-group"
    )
    public void handleNewApplication(String message) {
        log.info("Received new application event: {}", message);
        
        try {
            ApplicationEvent event = objectMapper.readValue(
                message, 
                ApplicationEvent.class
            );
            
            // Send email to candidate
            emailService.sendApplicationConfirmation(
                event.getCandidateEmail(),
                event.getVacancyTitle()
            );
            
            // Send email to employer
            emailService.sendNewApplicationNotification(
                event.getEmployerEmail(),
                event.getCandidateId(),
                event.getVacancyTitle()
            );
            
            log.info("Application emails sent successfully");
            
        } catch (Exception e) {
            log.error("Error processing new application event", e);
            throw new RuntimeException(e);
        }
    }
    
    @KafkaListener(
        topics = "rejected-application-notification",
        groupId = "notification-service-group"
    )
    public void handleRejectedApplication(String message) {
        log.info("Received rejection event: {}", message);
        
        try {
            ApplicationEvent event = objectMapper.readValue(
                message,
                ApplicationEvent.class
            );
            
            emailService.sendRejectionEmail(
                event.getCandidateEmail(),
                event.getVacancyTitle(),
                event.getRejectionReason()
            );
            
        } catch (Exception e) {
            log.error("Error processing rejection event", e);
        }
    }
    
    @KafkaListener(
        topics = "withdrawn-application-notification",
        groupId = "notification-service-group"
    )
    public void handleWithdrawnApplication(String message) {
        log.info("Received withdrawal event: {}", message);
        
        try {
            ApplicationEvent event = objectMapper.readValue(
                message,
                ApplicationEvent.class
            );
            
            emailService.sendWithdrawalConfirmation(
                event.getCandidateEmail(),
                event.getVacancyTitle()
            );
            
        } catch (Exception e) {
            log.error("Error processing withdrawal event", e);
        }
    }
}
```

## 📧 Email Service

### Email Templates

#### Application Confirmation

```java
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${spring.mail.from}")
    private String fromEmail;
    
    public void sendApplicationConfirmation(String to, String vacancyTitle) {
        Context context = new Context();
        context.setVariable("vacancyTitle", vacancyTitle);
        
        String htmlContent = templateEngine.process(
            "application-confirmation",
            context
        );
        
        sendEmail(
            to,
            "Application Submitted - " + vacancyTitle,
            htmlContent
        );
    }
    
    public void sendRejectionEmail(String to, String vacancyTitle, String reason) {
        Context context = new Context();
        context.setVariable("vacancyTitle", vacancyTitle);
        context.setVariable("reason", reason);
        
        String htmlContent = templateEngine.process(
            "application-rejection",
            context
        );
        
        sendEmail(
            to,
            "Application Update - " + vacancyTitle,
            htmlContent
        );
    }
    
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new EmailSendException("Failed to send email", e);
        }
    }
}
```

### Email Template Example

```html
<!-- templates/application-confirmation.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Application Confirmation</title>
    <style>
        body { font-family: Arial, sans-serif; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #4CAF50; color: white; padding: 20px; }
        .content { padding: 20px; background: #f5f5f5; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Application Submitted</h1>
        </div>
        <div class="content">
            <p>Dear Applicant,</p>
            <p>Your application for <strong th:text="${vacancyTitle}">Vacancy</strong> 
               has been successfully submitted.</p>
            <p>The employer will review your application and contact you soon.</p>
            <p>Best regards,<br/>LaborExchange Team</p>
        </div>
    </div>
</body>
</html>
```

## ⚙️ Configuration

```yaml
server:
  port: 8086

spring:
  application:
    name: notification-service
  
  # Kafka Configuration
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: notification-service-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
  
  # Email Configuration
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SMTP_USERNAME:your-email@gmail.com}
    password: ${SMTP_PASSWORD:your-app-password}
    from: ${SMTP_FROM:noreply@laborexchange.com}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
  
  # Thymeleaf Configuration
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    encoding: UTF-8

logging:
  level:
    com.vlz.laborexchange_notificationservice: INFO
    org.springframework.kafka: WARN
    org.springframework.mail: DEBUG
```

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `SMTP_HOST` | SMTP server | Yes |
| `SMTP_PORT` | SMTP port | Yes |
| `SMTP_USERNAME` | SMTP username | Yes |
| `SMTP_PASSWORD` | SMTP password | Yes |
| `SMTP_FROM` | From email address | Yes |

## 🧪 Testing

```java
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    
    @Mock private JavaMailSender mailSender;
    @Mock private TemplateEngine templateEngine;
    
    @InjectMocks
    private EmailService emailService;
    
    @Test
    void sendApplicationConfirmation_Success() throws MessagingException {
        // Arrange
        String to = "candidate@example.com";
        String vacancyTitle = "Java Developer";
        
        when(templateEngine.process(anyString(), any(Context.class)))
            .thenReturn("<html>Email content</html>");
        when(mailSender.createMimeMessage())
            .thenReturn(new MimeMessage((Session) null));
        
        // Act
        emailService.sendApplicationConfirmation(to, vacancyTitle);
        
        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }
}
```

## 📊 Monitoring

```bash
# Health check
curl http://localhost:8086/actuator/health

# Kafka consumer metrics
curl http://localhost:8086/actuator/metrics/kafka.consumer.records-consumed-total

# Email sent count
curl http://localhost:8086/actuator/metrics/email.sent
```

## 🐛 Troubleshooting

### Common Issues

**1. SMTP Authentication Failed**
```yaml
# Check credentials
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password  # NOT your account password
```

**2. Kafka Connection Failed**
```bash
# Check Kafka
docker-compose logs kafka

# Verify bootstrap servers
spring.kafka.bootstrap-servers=localhost:9092
```

**3. Template Not Found**
```bash
# Check template location
src/main/resources/templates/application-confirmation.html
```

## 🚀 Quick Start

```bash
# Set environment variables
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-app-password

# Start Kafka
docker-compose up -d kafka

# Run service
./gradlew bootRun

# Test
curl http://localhost:8086/actuator/health
```

---

<div align="center">

**Made with ❤️ by the LaborExchange Team**

</div>
