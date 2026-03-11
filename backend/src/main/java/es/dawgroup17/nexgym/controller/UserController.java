package es.dawgroup17.nexgym.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.dawgroup17.nexgym.model.AppUser;
import es.dawgroup17.nexgym.model.GymClass;
import es.dawgroup17.nexgym.model.Image;
import es.dawgroup17.nexgym.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.ObjectMapper;
import es.dawgroup17.nexgym.service.ImageService;

//faltn los import de classService y scheduleService

import es.dawgroup17.nexgym.service.ClassService;
import es.dawgroup17.nexgym.service.SessionService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ClassService classService;

	@Autowired
	private SessionService sessionService;

	@Autowired
	private ImageService imageService;

	// Is for the user info in the header, to know if there is a user logged, if the
	// user is an admin and their name
	@ModelAttribute
	public void addAttributes(Model model, HttpServletRequest request) {

		Principal principal = request.getUserPrincipal();

		if (principal != null) {

			model.addAttribute("logged", true);
			model.addAttribute("userEmail", principal.getName());
			model.addAttribute("admin", request.isUserInRole("ADMIN"));

		} else {
			model.addAttribute("logged", false);
		}
	}

	@GetMapping("/profile")
	public String profile(Model model, Principal principal) throws Exception {
		Optional<AppUser> userOpt = userService.findByEmail(principal.getName());
		if (userOpt.isPresent()) {
			AppUser user = userOpt.get();
			model.addAttribute("user", user);
			model.addAttribute("sessions", user.getUserSessions());
			model.addAttribute("suggestedClasses", classService.findAll());
			model.addAttribute("cssProfile", true);

			// Pie chart data
			Map<String, Integer> distribution = userService.getClassDistributionLast30Days(user);
			ObjectMapper mapper = new ObjectMapper();
			model.addAttribute("classDistributionJson", mapper.writeValueAsString(distribution));

			return "profile";
		} else {
			model.addAttribute("cssError", true);
			return "error";
		}
	}

	@GetMapping("/profile/profileAdmin")
	public String profileAdmin(Model model, Principal principal) {
		Optional<AppUser> userOpt = userService.findByEmail(principal.getName());
		if (userOpt.isPresent()) {
			model.addAttribute("user", userOpt.get());
			model.addAttribute("formAction", "/profile/profileAdmin");
			model.addAttribute("cancelUrl", "/profile");
			model.addAttribute("deleteAction", "/profile/delete");
			model.addAttribute("isAdmin", false);
			model.addAttribute("cssProfileAdmin", true);
			return "profileAdmin";
		} else {
			model.addAttribute("cssError", true);

			return "error";
		}
	}

	@GetMapping("/admin")
	public String adminPanel(Model model) throws Exception {
		model.addAttribute("users", userService.findAll());
		model.addAttribute("classes", classService.findAll());
		model.addAttribute("sessions", sessionService.findAll());
		model.addAttribute("cssAdmin", true);

		// Convert map to JSON string for Chart.js
		Map<String, List<Integer>> chartData = classService.getLastWeekEnrollmentsAllClasses();
		ObjectMapper mapper = new ObjectMapper();
		model.addAttribute("chartDataJson", mapper.writeValueAsString(chartData));

		return "admin";
	}

	@GetMapping("/admin/newClassForm")
	public String addNewClassPage(Model model) {
		GymClass g = new GymClass();
		model.addAttribute("name", g.getName() != null ? g.getName() : ""); // null → empty
		model.addAttribute("coachName", g.getCoachName() != null ? g.getCoachName() : ""); // null → empty
		model.addAttribute("description", g.getDescription() != null ? g.getDescription() : ""); // null → empty
		model.addAttribute("sessions", g.getSessions() != null ? g.getSessions() : new ArrayList<>());
		model.addAttribute("isEdit", false);
		model.addAttribute("formAction", "/admin/newClass");
		model.addAttribute("cssNewClass", true);
		return "newClass";
	}

	@GetMapping("/admin/newClass/{id}")
	public String addNewClass(Model model, @PathVariable long id) {
		Optional<GymClass> classOpt = classService.findById(id);
		if (classOpt.isPresent()) {
			GymClass gymClass = classOpt.get();
			model.addAttribute("name", gymClass.getName() != null ? gymClass.getName() : "");
			model.addAttribute("coachName", gymClass.getCoachName() != null ? gymClass.getCoachName() : "");
			model.addAttribute("description", gymClass.getDescription() != null ? gymClass.getDescription() : "");
			model.addAttribute("sessions", gymClass.getSessions() != null ? gymClass.getSessions() : new ArrayList<>());
			model.addAttribute("isEdit", true);
			model.addAttribute("formAction", "/admin/updateClass/" + id);
			model.addAttribute("cssNewClass", true);
			return "newClass";
		} else {
			model.addAttribute("cssError", true);

			return "error";
		}
	}

	@GetMapping("/admin/profileAdmin/{id}")
	public String adminEditProfile(Model model, @PathVariable long id) {
		Optional<AppUser> userOpt = userService.findById(id);
		if (userOpt.isPresent()) {
			model.addAttribute("user", userOpt.get());
			model.addAttribute("formAction", "/admin/profileAdmin/" + id);
			model.addAttribute("cancelUrl", "/admin");
			model.addAttribute("deleteAction", "/admin/users/" + id + "/delete");
			model.addAttribute("isAdmin", true);
			model.addAttribute("cssProfileAdmin", true);
			return "profileAdmin";
		} else {
			model.addAttribute("cssError", true);

			return "error";
		}
	}

	@GetMapping("/admin/profile/{id}")
	public String adminViewProfile(Model model, @PathVariable long id) {
		Optional<AppUser> userOpt = userService.findById(id);
		if (userOpt.isPresent()) {
			AppUser user = userOpt.get();
			model.addAttribute("user", user);
			model.addAttribute("sessions", user.getUserSessions());
			model.addAttribute("suggestedClasses", classService.findAll());
			model.addAttribute("cssProfile", true);
			return "profile";
		} else {
			model.addAttribute("cssError", true);

			return "error";
		}
	}

	@PostMapping("/profile/profileAdmin")
	public String updateProfileFromUser(@RequestParam long id, AppUser updatedUser,
			@RequestParam(required = false) String password,
			@RequestParam(required = false) MultipartFile imageFile, Principal principal, HttpServletRequest request)
			throws IOException {

		AppUser user = userService.findById(id).orElseThrow();

		user.setName(updatedUser.getName());
		user.setSurname(updatedUser.getSurname());
		user.setPhone(updatedUser.getPhone());
		user.setBirthdate(updatedUser.getBirthdate());

		// Only update email if it actually changed and is not blank
		String newEmail = updatedUser.getEmail();
		boolean emailChanged = false;
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
			user.setEmail(newEmail);
			emailChanged = true;
		}

		// If the user changes the password
		if (password != null && !password.isBlank()) {
			userService.updatePassword(user, password);
		}

		if (imageFile != null && !imageFile.isEmpty()) {
			if (user.getImage() != null) {
				imageService.replaceImageFile(user.getImage().getId(), imageFile.getInputStream());
			} else {
				Image newImage = imageService.createImage(imageFile.getInputStream());
				user.setImage(newImage);
			}
		}

		userService.save(user);
		// If email changed, update the Spring Security session with the new email
		// so the user doesn't get logged out
		if (emailChanged) {
			org.springframework.security.core.userdetails.UserDetails newUserDetails = org.springframework.security.core.userdetails.User
					.withUsername(user.getEmail())
					.password(user.getEncodedPassword())
					.roles(user.getRoles().toArray(new String[0]))
					.build();

			org.springframework.security.core.Authentication newAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
					newUserDetails, null, newUserDetails.getAuthorities());

			org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(newAuth);
		}

		return "redirect:/profile/profileAdmin";
	}

	@PostMapping("/admin/profileAdmin/{id}")
	public String adminUpdateProfile(@PathVariable long id, AppUser updatedUser,
			@RequestParam(required = false) String password, @RequestParam(required = false) MultipartFile imageFile)
			throws IOException {

		AppUser user = userService.findById(id).orElseThrow();

		user.setName(updatedUser.getName());
		user.setSurname(updatedUser.getSurname());
		user.setPhone(updatedUser.getPhone());
		user.setBirthdate(updatedUser.getBirthdate());

		// Only update email if it actually changed and is not blank
		String newEmail = updatedUser.getEmail();
		if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
			user.setEmail(newEmail);
		}

		// If the user changes the password
		if (password != null && !password.isBlank()) {
			userService.updatePassword(user, password);
		}

		if (imageFile != null && !imageFile.isEmpty()) {
			if (user.getImage() != null) {
				imageService.replaceImageFile(user.getImage().getId(), imageFile.getInputStream());
			} else {
				Image newImage = imageService.createImage(imageFile.getInputStream());
				user.setImage(newImage);
			}
		}

		userService.save(user);

		return "redirect:/admin/profileAdmin/" + user.getId();
	}

	// The user delete's his own accorunt from the profile page
	@PostMapping("/profile/delete")
	public String deleteAccount(Principal principal, HttpServletRequest request) throws Exception {
		userService.deleteByEmail(principal.getName());
		// Logout after deleting the account
		request.logout();
		return "redirect:/";
	}

	// used in the admin.html to delete a user
	@PostMapping("/admin/users/{id}/delete")
	public String deleteUserById(Model model, @PathVariable long id) {
		try {
			Optional<AppUser> user = userService.findById(id);
			if (user.isPresent()) {
				userService.delete(id);
			}
		} catch (Exception e) {
			return "error";
		}
		return "redirect:/admin";
	}

}