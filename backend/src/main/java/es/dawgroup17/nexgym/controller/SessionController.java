package es.dawgroup17.nexgym.controller;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.model.Session;
import es.dawgroup17.nexgym.service.EmailService;
import es.dawgroup17.nexgym.service.SessionService;
import es.dawgroup17.nexgym.service.UserService;


@Controller
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/session/{sessionId}/signup")
    public String signUp(@PathVariable long sessionId, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Session> sessionOpt = sessionService.findById(sessionId);
        Optional<AppUser> userOpt = userService.findByEmail(principal.getName());

        if (sessionOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/error";
        }

        Session session = sessionOpt.get();
        AppUser user = userOpt.get();

        // Check if already signed up
        if (user.getUserSessions().contains(session)) {
            return "redirect:/class/" + session.getGymClass().getId();
        }

        // Check capacity
        if (session.getJoined() >= session.getCapacity()) {
            return "redirect:/class/" + session.getGymClass().getId();
        }

        // Check time overlap: user already has a session at the same date+time
        boolean hasConflict = user.getUserSessions().stream()
            .anyMatch(us -> us.getDate().equals(session.getDate()));
        if (hasConflict) {
            return "redirect:/class/" + session.getGymClass().getId();
        }

        // Sign up: link user <-> session
        user.setSession(session);
        session.increaseJoined();

        userService.save(user);
        sessionService.save(session);


        try {
            emailService.sendConfirmationSignUpEmail(
                    user.getEmail(),
                    user.getName(),
                    session.getGymClass().getName(),
                    session.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")));
        } catch (Exception e) {
            // Email failure should not block the signup
        }

        return "redirect:/class/" + session.getGymClass().getId();
    }

    @PostMapping("/session/{sessionId}/cancel")
    public String cancelSession(@PathVariable long sessionId, @RequestParam(defaultValue = "class") String from, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        Optional<Session> sessionOpt = sessionService.findById(sessionId);
        Optional<AppUser> userOpt = userService.findByEmail(principal.getName());

        if (sessionOpt.isEmpty() || userOpt.isEmpty()) {
            return "redirect:/error";
        }

        Session session = sessionOpt.get();
        AppUser user = userOpt.get();

        // Check if user is signed up for the session
        if (!user.getUserSessions().contains(session)) {
            return "redirect:/class/" + session.getGymClass().getId();
        }

        // Cancel the session: unlink user <-> session
        user.removeSession(session);
        session.decreaseJoined();

        userService.save(user);
        sessionService.save(session);

        if ("profile".equals(from)) {
            return "redirect:/profile";
        }
        return "redirect:/class/" + session.getGymClass().getId();
    }
    
}