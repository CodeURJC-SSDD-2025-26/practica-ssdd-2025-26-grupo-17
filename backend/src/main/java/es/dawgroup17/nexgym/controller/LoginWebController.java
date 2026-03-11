package es.dawgroup17.nexgym.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.service.EmailService;
import es.dawgroup17.nexgym.service.UserService;

@Controller
public class LoginWebController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@GetMapping("/login")
	public String login(
			@RequestParam(name = "error", required = false) String error,
			@RequestParam(name = "emailError", required = false) String emailError,
			Model model) {
		model.addAttribute("cssLogin", true);
		if (error != null) {
			model.addAttribute("loginError", true);
		}
		if (emailError != null) {
			model.addAttribute("errorEmail", true);
		}
		return "login";
	}

	@GetMapping("/loginerror")
	public String loginerror(Model model) {
		model.addAttribute("cssError", true);
		return "redirect:/login?error=true";
	}

	// The postMapping is in the UserController, because it needs to use the
	// userService to check if the email is already used and to register the user
	@GetMapping("/register")
	public String registered(Model model) {
		model.addAttribute("cssLogin", true);
		model.addAttribute("registered", true);
		return "login";
	}

	@PostMapping("/register")
	public String register(AppUser user, @RequestParam String password, Model model) {
		if (userService.findByEmail(user.getEmail()).isPresent()) {
			return "redirect:/login?mode=register&emailError=true";
		}
		user.setRoles("USER");
		try {
			userService.registerUser(user, password);
		} catch (Exception e) {
			model.addAttribute("error", "Registration failed");
			return "redirect:/login?mode=register&error=true";
		}

		emailService.sendWelcomeEmail(user.getEmail(), user.getName());
		return "redirect:/login";
	}

}
