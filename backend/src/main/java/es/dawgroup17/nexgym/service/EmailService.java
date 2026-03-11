package es.dawgroup17.nexgym.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail, String userName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to NexGym!");
        message.setText("Hi " + userName + "!\n\nYour account has been created successfully.\n\nSee you at the gym!");
        mailSender.send(message);
    }

    public void sendConfirmationSignUpEmail(String toEmail, String userName, String gymClass, String date) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Signed up to " + gymClass + " class!");
        message.setText("Hi " + userName + "!\n\nYou have signed up correctly to the " + gymClass
                + ".\n\nSee you at the gym! on " + date);
        mailSender.send(message);
    }
}
